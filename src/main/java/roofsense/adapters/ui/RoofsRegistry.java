package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import java.util.Objects;

/**
 * View providing an overview on all the monitored devices.
 */
public final class RoofsRegistry extends AbstractNode {

    private final ManageRoof manager;

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

    private RoofsRegistry(final ManageRoof manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    /**
     * Creates a new instance of this node.
     *
     * @param manager the use case for managing {@link Roof} entities.
     *
     * @return a new instance of this node.
     */
    public static RoofsRegistry create(final ManageRoof manager) {
        return loadFXMLFile("javafx/RoofsRegistry.fxml", param -> new RoofsRegistry(manager)).getController();
    }

    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
        // roofsTableView initialization
        roofsTableCodeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        roofsTableBuildingAddressColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell
                .getValue()
                .getBuildingAddress()));

        // load roofs
        manager.getAll().forEach(roofsTableView.getItems()::add);
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Node getRootNode() {
        return rootNode;
    }

}
