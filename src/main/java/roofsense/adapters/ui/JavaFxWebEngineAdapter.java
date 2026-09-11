package roofsense.adapters.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.concurrent.Worker;

import java.util.Objects;

/**
 * {@link WebEngineAdapter} routing calls to a JavaFX {@link javafx.scene.web.WebEngine}.
 *
 * <p>
 * The loaded property is bound to the engine load worker state and turns {@code true} once the worker reaches
 * {@link Worker.State#SUCCEEDED}.
 */
public final class JavaFxWebEngineAdapter implements WebEngineAdapter {

    private final javafx.scene.web.WebEngine engine;
    private final ReadOnlyBooleanWrapper loaded = new ReadOnlyBooleanWrapper(false);

    /**
     * Default constructor.
     *
     * @param engine the JavaFX web engine to delegate webpage operations to
     */
    public JavaFxWebEngineAdapter(final javafx.scene.web.WebEngine engine) {
        this.engine = Objects.requireNonNull(engine, "engine must not be null");

        loaded.bind(engine.getLoadWorker().stateProperty().isEqualTo(Worker.State.SUCCEEDED));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final String url) {
        Objects.requireNonNull(url, "url must not be null");

        engine.load(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object executeScript(final String script) {
        return engine.executeScript(script);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReadOnlyBooleanProperty loadedProperty() {
        return loaded.getReadOnlyProperty();
    }

}
