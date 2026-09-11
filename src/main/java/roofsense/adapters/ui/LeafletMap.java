package roofsense.adapters.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import roofsense.entities.Coordinates;

import java.util.Objects;

/**
 * A {@link Region} that displays an OpenStreetMap map using Leaflet.js inside a {@link WebView}.
 */
public final class LeafletMap extends Region {

    private static final String MAP_HTML_PATH = "leaflet-map-index.html";
    private static final Integer MIN_NODE_SIZE = 300;

    private final WebEngineAdapter webEngineAdapter;

    /**
     * Default constructor.
     */
    public LeafletMap() {
        final var webView = new WebView();

        webView.prefWidthProperty().bind(widthProperty());
        webView.prefHeightProperty().bind(heightProperty());

        webEngineAdapter = new JavaFxWebEngineAdapter(webView.getEngine());
        webEngineAdapter.load(ClassLoader.getSystemResource(MAP_HTML_PATH).toExternalForm());

        getChildren().add(webView);
        setMinWidth(MIN_NODE_SIZE);
        setMinHeight(MIN_NODE_SIZE);
    }

    /**
     * Creates a new {@link LeafletMap} instance with the given {@link WebEngineAdapter}. Must be used only for test
     * purposes.
     *
     * @param webEngineAdapter the {@link WebEngineAdapter} to delegate webpage operations to (URL loading, JavaScript
     *                         execution, ...).
     */
    LeafletMap(final WebEngineAdapter webEngineAdapter) {
        this.webEngineAdapter = Objects.requireNonNull(webEngineAdapter, "webEngineAdapter must not be null");
    }

    /**
     * Adds a marker at the given coordinates with the specified title.
     *
     * @param coordinates the coordinates where the marker will be placed
     * @param title       the title (popup text) of the marker
     *
     * @return a {@link LeafletMapMarker} representing the marker created.
     *
     * @throws IllegalStateException if the map has not finished loading yet.
     */
    public LeafletMapMarker addMarker(final Coordinates coordinates, final String title) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        Objects.requireNonNull(title, "title must not be null");
        requireWebpageLoaded();

        final var script = "addMarker(%s, %s, '%s')".formatted(
                coordinates.getLatitude(),
                coordinates.getLongitude(),
                title.replace("'", "\\'")
        );
        final var markerId = (String) webEngineAdapter.executeScript(script);
        return new LeafletMapMarker(markerId);
    }

    /**
     * Removes all markers from the map.
     *
     * @throws IllegalStateException if the map has not finished loading yet.
     */
    public void removeAllMarkers() {
        requireWebpageLoaded();
        webEngineAdapter.executeScript("removeAllMarkers()");
    }

    /**
     * Returns the loaded property, which is {@code true} when the map has finished loading.
     *
     * @return the loaded property
     */
    public ReadOnlyBooleanProperty loadedProperty() {
        return webEngineAdapter.loadedProperty();
    }

    private void requireWebpageLoaded() {
        if (!loadedProperty().get()) {
            throw new IllegalStateException("map has not finished loading yet");
        }
    }

}
