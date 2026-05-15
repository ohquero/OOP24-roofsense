package roofsense.adapters.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for {@link Stage}s.
 */
public final class Stages {

    /**
     * The URL string for the stylesheet to be applied to all stages created by this class.
     */
    public static final String STYLESHEET_URL_STRING =
            ClassLoader.getSystemResource("javafx/style.css").toExternalForm();

    private Stages() {
    }

    /**
     * Creates a new {@link Stage} with the given {@link Parent} as its root.
     *
     * @param root the {@link Parent} to use as the root of the stage.
     *
     * @return a new {@link Stage}.
     */
    public static Stage createStage(final Parent root) {
        final var stage = new Stage();
        final var scene = new Scene(root);
        scene.getStylesheets().add(STYLESHEET_URL_STRING);
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
