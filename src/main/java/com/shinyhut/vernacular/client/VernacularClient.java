package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.handshaking.Handshaker;
import com.shinyhut.vernacular.protocol.initialization.Initializer;
import com.shinyhut.vernacular.utils.KeySyms;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static java.awt.event.KeyEvent.*;
import static java.util.stream.IntStream.range;

public class VernacularClient {

    private final Handshaker handshaker;
    private final Initializer initializer;

    private final VernacularConfig config;

    private VncSession session;
    private ClientEventHandler clientEventHandler;
    private ServerEventHandler serverEventHandler;

    private volatile boolean running;

    /**
     * Creates a new VNC client using the specified configuration object
     *
     * @param config The VNC client configuration
     */
    public VernacularClient(VernacularConfig config) {
        this.config = config;
        this.handshaker = new Handshaker();
        this.initializer = new Initializer();
    }

    /**
     * Starts the VNC client by connecting to the specified remote host and port
     *
     * @param host Remote host to connect to
     * @param port Remote port to connect to
     * @throws IllegalStateException if the client is already running
     */
    public void start(String host, int port) {
        try {
            start(new Socket(host, port));
        } catch (IOException e) {
            handleError(new UnexpectedVncException(e));
        }
    }

    /**
     * Starts the VNC client by connecting to the specified socket
     *
     * @param socket Socket to connect to
     * @throws IllegalStateException if the client is already running
     */
    public void start(Socket socket) {
        if (running) {
            throw new IllegalStateException("VNC Client is already running");
        }

        running = true;

        try {
            createSession(socket);
            clientEventHandler = new ClientEventHandler(session, this::handleError);
            serverEventHandler = new ServerEventHandler(session, this::handleError);

            serverEventHandler.start();
            clientEventHandler.start();
        } catch (IOException e) {
            handleError(new UnexpectedVncException(e));
        } catch (VncException e) {
            handleError(e);
        }
    }

    /**
     * Stops the VNC client
     */
    public void stop() {
        running = false;
        if (serverEventHandler != null) {
            serverEventHandler.stop();
        }
        if (clientEventHandler != null) {
            clientEventHandler.stop();
        }
        if (session != null) {
            session.kill();
        }
    }

    /**
     * Moves the remote mouse pointer to the specified coordinates (relative to the top-left of the screen).
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     */
    public void moveMouse(int x, int y) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.moveMouse(x, y);
            } catch (IOException e) {
                handleError(new UnexpectedVncException(e));
            }
        }
    }

    /**
     * Updates the status (pressed or not pressed) of the specified mouse button.
     * <p>
     * To indicate a mouse 'click', call this method twice in quick succession, first with pressed = true,
     * then with pressed = false, or use the convenience {@link #click(int)} method.
     * <p>
     * On a conventional mouse, buttons 1, 2, and 3 correspond to the left, middle, and right buttons on the mouse.
     * On a wheel mouse, each step of the wheel upwards is represented by a press and release of button 4, and each step
     * downwards is represented by a press and release of button 5.
     *
     * @param button  The mouse button number (1-8)
     * @param pressed Is the mouse button currently 'pressed'?
     */
    public void updateMouseButton(int button, boolean pressed) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.updateMouseButton(button, pressed);
            } catch (IOException e) {
                handleError(new UnexpectedVncException(e));
            }
        }
    }

    /**
     * 'Clicks' (presses and releases) the specified mouse button.
     * <p>
     * This is equivalent to calling {@link #updateMouseButton(int, boolean)} twice in quick succession with
     * pressed = true and pressed = false
     *
     * @param button The mouse button number (1-8)
     */
    public void click(int button) {
        updateMouseButton(button, true);
        updateMouseButton(button, false);
    }

    /**
     * Scroll up using the mouse wheel.
     * <p>
     * This is equivalent to calling {@link #click(int)} with button number 4
     */
    public void scrollUp() {
        click(4);
    }

    /**
     * Scroll down using the mouse wheel.
     * <p>
     * This is equivalent to calling {@link #click(int)} with button number 5
     */
    public void scrollDown() {
        click(5);
    }

    /**
     * Presses, releases or 'types' the key represented by the specified KeyEvent.
     * <p>
     * The event type should be one of KEY_PRESSED, KEY_RELEASED or KEY_TYPED. All other event types are ignored.
     *
     * @param event The KeyEvent to handle
     * @see java.awt.event.KeyEvent KeyEvent
     */
    public void handleKeyEvent(KeyEvent event) {
        KeySyms.forEvent(event).ifPresent(k -> {
            switch (event.getID()) {
                case KEY_PRESSED:
                case KEY_RELEASED:
                    updateKey(k, event.getID() == KEY_PRESSED);
                    break;
                case KEY_TYPED:
                    type(k);
                    break;
            }
        });
    }

    /**
     * Updates the status (pressed or not pressed) of the key represented by the specified KeySym
     * <p>
     * For a complete list of KeySyms, see https://cgit.freedesktop.org/xorg/proto/x11proto/plain/keysymdef.h
     *
     * @param keySym  The KeySym for this key press or release
     * @param pressed Was the key pressed (true) or released (false)?
     */
    public void updateKey(int keySym, boolean pressed) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.updateKey(keySym, pressed);
            } catch (IOException e) {
                handleError(new UnexpectedVncException(e));
            }
        }
    }

    /**
     * 'Types' (presses and releases) the key represented by the specified KeySym.
     * <p>
     * This is equivalent to calling {@link #updateKey(int, boolean)} twice in quick succession with
     * pressed = true and pressed = false
     *
     * @param keySym The KeySym for this key press or release
     */
    public void type(int keySym) {
        updateKey(keySym, true);
        updateKey(keySym, false);
    }

    /**
     * 'Types' (presses and releases) the key representing each character in the specified string, in order.
     * <p>
     * Note that this is only guaranteed to work as expected for strings containing only printable ASCII characters -
     * apart from line breaks, which are converted to ENTER key presses, and tabs, which are converted to TAB key
     * presses.
     *
     * @param text The text that will be typed on the remote server
     */
    public void type(String text) {
        text = text.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
        range(0, text.length())
                .map(text::charAt)
                .map(c -> {
                    switch (c) {
                        case '\n':
                            return KeySyms.forKeyCode(VK_ENTER).get();
                        case '\t':
                            return KeySyms.forKeyCode(VK_TAB).get();
                        default:
                            return c;
                    }
                })
                .forEach(this::type);
    }

    /**
     * Copies the specified text to the remote clipboard
     *
     * @param text The text to be copied to the remote clipboard
     */
    public void copyText(String text) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.copyText(text);
            } catch (IOException e) {
                handleError(new UnexpectedVncException(e));
            }
        }
    }

    /**
     * Is the client currently running?
     *
     * @return true if the client is currently running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    private void createSession(Socket socket) throws IOException, VncException {
        InputStream in = new BufferedInputStream(socket.getInputStream());
        OutputStream out = socket.getOutputStream();
        session = new VncSession(config, in, out);

        handshaker.handshake(session);
        initializer.initialise(session);
    }

    private void handleError(VncException e) {
        notifyErrorListeners(e);
        stop();
    }

    private void notifyErrorListeners(VncException e) {
        if (config.getErrorListener() != null) {
            config.getErrorListener().accept(e);
        }
    }

}
