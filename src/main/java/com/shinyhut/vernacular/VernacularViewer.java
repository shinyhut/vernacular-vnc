package com.shinyhut.vernacular;

import com.shinyhut.vernacular.client.VernacularClient;
import com.shinyhut.vernacular.client.VernacularConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.Optional;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_16;
import static java.awt.EventQueue.invokeLater;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.event.KeyEvent.*;
import static java.lang.Integer.parseInt;
import static java.lang.System.exit;
import static javax.swing.JOptionPane.*;

public class VernacularViewer extends JFrame {

    private VernacularConfig config;
    private VernacularClient client;

    private JMenuItem connectMenuItem;
    private JMenuItem disconnectMenuItem;

    private Image lastFrame;

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
        initialiseVernacularClient();
    }

    private void addKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (running()) {
                    client.keyPress(e.getKeyCode(), e.getKeyChar(), true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (running()) {
                    client.keyPress(e.getKeyCode(), e.getKeyChar(), false);
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

    private void initialiseVernacularClient() {
        config = new VernacularConfig();
        config.setColorDepth(BPP_16);
        config.setErrorListener(e -> {
            showMessageDialog(this, e.getMessage());
            setMenuState(false);
        });
        config.setPasswordSupplier(() ->
                Optional.ofNullable(showInputDialog(this, "Enter password")).orElse(""));

        config.setFramebufferUpdateListener(this::renderFrame);
        config.setBellListener(v -> getDefaultToolkit().beep());
        config.setServerCutTextListener(t -> getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(t), null));
        client = new VernacularClient(config);
    }

    private void addMenu() {
        JMenuBar menu = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(VK_F);

        connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.setMnemonic(VK_C);
        connectMenuItem.addActionListener((ActionEvent event) -> showConnectDialog());

        disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.setMnemonic(VK_D);
        disconnectMenuItem.setEnabled(false);
        disconnectMenuItem.addActionListener((ActionEvent event) -> disconnect());

        JMenuItem exit = new JMenuItem("Exit");
        exit.setMnemonic(VK_E);
        exit.addActionListener((ActionEvent event) -> {
            if (client != null) {
                client.stop();
            }
            exit(0);
        });

        file.add(connectMenuItem);
        file.add(disconnectMenuItem);
        file.add(exit);
        menu.add(file);
        setJMenuBar(menu);
    }

    private void showConnectDialog() {
        JPanel connectDialog = new JPanel();
        JTextField hostField = new JTextField(20);
        JTextField portField = new JTextField("5900");
        JLabel hostLabel = new JLabel("Host");
        hostLabel.setLabelFor(hostField);
        JLabel portLabel = new JLabel("Port");
        portLabel.setLabelFor(hostLabel);
        connectDialog.add(hostLabel);
        connectDialog.add(hostField);
        connectDialog.add(portLabel);
        connectDialog.add(portField);
        int choice = showConfirmDialog(VernacularViewer.this, connectDialog, "Connect", OK_CANCEL_OPTION);
        if (choice == OK_OPTION) {
            connect(hostField.getText(), portField.getText());
        }
    }

    private void connect(String host, String port) {
        setMenuState(true);
        lastFrame = null;
        client.start(host, parseInt(port));
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
        } else {
            connectMenuItem.setEnabled(true);
            disconnectMenuItem.setEnabled(false);
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
        getContentPane().getGraphics().drawImage(lastFrame, 0, 0, getContentPane().getWidth(), getContentPane().getHeight(), null);
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (lastFrame != null) {
            renderFrame(lastFrame);
        }
    }

    private boolean resizeRequired(Image frame) {
        return lastFrame == null || lastFrame.getWidth(null) != frame.getWidth(null) || lastFrame.getHeight(null) != frame.getHeight(null);
    }

    private void resizeWindow(Image frame) {
        int remoteWidth = frame.getWidth(null);
        int remoteHeight = frame.getHeight(null);
        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int maxWidth = (int) screenSize.getWidth();
        int maxHeight = (int) screenSize.getHeight();
        if (remoteWidth <= maxWidth && remoteHeight < maxHeight) {
            setWindowSize(remoteWidth, remoteHeight);
        } else {
            int scaledWidth;
            int scaledHeight;
            if (remoteWidth >= remoteHeight) {
                scaledWidth = maxWidth;
                scaledHeight = (int) (remoteHeight * ((double) maxWidth / remoteWidth));
            } else {
                scaledHeight = maxHeight;
                scaledWidth = (int) (remoteWidth * ((double) maxHeight / remoteHeight));
            }
            setWindowSize(scaledWidth, scaledHeight);
        }
        setLocation(0, 0);
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