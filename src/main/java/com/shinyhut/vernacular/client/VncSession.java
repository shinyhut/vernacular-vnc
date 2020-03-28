package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.protocol.messages.PixelFormat;
import com.shinyhut.vernacular.protocol.messages.ProtocolVersion;
import com.shinyhut.vernacular.protocol.messages.ServerInit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class VncSession {

    private final VernacularConfig config;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private ProtocolVersion protocolVersion;
    private ServerInit serverInit;
    private PixelFormat pixelFormat;

    private volatile int framebufferWidth;
    private volatile int framebufferHeight;

    private boolean receivedFramebufferUpdate = false;
    private final ReentrantLock framebufferUpdateLock = new ReentrantLock();
    private final Condition framebufferUpdatedCondition = framebufferUpdateLock.newCondition();

    public VncSession(VernacularConfig config, InputStream inputStream, OutputStream outputStream) {
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

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
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

    public void waitForFramebufferUpdate() throws InterruptedException {
        framebufferUpdateLock.lock();
        try {
            while (!receivedFramebufferUpdate) {
                framebufferUpdatedCondition.await();
            }
            receivedFramebufferUpdate = false;
        } finally {
            framebufferUpdateLock.unlock();
        }
    }

    public void framebufferUpdated() {
        framebufferUpdateLock.lock();
        try {
            receivedFramebufferUpdate = true;
            framebufferUpdatedCondition.signalAll();
        } finally {
            framebufferUpdateLock.unlock();
        }
    }

    public void kill() {
        try {
            inputStream.close();
        } catch (IOException ignored) {
        } finally {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}
