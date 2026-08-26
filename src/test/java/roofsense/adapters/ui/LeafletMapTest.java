package roofsense.adapters.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.Start;

class LeafletMapTest extends AbstractNodeTest {

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
        final var map = new LeafletMap();

        final var scene = new Scene(map);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);

        return scene;
    }

}