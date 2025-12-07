package roofsense.adapters.ui;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
                final var nodeUnderTest = getNode();
                final var root = (Parent) nodeUnderTest.getRootNode();
                final var stage = new Stage();
                final var scene = new Scene(root);
                stage.setScene(scene);

                // Force CSS application to ensure correct size calculation since styling affects layout dimensions
                root.applyCss();

                // Set minimum stage size to nodeUnderTest pref size to ensure that will be visible
                final double width = root.prefWidth(-1);
                final double height = root.prefHeight(-1);
                stage.setMinHeight(height);
                stage.setMinWidth(width);

                stage.showAndWait();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    protected abstract AbstractNode getNode();

}
