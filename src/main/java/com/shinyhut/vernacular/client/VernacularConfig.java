package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.client.rendering.ColorDepth;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_8_INDEXED;

public class VernacularConfig {

    private Supplier<String> passwordSupplier;
    private Consumer<VncException> errorListener;
    private Consumer<Image> screenUpdateListener;
    private Consumer<Void> bellListener;
    private Consumer<String> remoteClipboardListener;
    private boolean shared = true;
    private int targetFramesPerSecond = 30;
    private ColorDepth colorDepth = BPP_8_INDEXED;

    public Supplier<String> getPasswordSupplier() {
        return passwordSupplier;
    }

    /**
     * Specifies a Supplier which will be called to find the VNC password if the remote host requires authentication.
     *
     * @param passwordSupplier A Supplier which when invoked will return the user's VNC password
     */
    public void setPasswordSupplier(Supplier<String> passwordSupplier) {
        this.passwordSupplier = passwordSupplier;
    }

    public Consumer<VncException> getErrorListener() {
        return errorListener;
    }

    /**
     * Specifies a Consumer which will be passed any Exception which occurs during the VNC session.
     *
     * @param errorListener A Consumer which will receive any Exceptions which occur during the VNC session
     */
    public void setErrorListener(Consumer<VncException> errorListener) {
        this.errorListener = errorListener;
    }

    public Consumer<Image> getScreenUpdateListener() {
        return screenUpdateListener;
    }

    /**
     * Specifies a Consumer which will be passed an Image representing the remote server's desktop every time
     * we receive a screen update.
     *
     * @param screenUpdateListener A Consumer which will receive Images representing the updated remote desktop
     * @see java.awt.Image Image
     */
    public void setScreenUpdateListener(Consumer<Image> screenUpdateListener) {
        this.screenUpdateListener = screenUpdateListener;
    }

    public Consumer<String> getRemoteClipboardListener() {
        return remoteClipboardListener;
    }

    /**
     * Specifies a Consumer which will be invoked when the sever wants to store text in the clipboard
     *
     * @param remoteClipboardListener A Consumer which will be invoked when the sever wants to store text in the clipboard
     */
    public void setRemoteClipboardListener(Consumer<String> remoteClipboardListener) {
        this.remoteClipboardListener = remoteClipboardListener;
    }

    public Consumer<Void> getBellListener() {
        return bellListener;
    }

    /**
     * Specifies a Consumer which will be invoked when the server triggers an alert sound
     *
     * @param bellListener A Consumer which will be notified when the server triggers an alert sound
     */
    public void setBellListener(Consumer<Void> bellListener) {
        this.bellListener = bellListener;
    }

    public boolean isShared() {
        return shared;
    }

    /**
     * Specifies whether we should request 'shared' access to the remote server.
     * <p>
     * If this is set to false, most servers will disconnect any other clients as soon as we connect.
     * <p>
     * Default: true
     *
     * @param shared Should we request shared access to the remote server?
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public int getTargetFramesPerSecond() {
        return targetFramesPerSecond;
    }

    /**
     * Sets the target number of frames per second we wish to receive from the remote server.
     * <p>
     * Note that this is the maximum number of framebuffer updates we will request per second. The server does not have
     * to honour our requests. The higher the number of frames per second, the more bandwidth we will consume.
     * <p>
     * Default: 30
     *
     * @param targetFramesPerSecond The number of frames per second we want to receive from the remote server
     */
    public void setTargetFramesPerSecond(int targetFramesPerSecond) {
        this.targetFramesPerSecond = targetFramesPerSecond;
    }

    public ColorDepth getColorDepth() {
        return colorDepth;
    }

    /**
     * Specifies the color depth to request from the remote server. Note that the greater the color depth, the more
     * bandwidth we will consume.
     * <p>
     * Default: 8 bits per pixel
     *
     * @param colorDepth The color depth for rendering the remote desktop
     * @see com.shinyhut.vernacular.client.rendering.ColorDepth ColorDepth
     */
    public void setColorDepth(ColorDepth colorDepth) {
        this.colorDepth = colorDepth;
    }
}
