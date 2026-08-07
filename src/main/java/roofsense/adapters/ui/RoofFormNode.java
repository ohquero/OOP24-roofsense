package roofsense.adapters.ui;

import io.avaje.inject.Prototype;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import roofsense.adapters.ui.events.SaveEvent;
import roofsense.entities.Coordinates;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;
import roofsense.utils.ConstraintViolations;
import roofsense.utils.Validators;

import java.util.Objects;

/**
 * Form for editing {@link Roof} objects.
 */
@Prototype
public final class RoofFormNode extends AnchorPane {

    private static final int FIELDS_COLUMN_MIN_WIDTH = 350;
    private static final int SAVE_RESULT_LABEL_MIN_WIDTH = 200;
    private static final String VALIDATION_RESULT_LABEL_STYLE_CLASS = "validation-result-label";
    private final BooleanProperty editedRoofIsValidProperty;

    private final Label titleLabel;
    private final TextField codeTextField;
    private final Label codeValidationResultLabel;
    private final TextField buildingAddressTextField;
    private final Label buildingAddressValidationResultLabel;
    private final TextField latitudeTextField;
    private final Label latitudeValidationResultLabel;
    private final TextField longitudeTextField;
    private final Label longitudeValidationResultLabel;
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
        this.newRoof = new Roof();

        //-----------------------------------------------------------------------------------------
        // NODE LAYOUT
        //-----------------------------------------------------------------------------------------
        // CHECKSTYLE: MagicNumber OFF

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
        codeTextField.setPromptText("enter roof code...");
        codeValidationResultLabel = new Label();
        codeValidationResultLabel.setId("codeValidationResultLabel");
        codeValidationResultLabel.getStyleClass().add(VALIDATION_RESULT_LABEL_STYLE_CLASS);
        formGrid.addRow(0, codeLabel, codeTextField);
        formGrid.add(codeValidationResultLabel, 1, 1);

        // Building address field
        final Label buildingAddressLabel = new Label("Building address:");
        buildingAddressTextField = new TextField();
        buildingAddressTextField.setId("buildingAddressTextField");
        buildingAddressTextField.setPromptText("enter building address...");
        buildingAddressValidationResultLabel = new Label();
        buildingAddressValidationResultLabel.setId("buildingAddressValidationResultLabel");
        buildingAddressValidationResultLabel.getStyleClass().add(VALIDATION_RESULT_LABEL_STYLE_CLASS);
        formGrid.addRow(2, buildingAddressLabel, buildingAddressTextField);
        formGrid.add(buildingAddressValidationResultLabel, 1, 3);

        // Latitude field
        final Label latitudeLabel = new Label("Latitude:");
        latitudeTextField = new TextField();
        latitudeTextField.setId("latitudeTextField");
        latitudeTextField.setPromptText("enter a number between -90.00 and 90.00...");
        latitudeTextField.setTextFormatter(new TextFormatter<>(TextFormatterFilters.GEOGRAPHICAL_COORDINATES_FILTER));
        latitudeValidationResultLabel = new Label();
        latitudeValidationResultLabel.setId("latitudeValidationResultLabel");
        latitudeValidationResultLabel.getStyleClass().add(VALIDATION_RESULT_LABEL_STYLE_CLASS);
        formGrid.addRow(4, latitudeLabel, latitudeTextField);
        formGrid.add(latitudeValidationResultLabel, 1, 5);

        // Longitude field
        final Label longitudeLabel = new Label("Longitude:");
        longitudeTextField = new TextField();
        longitudeTextField.setId("longitudeTextField");
        longitudeTextField.setPromptText("enter a number between -180.00 and 180.00...");
        longitudeTextField.setTextFormatter(new TextFormatter<>(TextFormatterFilters.GEOGRAPHICAL_COORDINATES_FILTER));
        longitudeValidationResultLabel = new Label();
        longitudeValidationResultLabel.setId("longitudeValidationResultLabel");
        longitudeValidationResultLabel.getStyleClass().add(VALIDATION_RESULT_LABEL_STYLE_CLASS);
        formGrid.addRow(6, longitudeLabel, longitudeTextField);
        formGrid.add(longitudeValidationResultLabel, 1, 7);

