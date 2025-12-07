package roofsense.adapters.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Base class for all the UI components.
 */
public abstract class AbstractNode {

    /**
     * Load an FXML file.
     *
     * @param fxmlPath the path to the FXML file.
     *
     * @return an {@link FXMLLoader}
     */
    protected static FXMLLoader loadFXMLFile(final String fxmlPath) {
        return loadFXMLFile(fxmlPath, null);
    }

    /**
     * Loads an FXML file and optionally allows setting a controller factory for custom controller instances.
     *
     * @param fxmlPath                  the path to the FXML file to load, relative to the "resources" folder.
     * @param controllerFactoryCallback a {@link Callback} to provide custom controller instances, or {@code null}
     *                                  if no custom factory is required.
     *
     * @return an {@link FXMLLoader}
     *
     * @throws UncheckedIOException if an error occurs while loading the FXML file.
     */
    protected static FXMLLoader loadFXMLFile(
            final String fxmlPath,
            final Callback<Class<?>, Object> controllerFactoryCallback
    ) {
        final var view = new FXMLLoader(ClassLoader.getSystemResource(fxmlPath));
        if (controllerFactoryCallback != null) {
            view.setControllerFactory(controllerFactoryCallback);
        }
        try {
            view.load();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        return view;
    }

    /**
     * Returns the component root {@link Node}.
     *
     * @return a {@link Node}.
     */
    public abstract Node getRootNode();

}
