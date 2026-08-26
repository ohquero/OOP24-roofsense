package roofsense.adapters.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Worker;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import roofsense.entities.Coordinates;

import java.util.Objects;

/**
 * A {@link Region} that displays an OpenStreetMap map using Leaflet.js inside a {@link WebView}.
 *
 * <p>
 * Markers can be added and removed via {@link #addMarker(Coordinates, String)} and
 * {@link #removeAllMarkers()}.
 */
public final class LeafletMap extends Region {

    private static final String MAP_HTML_PATH = "leaflet-map-index.html";
    private static final Integer MIN_NODE_SIZE = 300;

    private final WebView webView;
    private final BooleanProperty loaded = new SimpleBooleanProperty(false);

    /**
     * Creates a new {@link LeafletMap} instance.
     */
    public LeafletMap() {
        webView = new WebView();

        webView.prefWidthProperty().bind(widthProperty());
        webView.prefHeightProperty().bind(heightProperty());

        loaded.bind(
                webView.getEngine().getLoadWorker().stateProperty().isEqualTo(Worker.State.SUCCEEDED)
        );
//        loaded.addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                webView.getEngine().executeScript("map.invalidateSize()");
//            }
//        });
        webView.getEngine().load(ClassLoader.getSystemResource(MAP_HTML_PATH).toExternalForm());

        getChildren().add(webView);
        setMinWidth(MIN_NODE_SIZE);
        setMinHeight(MIN_NODE_SIZE);
    }

    /**
     * Adds a marker at the given coordinates with the specified title.
     *
     * @param coordinates the coordinates where the marker will be placed
     * @param title       the title (popup text) of the marker
     */
    public void addMarker(final Coordinates coordinates, final String title) {
        Objects.requireNonNull(coordinates, "coordinates must not be null");
        Objects.requireNonNull(title, "title must not be null");

        final String script = "addMarker(%s, %s, '%s')"
                .formatted(
                        coordinates.getLatitude(),
                        coordinates.getLongitude(),
                        title.replace("'", "\\'")
                );
        webView.getEngine().executeScript(script);
    }

    /**
     * Removes all markers from the map.
     */
    public void removeAllMarkers() {
        webView.getEngine().executeScript("removeAllMarkers()");
    }

    /**
     * Returns the loaded property, which is {@code true} when the map has finished loading.
     *
     * @return the loaded property
     */
    public BooleanProperty loadedProperty() {
        return loaded;
    }

}
