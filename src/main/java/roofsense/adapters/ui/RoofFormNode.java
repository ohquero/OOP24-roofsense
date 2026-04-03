package roofsense.adapters.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import roofsense.adapters.ui.events.SaveEvent;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;
import roofsense.utils.ConstraintViolations;
import roofsense.utils.Validators;

import java.util.Objects;

/**
 * Form for editing {@link Roof} objects.
 */
public final class RoofFormNode extends AnchorPane {

    private static final int FIELDS_COLUMN_MIN_WIDTH = 350;
    private static final int SAVE_RESULT_LABEL_MIN_WIDTH = 200;
    private final BooleanProperty editedRoofIsValidProperty;

    private final Label titleLabel;
    private final TextField codeTextField;
    private final Label codeValidationResultLabel;
    private final TextField buildingAddressTextField;
    private final Label buildingAddressValidationResultLabel;
    private final Label saveResultLabel;

    private Roof roof;
    private Roof newRoof;

    /**
     * Constructor.
     *
     * @param manager the use case for managing {@link Roof} entities. Must not be {@code null}
     */
    public RoofFormNode(final RoofsManager manager) {
        Objects.requireNonNull(manager);

        this.editedRoofIsValidProperty = new SimpleBooleanProperty();
        this.newRoof = new Roof(null, null);

        //-----------------------------------------------------------------------------------------
        // NODE LAYOUT
        //-----------------------------------------------------------------------------------------

        final VBox rootNode = new VBox();
        rootNode.setId("roofFormNode");

        titleLabel = new Label("Create new roof");
        titleLabel.getStyleClass().add("h1");

        // Create GridPane for form fields
        final GridPane formGrid = new GridPane();
        formGrid.getStyleClass().add("form");
        formGrid.getColumnConstraints().addAll(
                new ColumnConstraints(),  // labels column
                new ColumnConstraints(FIELDS_COLUMN_MIN_WIDTH, -1, -1)  // fields column
        );

        // Code field
        final Label codeLabel = new Label("Code:");
        codeTextField = new TextField();
        codeTextField.setId("codeTextField");
        codeTextField.setPromptText("Enter roof code");
        codeValidationResultLabel = new Label();
        codeValidationResultLabel.setId("codeValidationResultLabel");
        codeValidationResultLabel.getStyleClass().add("validation-result-label");
        formGrid.addRow(0, codeLabel, codeTextField);
        formGrid.add(codeValidationResultLabel, 1, 1);

        // Building address field
        final Label buildingAddressLabel = new Label("Building address:");
        buildingAddressTextField = new TextField();
        buildingAddressTextField.setId("buildingAddressTextField");
        buildingAddressTextField.setPromptText("Enter building address");
        buildingAddressValidationResultLabel = new Label();
        buildingAddressValidationResultLabel.setId("buildingAddressValidationResultLabel");
        buildingAddressValidationResultLabel.getStyleClass().add("validation-result-label");
        formGrid.addRow(2, buildingAddressLabel, buildingAddressTextField);
        formGrid.add(buildingAddressValidationResultLabel, 1, 3);

        // Create buttons GridPane
        final GridPane buttonGrid = new GridPane();
        buttonGrid.getColumnConstraints().addAll(
                new ColumnConstraints(SAVE_RESULT_LABEL_MIN_WIDTH, -1, -1),
                new ColumnConstraints()
        );
        buttonGrid.getRowConstraints().add(new RowConstraints());

        saveResultLabel = new Label();
        saveResultLabel.setId("saveResultLabel");
        saveResultLabel.setMinWidth(SAVE_RESULT_LABEL_MIN_WIDTH);
        final Button saveButton = new Button("Save");
        saveButton.setId("saveButton");
        saveButton.setDefaultButton(true);
        buttonGrid.addRow(0, saveResultLabel, saveButton);

        // Add all to root
        rootNode.getChildren().setAll(titleLabel, formGrid, buttonGrid);

        // Set anchors for root node
        setBottomAnchor(rootNode, 0.0);
        setLeftAnchor(rootNode, 0.0);
        setRightAnchor(rootNode, 0.0);
        setTopAnchor(rootNode, 0.0);
        getChildren().add(rootNode);

        //-----------------------------------------------------------------------------------------
        // NODE LOGIC
        //-----------------------------------------------------------------------------------------

        // Reacting to form field changes
        final Runnable updateEditedRoofIsValidProperty = () -> {
            final var isValid = Validators.validate(newRoof).isEmpty();
            editedRoofIsValidProperty.set(isValid);
        };
        codeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            newRoof.setCode(newVal);
            final var violations = Validators.validateProperty(newRoof, "code");
            codeValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateEditedRoofIsValidProperty.run();
        });
        buildingAddressTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            newRoof.setBuildingAddress(newVal);
            final var violations = Validators.validateProperty(newRoof, "buildingAddress");
            buildingAddressValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateEditedRoofIsValidProperty.run();
        });

        // Disable the commit button if the form is not valid
        saveButton.disableProperty().bind(editedRoofIsValidProperty.not());

        // Save button on click action
        saveButton.setOnAction(actionEvent -> {

            if (roof == null && manager.exists(newRoof)) {
                saveResultLabel.setText("An equal Roof already exists");
                saveResultLabel.getStyleClass().setAll("error");
                return;
            }

            newRoof = manager.save(newRoof);
            saveResultLabel.setText("Roof saved successfully");
            saveResultLabel.getStyleClass().setAll("success");
            this.fireEvent(new SaveEvent<>(newRoof, EventTypes.ROOF_SAVED));

            // Re-initialize the form with the persisted roof
            setRoof(newRoof);
        });
    }

    /**
     * Set the {@link Roof} to be edited. If {@link null} a new {@link Roof} will be created.
     *
     * @param roof a {@link Roof}.
     */
    public void setRoof(final Roof roof) {
        this.roof = roof == null ? null : new Roof(roof);
        this.newRoof = roof == null ? new Roof(null, null) : new Roof(roof);

        // setting form title based on whether we are editing or creating a new roof
        if (this.roof != null) {
            titleLabel.setText("Edit roof");
        } else {
            titleLabel.setText("Create new roof");
        }

        // populating form fields
        codeTextField.setText(newRoof.getCode() != null ? newRoof.getCode() : "");
        buildingAddressTextField.setText(newRoof.getBuildingAddress() != null ? newRoof.getBuildingAddress() : "");

        // disabling the codeTextField if editing an existing roof
        codeTextField.setDisable(roof != null);
    }

    /**
     * Event types for {@link RoofFormNode} events.
     */
    public static final class EventTypes {

        public static final EventType<SaveEvent<Roof>> ROOF_SAVED = new EventType<>(Event.ANY, "ROOF_SAVED");

        private EventTypes() {
        }

    }

}
