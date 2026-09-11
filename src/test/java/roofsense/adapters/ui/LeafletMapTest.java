package roofsense.adapters.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Coordinates;

class LeafletMapTest extends AbstractNodeTest {

    private LeafletMap map;

    @Override
    protected Stage getStage() {
        final var stage = new Stage();
        stage.setTitle("LeafletMap node demo");
        stage.setScene(buildScene());
        stage.show();
        return stage;
    }

    @Start
    void start(final Stage testfxStage) {
        testfxStage.setScene(buildScene());
        testfxStage.show();
    }

    private Scene buildScene() {
        map = new LeafletMap();

        final var scene = new Scene(map);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);

        return scene;
    }

    @Test
    void addMarker(final FxRobot robot) {
        robot.interact(() -> map.addMarker(new Coordinates(1.2, 3.4), "test"));
    }

}