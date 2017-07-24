package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.UnknownMessageTypeException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.client.rendering.Framebuffer;
import com.shinyhut.vernacular.protocol.messages.FramebufferUpdate;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.function.Consumer;

public class ServerEventHandler {

    private final VncSession session;
    private final Consumer<VncException> errorHandler;
    private final Framebuffer framebuffer;

    private boolean running;

    ServerEventHandler(VncSession session, Consumer<VncException> errorHandler) {
        this.session = session;
        this.errorHandler = errorHandler;
        this.framebuffer = new Framebuffer(session);
    }

    void start() {
        PushbackInputStream in = new PushbackInputStream(session.getInputStream());

        running = true;

        new Thread(() -> {
            try {
                int messageType;
                while (running && ((messageType = in.read()) != -1)) {
                    in.unread(messageType);

                    switch (messageType) {
                        case 0x00:
                            FramebufferUpdate message = FramebufferUpdate.decode(in, session.getPixelFormat().getBitsPerPixel());
                            framebuffer.processUpdate(message);
                            break;
                        default:
                            throw new UnknownMessageTypeException(messageType);
                    }

                }
            } catch (IOException e) {
                if (running) {
                    errorHandler.accept(new UnexpectedVncException(e));
                }
            } catch (VncException e ) {
                if (running) {
                    errorHandler.accept(e);
                }
            } finally {
                stop();
            }

        }).start();
    }

    void stop() {
        running = false;
    }

}
