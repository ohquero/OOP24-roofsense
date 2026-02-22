package roofsense.adapters.ui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;

/**
 * Base class for all {@link AbstractNode} unit tests.
 *
 * <p>
 * It includes a test named {@code showInView()} which displays the {@link AbstractNode} in a dedicated {@link Stage}.
 */
@ExtendWith(ApplicationExtension.class)
abstract class AbstractNodeTest {

    /**
     * Run this test to show the {@link AbstractNode} in a dedicated {@link Stage}.
     */
    @Test
    @Tag("show-node")
    void show() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                final var stage = Stages.createStage(getNode());
                stage.showAndWait();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    protected abstract AbstractNode getNode();

}
