package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.avaje.inject.Prototype;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.Objects;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Node providing an overview on all the monitored {@link Roof}.
 */
@Prototype
public final class RoofsRegistryNode extends SplitPane {

    private static final double ROOFS_TABLE_CODE_COLUMN_MIN_WIDTH = 80.0;
    private static final double ROOFS_TABLE_BUILDING_ADDRESS_COLUMN_MIN_WIDTH = 300.0;
    private static final double SPLITPANE_DIVIDER_POSITION = 0.7;

    private final RoofsManager manager;
    private final TableView<Roof> roofsTableView;
    private final TextField searchStringTextField;

    private final OsmMap map;

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

        this.setId("roofRegistryNode");

        final var leftPane = new VBox();
        final var rightPane = new VBox();
        this.getItems().addAll(new StackPane(leftPane), new StackPane(rightPane));
        this.setDividerPositions(SPLITPANE_DIVIDER_POSITION);

        // Title label
        final var titleLabel = new Label("Roofs");
        titleLabel.getStyleClass().add("h1");

        leftPane.getChildren().add(titleLabel);

        // Roofs search text field
        searchStringTextField = new TextField();
        searchStringTextField.setId("searchRoofsTextField");
        searchStringTextField.setPromptText("Search...");

        leftPane.getChildren().add(searchStringTextField);

        // Roofs table
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

        leftPane.getChildren().add(roofsTableView);

        // Buttons
        final Button addRoofButton = new Button("Add");
        addRoofButton.setId("addRoofButton");
        final Button editRoofButton = new Button("Edit");
        editRoofButton.setId("editRoofButton");
        final Button removeRoofButton = new Button("Delete");
        removeRoofButton.setId("removeRoofButton");

        final var buttonsContainer = new HBox();
        buttonsContainer.setAlignment(Pos.BASELINE_RIGHT);
        buttonsContainer.getChildren().addAll(addRoofButton, editRoofButton, removeRoofButton);

        leftPane.getChildren().add(buttonsContainer);

        // Roofs satellite map
        map = new OsmMap();

        rightPane.getChildren().add(map);

        //-----------------------------------------------------------------------------------------
        // NODE LOGIC
        //-----------------------------------------------------------------------------------------

        // Search field action
        searchStringTextField.textProperty().addListener(invalidation -> performSearch());

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

        //-----------------------------------------------------------------------------------------
        // NODE INITIALIZATION
        //-----------------------------------------------------------------------------------------

        // Loading some roof by performing a search with the search term in searchStringTextField after the node is
        // fully loaded, meaning that the map is loaded
        map.loadedProperty().addListener(
                (obs, oldState, newState) -> {
                    if (newState) {
                        performSearch();
                    }
                }
        );
    }

    private void performSearch() {
        final var roofs = manager.search(searchStringTextField.getText());
        roofsTableView.getItems().setAll(roofs);

        // Updating the map node
        if (map.loadedProperty().get()) {
            map.removeAllMarkers();

            for (final var roof : roofs) {
                map.addMarker(roof.getCoordinates(), roof.getCode());
            }
        }
    }

}
