package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roofsense.entities.Roof;

/**
 * View providing an overview on all the monitored devices.
 */
public final class RoofsRegistry extends AbstractNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoofsRegistry.class);

    @FXML
    private Node rootNode;
    @FXML
    private TableView<Roof> roofsTableView;
    @FXML
    private TableColumn<Roof, String> roofsTableCodeColumn;
    @FXML
    private TableColumn<Roof, String> roofsTableBuildingAddressColumn;
    @FXML
    private TextField searchRoofsTextField;
    @FXML
    private Button addNewRoofButton;
    @FXML
    private Button removeRoofButton;
    @FXML
    private Button saveEditsButton;

    private RoofsRegistry() {
    }

    /**
     * Creates a new instance of this node.
     *
     * @return a new instance of this node.
     */
    public static RoofsRegistry create() {
        return loadFXMLFile("javafx/RoofsRegistry.fxml", param -> new RoofsRegistry()).getController();
    }

    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        // roofsTableView initialization
        roofsTableCodeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        roofsTableBuildingAddressColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell
                .getValue()
                .getBuildingAddress()));

        // making roofsTableView rows editable
        roofsTableView.setRowFactory(tv -> {
            final TableRow<Roof> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editRoof(row.getItem());
                }
            });
            return row;
        });
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Node getRootNode() {
        return rootNode;
    }

    private void editRoof(final Roof roof) {
        LOGGER.debug("Requested editing roof {}", roof);
    }

}
