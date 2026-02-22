package roofsense.adapters.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for {@link Stage}s.
 */
public final class Stages {

    private Stages() {
    }

    /**
     * Creates a new {@link Stage} with the given {@link AbstractNode} as its root.
     *
     * @param node the node to use as the root of the stage
     *
     * @return a new {@link Stage}
     */
    public static Stage createStage(final AbstractNode node) {
        final var root = (Parent) node.getRootNode();
        final var stage = new Stage();
        final var scene = new Scene(root);
        stage.setScene(scene);

        // Force CSS application to ensure correct size calculation since styling affects layout dimensions
        root.applyCss();

        // Set minimum stage size to node pref size to ensure that will be visible
        final double width = root.prefWidth(-1);
        final double height = root.prefHeight(-1);
        stage.setMinHeight(height);
        stage.setMinWidth(width);

        return stage;
    }

}
