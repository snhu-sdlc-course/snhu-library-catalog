package edu.snhu.library.javafxext;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is the application which starts JavaFx. It is controlled through the
 * startJavaFx() method.
 */
public class SingleJfxApplication extends Application {

    /** The lock that guarantees that only one JavaFX thread will be started. */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /** Started flag. */
    private static AtomicBoolean started = new AtomicBoolean();

    /**
     * Start JavaFx.
     *
     * @throws InterruptedException
     */
    public static void startJavaFx() {
        try {
            // Lock or wait. This gives another call to this method time to
            // finish
            // and release the lock before another one has a go
            LOCK.lock();

            if (!started.get()) {
                // start the JavaFX application
                final ExecutorService executor = Executors.newSingleThreadExecutor();
                Future<?> jfxLaunchFuture = executor.submit(() -> SingleJfxApplication.launch());

                while (!started.get()) {
                    try {
                        jfxLaunchFuture.get(1, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | TimeoutException | ExecutionException e) {
                        // continue waiting until success or error
                    }
                    Thread.yield();
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * Launch.
     */
    protected static void launch() {
        Application.launch();
    }

    /**
     * An empty start method.
     *
     * @param stage
     *            The stage
     */
    @Override
    public void start(final Stage stage) {
        started.set(Boolean.TRUE);
    }
}
