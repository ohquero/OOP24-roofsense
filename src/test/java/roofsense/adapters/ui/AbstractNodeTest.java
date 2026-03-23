package roofsense.adapters.ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;

/**
 * Base class for all {@link Node} unit tests.
 *
 * <p>
 * It includes a test named {@code showInView()} which displays the {@link Node} in a dedicated {@link Stage}.
 */
@ExtendWith(ApplicationExtension.class)
abstract class AbstractNodeTest {

    /**
     * Run this test to display the {@link Stage} created by TestFX.
     */
    @Test
    @Tag("show-node")
    void show() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> getStage().setOnHidden(e -> latch.countDown()));
        latch.await();
    }

    protected abstract Stage getStage();

}
