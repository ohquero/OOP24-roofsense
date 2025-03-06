package roofsense.dashboard.boundary.javafxview;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

/**
 * Base class for all JavaFX views.
 * <p>
 * A JavaFXView represents the pair of an FXML file and its controller. In particular is an instance of th FXML file
 * controller holding a reference to the root node of the FXML file.
 * <p>
 * Concrete subclasses of JavaFXView must be created for each FXML file and provide a static method instantiating the
 * class by loading the FXML file.
 */
public abstract class JavaFXView {

    /**
     * No argument constructor, required by JavaFX to create an instance of this class.
     * <p>
     * Should never be called directly.
     */
    public JavaFXView() {
    }

    /**
     * Load an FXML file from the "resources" folder.
     *
     * @param fxmlPath the path to the FXML file
     * @return a FXMLLoader instance for the FXML file
     */
    protected static FXMLLoader loadFXMLFile(final String fxmlPath) {
        try {
            final var view = new FXMLLoader(ClassLoader.getSystemResource(fxmlPath));
            view.load();
            return view;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the root node of the view.
     *
     * @return the root node of the view
     */
    public abstract Node getRootNode();

}