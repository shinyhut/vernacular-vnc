package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.handshaking.Handshaker;
import com.shinyhut.vernacular.protocol.initialization.Initializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VernacularClient {

    private final Handshaker handshaker;
    private final Initializer initializer;

    private final VernacularConfig config;

    private VncSession session;
    private ClientEventHandler clientEventHandler;
    private ServerEventHandler serverEventHandler;

    private boolean running;

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
     * Updates the status (i.e. pressed or not pressed) of the specified key.
     *
     * Note: to indicate typical key press followed by an immediate release, call this method twice in rapid
     * succession, first with pressed = true, then with pressed = false
     *
     * @param keycode The keycode for the key being modified (@see java.awt.event.KeyEvent KeyEvent)
     * @param symbol If the key is 'printable', specify the intended character representation. Otherwise set to 'null'
     * @param pressed Is the key currently 'pressed'?
     */
    public void keyPress(int keycode, char symbol, boolean pressed) {
        if (clientEventHandler != null) {
            try {
                clientEventHandler.keyPress(keycode, symbol, pressed);
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
