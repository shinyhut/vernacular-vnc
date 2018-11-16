package com.shinyhut.vernacular;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_16_TRUE;
import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_8_INDEXED;
import static java.awt.BorderLayout.CENTER;
import static java.awt.EventQueue.invokeLater;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.event.KeyEvent.*;
import static java.lang.Integer.parseInt;
import static java.lang.Math.min;
import static java.lang.System.exit;
import static javax.swing.JOptionPane.*;

public class VernacularViewer extends JFrame {

    private VernacularConfig config;
    private VernacularClient client;

    private JMenuItem connectMenuItem;
    private JMenuItem disconnectMenuItem;

    private JMenuItem bpp8IndexedColorMenuItem;
    private JMenuItem bpp16TrueColorMenuItem;

    private Image lastFrame;

    private AncestorListener focusRequester = new AncestorListener() {
        @Override
        public void ancestorAdded(AncestorEvent event) {
            event.getComponent().requestFocusInWindow();
        }

        @Override
        public void ancestorRemoved(AncestorEvent event) {
        }

        @Override
        public void ancestorMoved(AncestorEvent event) {
        }
    };

    private VernacularViewer() {
        initUI();
    }

    private void initUI() {
        setTitle("Vernacular VNC");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if (client != null) {
                    client.stop();
                }
                super.windowClosing(event);
            }
        });

        addMenu();
        addMouseListeners();
        addKeyListener();
        addDrawingSurface();
        initialiseVernacularClient();
    }

    private void addKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (running()) {
                    client.keyPress(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (running()) {
                    client.keyPress(e);
                }
            }
        });
    }

    private void addMouseListeners() {
        getContentPane().addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (running()) {
                    client.moveMouse(scaleMouseX(e.getX()), scaleMouseY(e.getY()));
                }
            }
        });
        getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (running()) {
                    client.updateMouseButton(e.getButton() - 1, true);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (running()) {
                    client.updateMouseButton(e.getButton() - 1, false);
                }
            }
        });
    }

    private void addDrawingSurface() {
        add(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (lastFrame != null) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(lastFrame, 0, 0, getContentPane().getWidth(), getContentPane().getHeight(), null);
                }
            }
        }, CENTER);
    }

    private void initialiseVernacularClient() {
        config = new VernacularConfig();
        config.setColorDepth(BPP_8_INDEXED);
        config.setErrorListener(e -> {
            showMessageDialog(this, e.getMessage(), "Error", ERROR_MESSAGE);
            setMenuState(false);
        });
        config.setPasswordSupplier(this::showPasswordDialog);
        config.setFramebufferUpdateListener(this::renderFrame);
        config.setBellListener(v -> getDefaultToolkit().beep());
        config.setServerCutTextListener(t -> getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(t), null));
        client = new VernacularClient(config);
    }

    private void addMenu() {
        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(VK_F);

        JMenu options = new JMenu("Options");
        options.setMnemonic(VK_O);

        connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.setMnemonic(VK_C);
        connectMenuItem.addActionListener((ActionEvent event) -> showConnectDialog());

        disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.setMnemonic(VK_D);
        disconnectMenuItem.setEnabled(false);
        disconnectMenuItem.addActionListener((ActionEvent event) -> disconnect());

        ButtonGroup colorDepths = new ButtonGroup();

        bpp8IndexedColorMenuItem = new JRadioButtonMenuItem("8-bit Indexed Color", true);
        bpp16TrueColorMenuItem = new JRadioButtonMenuItem("16-bit True Color");
        colorDepths.add(bpp8IndexedColorMenuItem);
        colorDepths.add(bpp16TrueColorMenuItem);

        bpp8IndexedColorMenuItem.addActionListener((ActionEvent event) -> config.setColorDepth(BPP_8_INDEXED));
        bpp16TrueColorMenuItem.addActionListener((ActionEvent event) -> config.setColorDepth(BPP_16_TRUE));

        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(VK_X);
        exit.addActionListener((ActionEvent event) -> {
            if (client != null) {
                client.stop();
            }
            exit(0);
        });

        file.add(connectMenuItem);
        file.add(disconnectMenuItem);
        file.add(exit);
        options.add(bpp8IndexedColorMenuItem);
        options.add(bpp16TrueColorMenuItem);
        menu.add(file);
        menu.add(options);
        setJMenuBar(menu);
    }

    private void showConnectDialog() {
        JPanel connectDialog = new JPanel();
        JTextField hostField = new JTextField(20);
        hostField.addAncestorListener(focusRequester);
        JTextField portField = new JTextField("5900");
        JLabel hostLabel = new JLabel("Host");
        hostLabel.setLabelFor(hostField);
        JLabel portLabel = new JLabel("Port");
        portLabel.setLabelFor(hostLabel);
        connectDialog.add(hostLabel);
        connectDialog.add(hostField);
        connectDialog.add(portLabel);
        connectDialog.add(portField);
        int choice = showConfirmDialog(this, connectDialog, "Connect", OK_CANCEL_OPTION);
        if (choice == OK_OPTION) {
            String host = hostField.getText();
            if (host == null || host.isEmpty()) {
                showMessageDialog(this, "Please enter a valid host", null, WARNING_MESSAGE);
                return;
            }
            int port;
            try {
                port = parseInt(portField.getText());
            } catch (NumberFormatException e) {
                showMessageDialog(this, "Please enter a valid port", null, WARNING_MESSAGE);
                return;
            }
            connect(host, port);
        }
    }

    private String showPasswordDialog() {
        String password = "";
        JPanel passwordDialog = new JPanel();
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.addAncestorListener(focusRequester);
        passwordDialog.add(passwordField);
        int choice = showConfirmDialog(this, passwordDialog, "Enter Password", OK_CANCEL_OPTION);
        if (choice == OK_OPTION) {
            password = new String(passwordField.getPassword());
        }
        return password;
    }

    private void connect(String host, int port) {
        setMenuState(true);
        lastFrame = null;
        client.start(host, port);
    }

    private void disconnect() {
        if (running()) {
            client.stop();
        }
        setMenuState(false);
    }

    private void setMenuState(boolean running) {
        if (running) {
            connectMenuItem.setEnabled(false);
            disconnectMenuItem.setEnabled(true);
            bpp8IndexedColorMenuItem.setEnabled(false);
            bpp16TrueColorMenuItem.setEnabled(false);
        } else {
            connectMenuItem.setEnabled(true);
            disconnectMenuItem.setEnabled(false);
            bpp8IndexedColorMenuItem.setEnabled(true);
            bpp16TrueColorMenuItem.setEnabled(true);
        }
    }

    private boolean running() {
        return client != null && client.isRunning();
    }

    private void renderFrame(Image frame) {
        if (resizeRequired(frame)) {
            resizeWindow(frame);
        }
        lastFrame = frame;
        repaint();
    }

    private boolean resizeRequired(Image frame) {
        return lastFrame == null || lastFrame.getWidth(null) != frame.getWidth(null) || lastFrame.getHeight(null) != frame.getHeight(null);
    }

    private void resizeWindow(Image frame) {
        int remoteWidth = frame.getWidth(null);
        int remoteHeight = frame.getHeight(null);
        Rectangle screenSize = getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int paddingTop = getHeight() - getContentPane().getHeight();
        int paddingSides = getWidth() - getContentPane().getWidth();
        int maxWidth = (int) screenSize.getWidth() - paddingSides;
        int maxHeight = (int) screenSize.getHeight() - paddingTop;
        if (remoteWidth <= maxWidth && remoteHeight < maxHeight) {
            setWindowSize(remoteWidth, remoteHeight);
        } else {
            double scale = min((double) maxWidth / remoteWidth, (double) maxHeight / remoteHeight);
            int scaledWidth = (int) (remoteWidth * scale);
            int scaledHeight = (int) (remoteHeight * scale);
            setWindowSize(scaledWidth, scaledHeight);
        }
        setLocationRelativeTo(null);
    }

    private void setWindowSize(int width, int height) {
        getContentPane().setPreferredSize(new Dimension(width, height));
        pack();
    }

    private int scaleMouseX(int x) {
        if (lastFrame == null) {
            return x;
        }
        return (int) (x * ((double) lastFrame.getWidth(null) / getContentPane().getWidth()));
    }

    private int scaleMouseY(int y) {
        if (lastFrame == null) {
            return y;
        }
        return (int) (y * ((double) lastFrame.getHeight(null) / getContentPane().getHeight()));
    }

    public static void main(String[] args) {
        invokeLater(() -> {
            VernacularViewer viewer = new VernacularViewer();
            viewer.setVisible(true);
        });
    }

}