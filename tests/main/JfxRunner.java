package main;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 *
 * This file is copypasted from:
 * https://github.com/sialcasa/mvvmFX/blob/develop/mvvmfx-testing-utils/src/main/java/de/saxsys/mvvmfx/testingutils/
 * jfxrunner
 *
 */
public class JfxRunner extends BlockJUnit4ClassRunner
{
    /**
     * Constructs a new JavaFxJUnit4ClassRunner with the given parameters.
     *
     * @param clazz
     *            The class that is to be run with this Runner
     * @throws InitializationError
     *             Thrown by the BlockJUnit4ClassRunner in the super()
     */
    public JfxRunner(final Class<?> clazz) throws InitializationError
    {
        super(clazz);

        new JFXPanel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runChild(final FrameworkMethod method, final RunNotifier notifier)
    {
        // Create a latch which is only removed after the super runChild()
        // method
        // has been implemented.
        final CountDownLatch latch = new CountDownLatch(1);

        // Check whether the method should run in FX-Thread or not.
        TestInJfxThread performMethodInFxThread = method.getAnnotation(TestInJfxThread.class);
        if (performMethodInFxThread != null)
        {
            Platform.runLater(() ->
            {
                JfxRunner.super.runChild(method, notifier);
                latch.countDown();
            });
        }
        else
        {
            JfxRunner.super.runChild(method, notifier);
            latch.countDown();
        }

        // Decrement the latch which will now proceed.

        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            // Waiting for the latch was interrupted
            e.printStackTrace();
        }
    }
}
