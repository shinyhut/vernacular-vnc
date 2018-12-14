package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.handshaking.Handshaker;
import com.shinyhut.vernacular.protocol.initialization.Initializer;
import com.shinyhut.vernacular.utils.KeySyms;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;

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

        if (running) {
            throw new IllegalStateException("VNC Client is already running");
        }

        running = true;

        try {
            createSession(host, port);
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
     * Notify the remote server the client has moved the mouse to the specified co-ordinates
     *
     * @param x The X co-ordinate
     * @param y The Y co-ordinate
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
     * Updates the status (i.e. pressed or not pressed) of the specified mouse button.
     *
     * Note: to indicate a mouse 'click', call this method twice in rapid succession, first with pressed = true,
     * then with pressed = false
     *
     * @param button The mouse button number (1-3)
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
     * Updates the status (i.e. pressed or not pressed) of the key represented by the specified KeyEvent
     *
     * @param event The KeyEvent for this key press or release (@see java.awt.event.KeyEvent KeyEvent)
     */
    public void keyPress(KeyEvent event) {
        if (event.getID() == KEY_PRESSED || event.getID() == KEY_RELEASED) {
            KeySyms.forEvent(event).ifPresent(k -> keyPress(k, event.getID() == KEY_PRESSED));
        }
    }

    /**
     * Updates the status (i.e. pressed or not pressed) of the key represented by the specified KeySym
     *
     * For a complete list of KeySyms, see https://cgit.freedesktop.org/xorg/proto/x11proto/plain/keysymdef.h
     *
     * @param keySym The KeySym for this key press or release
     * @param pressed Was the key pressed (true) or released (false)?
     */
    public void keyPress(int keySym, boolean pressed) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.keyPress(keySym, pressed);
            } catch (IOException e) {
                handleError(new UnexpectedVncException(e));
            }
        }
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

    private void createSession(String host, int port) throws IOException, VncException {
        Socket socket = new Socket(host, port);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        session = new VncSession(host, port, config, in, out);

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
