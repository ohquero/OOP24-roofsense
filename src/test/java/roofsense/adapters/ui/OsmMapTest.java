package roofsense.adapters.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.Start;

class OsmMapTest extends AbstractNodeTest {

    private Stage stage;

    @Override
    protected Stage getStage() {
        return stage;
    }

    @Start
    void start(final Stage testfxStage) {
        stage = testfxStage;

        final var map = new OsmMap();
        map.setPrefHeight(500);
        map.setPrefWidth(500);

        final var scene = new Scene(map);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
        testfxStage.setScene(scene);
        stage.show();
    }

}