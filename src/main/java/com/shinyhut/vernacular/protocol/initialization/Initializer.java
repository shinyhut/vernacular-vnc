package com.shinyhut.vernacular.protocol.initialization;

import com.shinyhut.vernacular.client.VernacularConfig;
import com.shinyhut.vernacular.client.VncSession;
import com.shinyhut.vernacular.client.rendering.ColorDepth;
import com.shinyhut.vernacular.protocol.messages.*;

import java.io.IOException;
import java.io.OutputStream;

import static com.shinyhut.vernacular.protocol.messages.Encoding.*;

public class Initializer {

    public void initialise(VncSession session) throws IOException {
        OutputStream out = session.getOutputStream();

        ClientInit clientInit = new ClientInit(session.getConfig().isShared());
        clientInit.encode(out);

        ServerInit serverInit = ServerInit.decode(session.getInputStream());
        session.setServerInit(serverInit);
        session.setFramebufferWidth(serverInit.getFramebufferWidth());
        session.setFramebufferHeight(serverInit.getFramebufferHeight());

        VernacularConfig config = session.getConfig();
        ColorDepth colorDepth = config.getColorDepth();

        PixelFormat pixelFormat = new PixelFormat(
                colorDepth.getBitsPerPixel(),
                colorDepth.getDepth(),
                true,
                colorDepth.isTrueColor(),
                colorDepth.getRedMax(),
                colorDepth.getGreenMax(),
                colorDepth.getBlueMax(),
                colorDepth.getRedShift(),
                colorDepth.getGreenShift(),
                colorDepth.getBlueShift());

        SetPixelFormat setPixelFormat = new SetPixelFormat(pixelFormat);
        SetEncodings setEncodings = new SetEncodings(HEXTILE, RRE, COPYRECT, RAW, DESKTOP_SIZE);

        setPixelFormat.encode(out);
        setEncodings.encode(out);

        session.setPixelFormat(pixelFormat);
    }

}