        // Create buttons GridPane
        final GridPane buttonGrid = new GridPane();
        buttonGrid
                .getColumnConstraints()
                .addAll(new ColumnConstraints(SAVE_RESULT_LABEL_MIN_WIDTH, -1, -1), new ColumnConstraints());
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

        // CHECKSTYLE: MagicNumber ON

        //-----------------------------------------------------------------------------------------
        // NODE LOGIC
        //-----------------------------------------------------------------------------------------

        // Reacting to form field changes
        codeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            newRoof.setCode(newVal);
            final var violations = Validators.validateProperty(newRoof, "code");
            codeValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateNewRoofIsValidProperty();
        });
        buildingAddressTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            newRoof.setBuildingAddress(newVal);
            final var violations = Validators.validateProperty(newRoof, "buildingAddress");
            buildingAddressValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateNewRoofIsValidProperty();
        });
        latitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCoordinatesFromTextFields();
            updateCoordinateValidationLabels();
            updateNewRoofIsValidProperty();
        });
        longitudeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCoordinatesFromTextFields();
            updateCoordinateValidationLabels();
            updateNewRoofIsValidProperty();
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

    private void updateCoordinatesFromTextFields() {
        final var lat = parseCoordinate(latitudeTextField.getText());
        final var lon = parseCoordinate(longitudeTextField.getText());
        if (lat != null || lon != null) {
            newRoof.setCoordinates(new Coordinates(lat, lon));
        } else {
            newRoof.setCoordinates(null);
        }
    }

    private Double parseCoordinate(final String text) {
        final var coordinateString = StringUtils.stripToNull(text);
        if (coordinateString == null) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    private void updateCoordinateValidationLabels() {
        final var coordinatesViolations = Validators.validateProperty(newRoof, "coordinates");
        if (!coordinatesViolations.isEmpty()) {
            final var coordinatesViolationsString = ConstraintViolations.prettyPrintViolations(coordinatesViolations);
            latitudeValidationResultLabel.setText(coordinatesViolationsString);
            longitudeValidationResultLabel.setText(coordinatesViolationsString);
        } else if (newRoof.getCoordinates() != null) {
            latitudeValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(
                    Validators.validateProperty(
                            newRoof,
                            "coordinates.latitude"
                    ))
            );
            longitudeValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(
                    Validators.validateProperty(
                            newRoof,
                            "coordinates.longitude"
                    ))
            );
        }
    }

    private void updateNewRoofIsValidProperty() {
        final var isValid = Validators.validate(newRoof).isEmpty();
        editedRoofIsValidProperty.set(isValid);
    }

    /**
     * Set the {@link Roof} to be edited. If {@link null} a new {@link Roof} will be created.
     *
     * @param roof a {@link Roof}.
     */
    public void setRoof(final Roof roof) {
        this.roof = roof == null ? null : new Roof(roof);
        this.newRoof = roof == null ? new Roof() : new Roof(roof);

        // setting form title based on whether we are editing or creating a new roof
        if (roof != null) {
            titleLabel.setText("Edit roof");
        } else {
            titleLabel.setText("Create new roof");
        }

        // populating form fields
        codeTextField.setText(newRoof.getCode() != null ? newRoof.getCode() : "");
        buildingAddressTextField.setText(newRoof.getBuildingAddress() != null ? newRoof.getBuildingAddress() : "");

        final var coords = newRoof.getCoordinates();
        latitudeTextField.setText(coords != null && coords.getLatitude() != null
                ? String.valueOf(coords.getLatitude())
                : "");
        longitudeTextField.setText(coords != null && coords.getLongitude() != null
                ? String.valueOf(coords.getLongitude())
                : "");

        // clearing validation labels
        codeValidationResultLabel.setText("");
        buildingAddressValidationResultLabel.setText("");
        latitudeValidationResultLabel.setText("");
        longitudeValidationResultLabel.setText("");

        // disabling the codeTextField if editing an existing roof
        codeTextField.setDisable(roof != null);
    }

    /**
     * Event types for {@link RoofFormNode} events.
     */
    public static final class EventTypes {

        /**
         * Event fired when a roof is saved (either created or updated) in the form. The event's payload contains the
         * saved roof.
         */
        public static final EventType<SaveEvent<Roof>> ROOF_SAVED = new EventType<>(Event.ANY, "ROOF_SAVED");

        private EventTypes() {
        }

    }

}
