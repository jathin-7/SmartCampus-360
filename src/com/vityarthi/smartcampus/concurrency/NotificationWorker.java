package com.vityarthi.smartcampus.concurrency;

import com.vityarthi.smartcampus.util.ConsoleFormatter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Background worker thread executing concurrent notification dispatching and queue monitoring.
 * Demonstrates Java Multithreading, Runnable, Producer-Consumer Pattern, and Concurrency Controls.
 */
public class NotificationWorker implements Runnable {
    public record NotificationMessage(String recipientId, String message, long timestamp) {}

    private final BlockingQueue<NotificationMessage> messageQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread workerThread;

    public void start() {
        if (running.compareAndSet(false, true)) {
            workerThread = new Thread(this, "SmartCampus-NotificationDispatcher");
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    public void dispatchNotification(String recipientId, String message) {
        messageQueue.offer(new NotificationMessage(recipientId, message, System.currentTimeMillis()));
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                NotificationMessage msg = messageQueue.poll(500, TimeUnit.MILLISECONDS);
                if (msg != null) {
                    processMessage(msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                ConsoleFormatter.printError("Notification worker encountered an error: " + e.getMessage());
            }
        }
    }

    private void processMessage(NotificationMessage msg) {
        // Asynchronous processing simulation (e.g., SMS/Email dispatch)
        // Kept non-intrusive for CLI experience
        // ConsoleFormatter.printInfo("[BACKGROUND NOTIFICATION -> " + msg.recipientId() + "] " + msg.message());
    }

    public int getPendingCount() {
        return messageQueue.size();
    }
}
