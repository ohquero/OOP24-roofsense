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
import roofsense.usecases.RoofsManager;

import java.util.Objects;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * View providing an overview on all the monitored devices.
 */
public final class RoofsRegistry extends AbstractNode {

    private final RoofsManager manager;

    @FXML
    private Node roofsRegistryRootNode;
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
    private Button editRoofButton;
    @FXML
    private Button removeRoofButton;

    private RoofsRegistry(final RoofsManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    /**
     * Creates a new instance of this node.
     *
     * @param manager the use case for managing {@link Roof} entities.
     *
     * @return a new instance of this node.
     */
    public static RoofsRegistry create(final RoofsManager manager) {
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

        // clicking addNewRoofButton displays a new roof creation form
        addNewRoofButton.setOnAction(clickEvent -> {
            final var form = RoofForm.create(manager);
            final var stage = Stages.createStage(form);
            form.addEventHandler(
                    RoofForm.EventTypes.ROOF_CREATED, creationEvent -> {
                        //TODO: when text search will be implemented, this should be replaced with a more complex logic
                        roofsTableView.getItems().add(creationEvent.getObject());

                        stage.close();
                    }
            );
            stage.initOwner(this.getRootNode().getScene().getWindow());
            stage.initModality(WINDOW_MODAL);
            stage.show();
        });

        // editRoofButton is disabled when no roof is selected
        editRoofButton.disableProperty().bind(roofsTableView.getSelectionModel().selectedItemProperty().isNull());

        // clicking editRoofButton displays a roof editing form
        editRoofButton.setOnAction(event -> {
            final var form = RoofForm.create(manager, roofsTableView.getSelectionModel().getSelectedItem());
            final var stage = Stages.createStage(form);
            form.addEventHandler(
                    RoofForm.EventTypes.ROOF_UPDATED, updateEvent -> {
                        //TODO: when text search will be implemented, this should be replaced with a more complex logic
                        final int selectedRoofIndex = roofsTableView.getSelectionModel().getSelectedIndex();
                        roofsTableView.getItems().set(selectedRoofIndex, updateEvent.getObject());

                        roofsTableView.getSelectionModel().clearSelection();

                        stage.close();
                    }
            );
            stage.initOwner(this.getRootNode().getScene().getWindow());
            stage.initModality(WINDOW_MODAL);
            stage.show();
        });

        // removeRoofButton is disabled when no roof is selected
        removeRoofButton.disableProperty().bind(roofsTableView.getSelectionModel().selectedItemProperty().isNull());

        // clicking removeRoofButton removes the selected roof
        removeRoofButton.setOnAction(event -> {
            final var selectedRoof = roofsTableView.getSelectionModel().getSelectedItem();
            manager.remove(selectedRoof);
            // TODO: when text search will be implemented, this should be replaced with a more complex logic
            roofsTableView.getItems().remove(selectedRoof);

            roofsTableView.getSelectionModel().clearSelection();
        });
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Node getRootNode() {
        return roofsRegistryRootNode;
    }

}
