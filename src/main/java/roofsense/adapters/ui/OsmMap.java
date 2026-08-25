package roofsense.adapters.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Worker;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import roofsense.entities.Coordinates;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * A {@link Region} that displays an OpenStreetMap map using Leaflet.js inside a {@link WebView}.
 *
 * <p>
 * Markers can be added and removed via {@link #addMarker(Coordinates, String)} and
 * {@link #removeAllMarkers()}.
 */
public final class OsmMap extends Region {

    private static final String MAP_HTML_PATH = "roofsense/map.html";
    private static final String LEAFLET_CSS_PATH = "roofsense/leaflet/leaflet.css";
    private static final String LEAFLET_JS_PATH = "roofsense/leaflet/leaflet.js";
    private static final String MARKER_ICON_PATH = "roofsense/leaflet/images/marker-icon.png";
    private static final String MARKER_ICON_RETINA_PATH = "roofsense/leaflet/images/marker-icon-2x.png";
    private static final String MARKER_SHADOW_PATH = "roofsense/leaflet/images/marker-shadow.png";

    private final WebView webView;
    private final BooleanProperty loaded = new SimpleBooleanProperty(false);

    /**
     * Creates a new {@link OsmMap} instance.
     */
    public OsmMap() {
        webView = new WebView();

        webView.prefWidthProperty().bind(widthProperty());
        webView.prefHeightProperty().bind(heightProperty());

//        final String html = buildHtml();
//        webView.getEngine().loadContent(html);

        loaded.bind(
                webView.getEngine().getLoadWorker().stateProperty().isEqualTo(Worker.State.SUCCEEDED)
        );
//        loaded.addListener((obs, was, isNow) -> {
//            if (isNow) {
//                webView.getEngine().executeScript("map.invalidateSize()");
//            }
//        });

        this.getChildren().add(webView);

        final var button = new Button("Load WebView");
        button.setOnAction(event -> {
            final String html = buildHtml();
            webView.getEngine().loadContent(html);
        });

        this.getChildren().add(button);

    }

    /**
     * Returns the webview HTML content.
     *
     * @return an HTML page.
     */
    private static String buildHtml() {
        final String template = readResource(MAP_HTML_PATH);
        final String css = readResource(LEAFLET_CSS_PATH);
        final String js = readResource(LEAFLET_JS_PATH);
        final String markerIcon = base64Encode(MARKER_ICON_PATH);
        final String markerIconRetina = base64Encode(MARKER_ICON_RETINA_PATH);
        final String markerShadow = base64Encode(MARKER_SHADOW_PATH);

        return template
                .replace("/*__LEAFLET_CSS__*/", css)
                .replace("/*__LEAFLET_JS__*/", js)
                .replace("__MARKER_ICON_URL__", markerIcon)
                .replace("__MARKER_ICON_RETINA_URL__", markerIconRetina)
                .replace("__MARKER_SHADOW_URL__", markerShadow);
    }

    private static String readResource(final String path) {
        final var pathStream = ClassLoader.getSystemResourceAsStream(path);
        Objects.requireNonNull(pathStream, "Resource not found: " + path);
        try (pathStream) {
            return new String(pathStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read resource: " + path, e);
        }
    }

    private static String base64Encode(final String resourcePath) {
        try (final var is = ClassLoader.getSystemResourceAsStream(resourcePath)) {
            Objects.requireNonNull(is, "Resource not found: " + resourcePath);
            final byte[] bytes = is.readAllBytes();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read resource: " + resourcePath, e);
        }
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
