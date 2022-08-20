package edu.snhu.library.javafxext;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

public class JavaFxExtension implements InvocationInterceptor {

    private Throwable testException = null;

    public JavaFxExtension() {
        SingleJfxApplication.startJavaFx();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interceptTestMethod(final Invocation<Void> invocation, final ReflectiveInvocationContext<Method> invocationContext, final ExtensionContext extensionContext) throws Throwable {
        // Create a latch which is only removed after the super runChild()
        // method
        // has been implemented.
        final CountDownLatch latch = new CountDownLatch(1);

        final Method method = invocationContext.getExecutable();

        // Check whether the method should run in FX-Thread or not.
        final TestInJfxThread performMethodInFxThread = method.getAnnotation(TestInJfxThread.class);
        if (performMethodInFxThread != null) {
            Platform.runLater(() -> {
                try {
                    invocation.proceed();
                } catch (Throwable e) {
                    testException = e;
                } finally {
                    latch.countDown();
                }
            });
        } else {
            try {
                invocation.proceed();
            } catch (Throwable e) {
                testException = e;
            } finally {
                latch.countDown();
            }
        }

        // Decrement the latch which will now proceed.
        try {
            latch.await();
        } catch (InterruptedException e) {
            // Waiting for the latch was interruped
            e.printStackTrace();
        }

        if(testException != null) {
            throw testException;
        }
    }
}