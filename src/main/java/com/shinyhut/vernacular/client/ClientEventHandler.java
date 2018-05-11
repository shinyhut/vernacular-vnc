package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.Encodable;
import com.shinyhut.vernacular.protocol.messages.FramebufferUpdateRequest;
import com.shinyhut.vernacular.protocol.messages.KeyEvent;
import com.shinyhut.vernacular.protocol.messages.PointerEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.lang.Thread.sleep;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.synchronizedList;
import static java.util.stream.IntStream.range;

public class ClientEventHandler {

    private final VncSession session;
    private final Consumer<VncException> errorHandler;

    private final ReentrantLock outputLock = new ReentrantLock(true);
    private boolean running;

    private int mouseX;
    private int mouseY;
    private final List<Boolean> buttons = synchronizedList(new ArrayList<>());

    ClientEventHandler(VncSession session, Consumer<VncException> errorHandler) {
        this.session = session;
        this.errorHandler = errorHandler;
        range(0, 8).forEach(i -> buttons.add(false));
    }

    void start() {
        running = true;

        new Thread(() -> {
            try {
                boolean firstRun = true;
                while (running) {
                    if (timeForFramebufferUpdate()) {
                        requestFramebufferUpdate(!firstRun);
                    }
                    firstRun = false;
                    pause();
                }
            } catch (IOException e) {
                errorHandler.accept(new UnexpectedVncException(e));
            } finally {
                stop();
            }

        }).start();

    }

    void stop() {
        running = false;
    }

    void updateMouseButton(int button, boolean pressed) throws IOException {
        buttons.set(button, pressed);
        updateMouseStatus();
    }

    void moveMouse(int mouseX, int mouseY) throws IOException {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        updateMouseStatus();
    }

    void keyPress(int keySym, boolean pressed) throws IOException {
        KeyEvent message = new KeyEvent(keySym, pressed);
        sendMessage(message);
    }

    private void updateMouseStatus() throws IOException {
        PointerEvent message = new PointerEvent(mouseX, mouseY, buttons);
        sendMessage(message);
    }

    private boolean timeForFramebufferUpdate() {
        long updateInterval = 1000 / session.getConfig().getTargetFramesPerSecond();
        return session.getLastFramebufferUpdateTime()
                .map(lastUpdate -> now().isAfter(lastUpdate.plus(updateInterval, MILLIS)))
                .orElse(true);
    }

    private void requestFramebufferUpdate(boolean incremental) throws IOException {
        if (!incremental || session.getLastFramebufferUpdateTime().isPresent()) {
            int width = session.getFramebufferWidth();
            int height = session.getFramebufferHeight();
            FramebufferUpdateRequest updateRequest = new FramebufferUpdateRequest(incremental, 0, 0, width, height);
            sendMessage(updateRequest);
        }
    }

    private void sendMessage(Encodable message) throws IOException {
        outputLock.lock();
        try {
            message.encode(session.getOutputStream());
        } finally {
            outputLock.unlock();
        }
    }

    private void pause() {
        try {
            sleep(1l);
        } catch (InterruptedException e) {
        }
    }
}
