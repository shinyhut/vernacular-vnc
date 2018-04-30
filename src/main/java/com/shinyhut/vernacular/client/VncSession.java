package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.ServerInit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

public class VncSession {

    private final String host;
    private final int port;
    private final VernacularConfig config;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private ServerInit serverInit;
    private PixelFormat pixelFormat;

    private int framebufferWidth;
    private int framebufferHeight;

    private LocalDateTime lastFramebufferUpdateTime;

    public VncSession(String host, int port, VernacularConfig config, InputStream inputStream, OutputStream outputStream) {
        this.host = host;
        this.port = port;
        this.config = config;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public VernacularConfig getConfig() {
        return config;
    }

    public ServerInit getServerInit() {
        return serverInit;
    }

    public void setServerInit(ServerInit serverInit) {
        this.serverInit = serverInit;
    }

    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    public void setPixelFormat(PixelFormat pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public Optional<LocalDateTime> getLastFramebufferUpdateTime() {
        return Optional.ofNullable(lastFramebufferUpdateTime);
    }

    public void setLastFramebufferUpdateTime(LocalDateTime lastFramebufferUpdateTime) {
        this.lastFramebufferUpdateTime = lastFramebufferUpdateTime;
    }

    public int getFramebufferWidth() {
        return framebufferWidth;
    }

    public void setFramebufferWidth(int framebufferWidth) {
        this.framebufferWidth = framebufferWidth;
    }

    public int getFramebufferHeight() {
        return framebufferHeight;
    }

    public void setFramebufferHeight(int framebufferHeight) {
        this.framebufferHeight = framebufferHeight;
    }

    public void kill() {
        try {
            inputStream.close();
        } catch (IOException e) {
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }
}
