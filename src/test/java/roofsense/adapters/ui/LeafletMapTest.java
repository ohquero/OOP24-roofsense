package roofsense.adapters.ui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Coordinates;

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

    private WebEngineAdapter mockJavascriptExecutor;
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
        mockJavascriptExecutor = mock(WebEngineAdapter.class);
        loaded = new SimpleBooleanProperty(true);
        when(mockJavascriptExecutor.loadedProperty()).thenReturn(loaded);
        map = new LeafletMap(mockJavascriptExecutor);

        final var scene = new Scene(map);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
        testfxStage.setScene(scene);
        testfxStage.show();
    }

    @Test
    void addMarkerShouldDelegateToExecutorTest(final FxRobot robot) {
        when(mockJavascriptExecutor.executeScript("addMarker(1.2, 3.4, 'test')")).thenReturn(7);
        final var markerRef = new AtomicReference<LeafletMapMarker>();

        robot.interact(() -> markerRef.set(map.addMarker(new Coordinates(1.2, 3.4), "test")));

        assertEquals(new LeafletMapMarker(7), markerRef.get());
        verify(mockJavascriptExecutor).executeScript("addMarker(1.2, 3.4, 'test')");
    }

    @Test
    void addMarkerWithDoubleResultShouldConvertToIntegerTest(final FxRobot robot) {
        when(mockJavascriptExecutor.executeScript(anyString())).thenReturn(3.0);
        final var markerRef = new AtomicReference<LeafletMapMarker>();

        robot.interact(() -> markerRef.set(map.addMarker(new Coordinates(1.0, 2.0), "test")));

        assertEquals(new LeafletMapMarker(3), markerRef.get());
    }

    @Test
    void addMarkerWithSingleQuoteInTitleShouldEscapeTest(final FxRobot robot) {
        when(mockJavascriptExecutor.executeScript("addMarker(1.0, 2.0, 'a\\'b')")).thenReturn(0);

        robot.interact(() -> map.addMarker(new Coordinates(1.0, 2.0), "a'b"));

        verify(mockJavascriptExecutor).executeScript("addMarker(1.0, 2.0, 'a\\'b')");
    }

    @Test
    void addMarkerWhenNotLoadedShouldThrowTest(final FxRobot robot) {
        robot.interact(() -> loaded.set(false));

        assertThrows(IllegalStateException.class, () -> map.addMarker(new Coordinates(1.0, 2.0), "test"));
        verify(mockJavascriptExecutor, never()).executeScript(anyString());
    }

    @Test
    void addMarkerWithNullArgumentsShouldThrowTest() {
        assertThrows(NullPointerException.class, () -> map.addMarker(null, "test"));
        assertThrows(NullPointerException.class, () -> map.addMarker(new Coordinates(1.0, 2.0), null));
    }

    @Test
    void removeAllMarkersShouldDelegateToExecutorTest(final FxRobot robot) {
        robot.interact(map::removeAllMarkers);

        verify(mockJavascriptExecutor).executeScript("removeAllMarkers()");
    }

    @Test
    void removeAllMarkersWhenNotLoadedShouldThrowTest(final FxRobot robot) {
        robot.interact(() -> loaded.set(false));

        assertThrows(IllegalStateException.class, map::removeAllMarkers);
        verify(mockJavascriptExecutor, never()).executeScript(anyString());
    }

}
