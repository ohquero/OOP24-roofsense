package roofsense.adapters.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Coordinates;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE: MultipleStringLiterals OFF
// CHECKSTYLE: MagicNumber OFF

class LeafletMapTest extends AbstractNodeTest {

    private WebEngineAdapter mockWebEngine;
    private SimpleBooleanProperty loaded;
    private LeafletMap map;

    @Override
    protected Stage getStage() {
        final var stage = new Stage();
        stage.setTitle("LeafletMap node demo");
        stage.setScene(new Scene(new LeafletMap()));
        stage.show();
        return stage;
    }

    @Start
    void start(final Stage testfxStage) {
        mockWebEngine = mock(WebEngineAdapter.class);
        loaded = new SimpleBooleanProperty(true);
        when(mockWebEngine.loadedProperty()).thenReturn(loaded);
        map = new LeafletMap(mockWebEngine);

        final var scene = new Scene(map);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
        testfxStage.setScene(scene);
        testfxStage.show();
    }

    @Test
    void addMarkerShouldDelegateToExecutorTest(final FxRobot robot) {
        // given
        final var markerId = UUID.randomUUID().toString();
        when(mockWebEngine.executeScript(anyString())).thenReturn(markerId);
        final var markerRef = new AtomicReference<LeafletMapMarker>();

        // when
        robot.interact(() -> markerRef.set(map.addMarker(new Coordinates(1.2, 3.4), "test")));

        // then
        assertEquals(new LeafletMapMarker(markerId), markerRef.get());
        verify(mockWebEngine).executeScript("addMarker(1.2, 3.4, 'test')");
    }

    @Test
    void addMarkerWithSingleQuoteInTitleShouldEscapeTest(final FxRobot robot) {
        when(mockWebEngine.executeScript("addMarker(1.0, 2.0, 'a\\'b')")).thenReturn(UUID.randomUUID().toString());

        robot.interact(() -> map.addMarker(new Coordinates(1.0, 2.0), "a'b"));

        verify(mockWebEngine).executeScript("addMarker(1.0, 2.0, 'a\\'b')");
    }

    @Test
    void addMarkerWhenNotLoadedShouldThrowTest(final FxRobot robot) {
        robot.interact(() -> loaded.set(false));

        assertThrows(IllegalStateException.class, () -> map.addMarker(new Coordinates(1.0, 2.0), "test"));
        verify(mockWebEngine, never()).executeScript(anyString());
    }

    @Test
    void addMarkerWithNullArgumentsShouldThrowTest() {
        assertThrows(NullPointerException.class, () -> map.addMarker(null, "test"));
        assertThrows(NullPointerException.class, () -> map.addMarker(new Coordinates(1.0, 2.0), null));
    }

    @Test
    void removeAllMarkersShouldDelegateToExecutorTest(final FxRobot robot) {
        robot.interact(map::removeAllMarkers);

        verify(mockWebEngine).executeScript("removeAllMarkers()");
    }

    @Test
    void removeAllMarkersWhenNotLoadedShouldThrowTest(final FxRobot robot) {
        robot.interact(() -> loaded.set(false));

        assertThrows(IllegalStateException.class, map::removeAllMarkers);
        verify(mockWebEngine, never()).executeScript(anyString());
    }

}
