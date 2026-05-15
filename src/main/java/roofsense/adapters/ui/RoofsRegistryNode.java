package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.Objects;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Node providing an overview on all the monitored {@link Roof}.
 */
public final class RoofsRegistryNode extends AnchorPane {

    private static final double ROOFS_TABLE_CODE_COLUMN_MIN_WIDTH = 80.0;
    private static final double ROOFS_TABLE_BUILDING_ADDRESS_COLUMN_MIN_WIDTH = 300.0;

    private final RoofsManager manager;
    private final TableView<Roof> roofsTableView;
    private final TextField searchRoofsTextField;

    /**
     * Constructor.
     *
     * @param manager the use case for managing {@link Roof} entities.
     */
    @SuppressFBWarnings(value = "EI2", justification = "RoofsManager is effectively immutable")
    public RoofsRegistryNode(final RoofsManager manager) {
        this.manager = Objects.requireNonNull(manager);

        //-----------------------------------------------------------------------------------------
        // NODE LAYOUT
        //-----------------------------------------------------------------------------------------

        final VBox rootNode = new VBox();
        rootNode.setId("roofRegistryNode");

        final Label titleLabel = new Label("Roofs");
        titleLabel.getStyleClass().add("h1");

        searchRoofsTextField = new TextField();
        searchRoofsTextField.setId("searchRoofsTextField");
        searchRoofsTextField.setPromptText("Search...");

        // Create TableView and columns
        roofsTableView = new TableView<>();
        roofsTableView.setId("roofsTableView");
        VBox.setVgrow(roofsTableView, javafx.scene.layout.Priority.ALWAYS);

        final TableColumn<Roof, String> roofsTableCodeColumn = new TableColumn<>("Code");
        roofsTableCodeColumn.setMinWidth(ROOFS_TABLE_CODE_COLUMN_MIN_WIDTH);
        roofsTableCodeColumn.setPrefWidth(-1); // USE_COMPUTED_SIZE

        final TableColumn<Roof, String> roofsTableBuildingAddressColumn = new TableColumn<>("Building address");
        roofsTableBuildingAddressColumn.setMinWidth(ROOFS_TABLE_BUILDING_ADDRESS_COLUMN_MIN_WIDTH);
        roofsTableBuildingAddressColumn.setPrefWidth(-1); // USE_COMPUTED_SIZE

        roofsTableView.getColumns().add(roofsTableCodeColumn);
        roofsTableView.getColumns().add(roofsTableBuildingAddressColumn);

        // Create buttons
        final Button addRoofButton = new Button("Add");
        addRoofButton.setId("addRoofButton");
        final Button editRoofButton = new Button("Edit");
        editRoofButton.setId("editRoofButton");
        final Button removeRoofButton = new Button("Delete");
        removeRoofButton.setId("removeRoofButton");

        // Create container for buttons
        final var buttonsContainer = new HBox();
        buttonsContainer.setAlignment(Pos.BASELINE_RIGHT);
        buttonsContainer.getChildren().addAll(addRoofButton, editRoofButton, removeRoofButton);

        // Add all components to this region
        rootNode.getChildren().addAll(titleLabel, searchRoofsTextField, roofsTableView, buttonsContainer);
        setBottomAnchor(rootNode, 0.0);
        setLeftAnchor(rootNode, 0.0);
        setRightAnchor(rootNode, 0.0);
        setTopAnchor(rootNode, 0.0);
        getChildren().add(rootNode);

        //-----------------------------------------------------------------------------------------
        // NODE LOGIC
        //-----------------------------------------------------------------------------------------

        // Search field action
        searchRoofsTextField.textProperty().addListener(invalidation -> performSearch());

        // Initialize table columns
        roofsTableCodeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        roofsTableBuildingAddressColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell
                .getValue()
                .getBuildingAddress()));

        // Add button action
        addRoofButton.setOnAction(clickEvent -> {
            final var form = new RoofFormNode(manager);
            final var stage = Stages.createStage(form);
            form.addEventHandler(
                    RoofFormNode.EventTypes.ROOF_SAVED, creationEvent -> {
                        performSearch();

                        final var pause = new PauseTransition(Duration.seconds(1));
                        pause.setOnFinished(event -> stage.close());
                        pause.play();
                    }
            );
            stage.initOwner(this.getScene().getWindow());
            stage.initModality(WINDOW_MODAL);
            stage.show();
        });

        // Edit button is disabled when no roof is selected
        editRoofButton.disableProperty().bind(roofsTableView.getSelectionModel().selectedItemProperty().isNull());

        // Edit button action
        editRoofButton.setOnAction(event -> {
            final var form = new RoofFormNode(manager);
            form.setRoof(roofsTableView.getSelectionModel().getSelectedItem());
            final var stage = Stages.createStage(form);
            form.addEventHandler(
                    RoofFormNode.EventTypes.ROOF_SAVED, updateEvent -> {
                        final int selectedRoofIndex = roofsTableView.getSelectionModel().getSelectedIndex();
                        roofsTableView.getItems().set(selectedRoofIndex, updateEvent.getObject());

                        roofsTableView.getSelectionModel().clearSelection();

                        stage.close();
                    }
            );
            stage.initOwner(this.getScene().getWindow());
            stage.initModality(WINDOW_MODAL);
            stage.show();
        });

        // Remove button is disabled when no roof is selected
        removeRoofButton.disableProperty().bind(roofsTableView.getSelectionModel().selectedItemProperty().isNull());

        // Remove button action
        removeRoofButton.setOnAction(event -> {
            final var selectedRoof = roofsTableView.getSelectionModel().getSelectedItem();
            manager.remove(selectedRoof);
            roofsTableView.getItems().remove(selectedRoof);

            roofsTableView.getSelectionModel().clearSelection();
        });

        // Filling the roofsTableView with the roofs retrieved with an empty-string search, in order that when the
        // searchRoofsTextField will be cleared the same roofs will appear
        performSearch();
    }

    private void performSearch() {
        final var roofs = manager.search(searchRoofsTextField.getText());
        roofsTableView.getItems().setAll(roofs);
    }

}
