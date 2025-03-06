package roofsense.dashboard.boundary.javafxview;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class DevicesOverviewJavaFXView extends JavaFXView {

    private static final DevicesOverviewJavaFXView INSTANCE =
            loadFXMLFile("javafx/views/DevicesOverview.fxml").getController();

    @FXML
    private Node rootNode;

    @FXML
    private Pane unknownDevicesPane;

    /**
     * Get the singleton instance of this view.
     *
     * @return the singleton instance of this view.
     */
    public static DevicesOverviewJavaFXView getInstance() {
        return INSTANCE;
    }

    public void initialize() {
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }
}
