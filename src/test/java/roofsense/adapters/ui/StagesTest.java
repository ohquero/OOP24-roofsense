package roofsense.adapters.ui;

import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link Stages}.
 */
@ExtendWith(ApplicationExtension.class)
class StagesTest {

    @Test
    void createStageSetsSceneRootAndMinSizes() throws InterruptedException {
        final var latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // given: a root with a deterministic preferred size
                final var root = new StackPane();
                final var sized = new Region();
                final int width = 420;
                final int height = 240;
                sized.setPrefSize(width, height);
                root.getChildren().add(sized);

                // when
                final var stage = Stages.createStage(root);

                // then
                assertNotNull(stage);
                assertNotNull(stage.getScene());
                assertSame(root, stage.getScene().getRoot());

                // createStage() applies CSS and then uses prefWidth/Height(-1)
                final double expectedWidth = root.prefWidth(-1);
                final double expectedHeight = root.prefHeight(-1);

                assertEquals(expectedWidth, stage.getMinWidth());
                assertEquals(expectedHeight, stage.getMinHeight());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

}
