package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.client.rendering.ColorDepth;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.shinyhut.vernacular.client.rendering.ColorDepth.BPP_8;

public class VernacularConfig {

    private Supplier<String> passwordSupplier;
    private Consumer<VncException> errorListener;
    private Consumer<Image> framebufferUpdateListener;
    private Consumer<Void> bellListener;
    private Consumer<String> serverCutTextListener;
    private boolean shared = true;
    private int targetFramesPerSecond = 30;
    private ColorDepth colorDepth = BPP_8;

    public Supplier<String> getPasswordSupplier() {
        return passwordSupplier;
    }

    /**
     * Specifies a Supplier which will be called to find the VNC password if the remote host requires authentication.
     * Most implementations will prompt the user to enter their password and return it immediately
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
     * Specifies a Consumer which will be passed a reference to any Exception which occurs during execution of the
     * VNC client. Most implementations will handle the exception by displaying a graphical error message to the user.
     *
     * @param errorListener A Consumer which will receive any Exceptions which occur during the VNC session
     */
    public void setErrorListener(Consumer<VncException> errorListener) {
        this.errorListener = errorListener;
    }

    public Consumer<Image> getFramebufferUpdateListener() {
        return framebufferUpdateListener;
    }

    /**
     * Specifies a Consumer which will be passed a java.awt.Image representing the remote server's desktop every time
     * we receive a framebuffer update. Most implementations will simply display the updated image on screen.
     *
     * @param framebufferUpdateListener A Consumer which will receive Images representing the updated remote desktop
     */
    public void setFramebufferUpdateListener(Consumer<Image> framebufferUpdateListener) {
        this.framebufferUpdateListener = framebufferUpdateListener;
    }

    public Consumer<String> getServerCutTextListener() {
        return serverCutTextListener;
    }

    /**
     * Specifies a Consumer which will be invoked when the sever wants to store text in the clipboard
     *
     * @param serverCutTextListener  A Consumer which will be invoked when the sever wants to store text in the clipboard
     */
    public void setServerCutTextListener(Consumer<String> serverCutTextListener) {
        this.serverCutTextListener = serverCutTextListener;
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
     * Specifies whether we should request 'shared' access to the remote server. If this is set to false, most servers
     * will disconnect any other clients as soon as we connect.
     *
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
     * Sets the target number of frames per second we wish to receive from the remote server. Note that this is the
     * maximum number of framebuffer updates we will request per second. The server does not have to honour our
     * requests. The higher the number of frames per second, the more bandwidth we will consume.
     *
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
     * Specifies the colour depth to request from the remote server. Note that the greater the colour depth, the more
     * bandwidth we will consume.
     *
     * Default: 8 bits per pixel
     *
     * @see com.shinyhut.vernacular.client.rendering.ColorDepth ColorDepth
     *
     * @param colorDepth The colour depth for rendering the remote desktop
     */
    public void setColorDepth(ColorDepth colorDepth) {
        this.colorDepth = colorDepth;
    }
}
