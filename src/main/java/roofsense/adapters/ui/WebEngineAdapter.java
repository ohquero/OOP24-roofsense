package roofsense.adapters.ui;

import javafx.beans.property.BooleanProperty;


/**
 * Adapter over a web engine used to display web content and execute JavaScript code.
 *
 * <p>
 * Decouples {@link LeafletMap} from the JavaFX {@link javafx.scene.web.WebEngine}, which cannot be easily mocked
 * in tests, by exposing only the operations needed: page loading, script execution and load-state observation.
 */
public interface WebEngineAdapter {

    /**
     * Loads the webpage located at the given URL.
     *
     * @param url the URL of the webpage to load
     */
    void load(final String url);

    /**
     * Executes the given JavaScript code on the webpage.
     *
     * @param script the JavaScript code to execute
     *
     * @return the result of the execution, if any.
     */
    Object executeScript(final String script);

    /**
     * Returns a property which well become {@code true} once the webpage is completely loaded.
     *
     * @return the loaded property.
     */
    BooleanProperty loadedProperty();

}
