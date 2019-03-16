package com.shinyhut.vernacular.client;

import com.shinyhut.vernacular.client.exceptions.UnexpectedVncException;
import com.shinyhut.vernacular.client.exceptions.VncException;
import com.shinyhut.vernacular.protocol.messages.*;

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
    private final List<Boolean> buttons = synchronizedList(new ArrayList<>());
    private final ReentrantLock outputLock = new ReentrantLock(true);

    private volatile boolean running;
    private Thread eventLoop;

    private int mouseX;
    private int mouseY;

    ClientEventHandler(VncSession session, Consumer<VncException> errorHandler) {
        this.session = session;
        this.errorHandler = errorHandler;
        range(0, 8).forEach(i -> buttons.add(false));
    }

    void start() {
        running = true;

        eventLoop = new Thread(() -> {
            try {
                while (running) {
                    if (timeForFramebufferUpdate()) {
                        requestFramebufferUpdate();
                    }
                    pause();
                }
            } catch (IOException e) {
                if (running) {
                    errorHandler.accept(new UnexpectedVncException(e));
                }
            } finally {
                running = false;
            }
        });

        eventLoop.start();
    }

    void stop() {
        running = false;
        try {
            if (eventLoop != null) {
                eventLoop.join(1000);
            }
        } catch (InterruptedException ignored) {
        }
    }

    void updateMouseButton(int button, boolean pressed) throws IOException {
        buttons.set(button - 1, pressed);
        updateMouseStatus();
    }

    void moveMouse(int mouseX, int mouseY) throws IOException {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        updateMouseStatus();
    }

    void updateKey(int keySym, boolean pressed) throws IOException {
        KeyEvent message = new KeyEvent(keySym, pressed);
        sendMessage(message);
    }

    void copyText(String text) throws IOException {
        ClientCutText message = new ClientCutText(text);
        sendMessage(message);
    }

    private void updateMouseStatus() throws IOException {
        PointerEvent message = new PointerEvent(mouseX, mouseY, buttons);
        sendMessage(message);
    }

    private boolean timeForFramebufferUpdate() {
        long updateInterval = 1000 / session.getConfig().getTargetFramesPerSecond();
        return session.getLastFramebufferUpdateRequestTime()
                .map(lastUpdate -> now().isAfter(lastUpdate.plus(updateInterval, MILLIS)))
                .orElse(true);
    }

    private void requestFramebufferUpdate() throws IOException {
        boolean incremental = session.getLastFramebufferUpdateTime().isPresent();
        boolean firstRun = !session.getLastFramebufferUpdateRequestTime().isPresent();
        if (firstRun || incremental) {
            session.setLastFramebufferUpdateRequestTime(now());
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
            sleep(1L);
        } catch (InterruptedException ignored) {
        }
    }
}
