package com.shinyhut.vernacular.client.rendering;

import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.client.rendering.renderers.CopyRectRenderer;
import com.shinyhut.vernacular.client.rendering.renderers.RRERenderer;
import com.shinyhut.vernacular.client.rendering.renderers.RawRenderer;
import com.shinyhut.vernacular.client.rendering.renderers.Renderer;
import com.shinyhut.vernacular.protocol.messages.Encoding;
import com.shinyhut.vernacular.protocol.messages.FramebufferUpdate;
import com.shinyhut.vernacular.protocol.messages.Rectangle;
import com.shinyhut.vernacular.protocol.messages.ServerInit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.shinyhut.vernacular.protocol.messages.Encoding.*;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.time.LocalDateTime.now;

public class Framebuffer {

    private final VncSession session;
    private final BufferedImage frame;

    private static final Map<Encoding, Renderer> RENDERERS = new ConcurrentHashMap<>();

    static {
        RENDERERS.put(RAW, new RawRenderer());
        RENDERERS.put(COPYRECT, new CopyRectRenderer());
        RENDERERS.put(RRE, new RRERenderer());
    }

    public Framebuffer(VncSession session) {
        ServerInit init = session.getServerInit();
        this.frame = new BufferedImage(init.getFrameBufferWidth(), init.getFrameBufferHeight(), TYPE_INT_RGB);
        this.frame.setAccelerationPriority(1);
        this.session = session;
    }

    public void processUpdate(FramebufferUpdate update) throws VncException {
        session.setLastFramebufferUpdateTime(now());
        for (Rectangle rectangle : update.getRectangles()) {
            RENDERERS.get(rectangle.getEncoding()).render(frame, rectangle, session.getPixelFormat());
        }
        paint();
    }

    public void paint() {
        Consumer<Image> listener = session.getConfig().getFramebufferUpdateListener();
        if (listener != null) {
            listener.accept(frame);
        }
    }

}
