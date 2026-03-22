package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import roofsense.adapters.ui.events.SaveEvent;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;
import roofsense.utils.ConstraintViolations;
import roofsense.utils.Validators;

import java.util.Objects;

/**
 * Form for editing {@link Roof} objects.
 */
public final class RoofForm extends AbstractNode {

    private final ManageRoof manager;
    private final BooleanProperty editedRoofIsValidProperty;
    private Roof roof;
    private Roof newRoof;
    @FXML
    private Node roofFormRootNode;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField codeTextField;
    @FXML
    private Label codeValidationResultLabel;
    @FXML
    private TextField buildingAddressTextField;
    @FXML
    private Label buildingAddressValidationResultLabel;
    @FXML
    private Label saveResultLabel;
    @FXML
    private Button saveButton;

    private RoofForm(final ManageRoof manager) {
        this.manager = Objects.requireNonNull(manager);
        this.editedRoofIsValidProperty = new SimpleBooleanProperty();
        this.newRoof = new Roof(null, null);
    }

    /**
     * Creates a new {@link RoofForm} instance creating a new {@link Roof} entity.
     *
     * @param manager the use case for managing {@link Roof} entities. Must not be {@code null}
     *
     * @return a new {@link RoofForm}
     */
    public static RoofForm create(final ManageRoof manager) {
        return createInstance(manager, null);
    }

    /**
     * Creates a new {@link RoofForm} instance editing an existing {@link Roof} entity.
     *
     * @param manager    the use case for managing {@link Roof} entities. Must not be {@code null}
     * @param roofToEdit the roof to be edited. Must not be {@code null}
     *
     * @return a new {@link RoofForm}
     */
    public static RoofForm create(final ManageRoof manager, final Roof roofToEdit) {
        Objects.requireNonNull(roofToEdit, "roof to edit must not be null");
        return createInstance(manager, roofToEdit);
    }

    private static RoofForm createInstance(final ManageRoof manager, final Roof roof) {
        Objects.requireNonNull(manager, "manager must not be null");
        final var loader = loadFXMLFile("javafx/RoofForm.fxml", param -> new RoofForm(manager));
        final RoofForm instance = loader.getController();
        instance.setRoof(roof);
        return instance;
    }

    /**
     * Method invoked after the FXML file has been loaded.
     */
    @FXML
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void initialize() {
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

            if (roof == null) {
                if (manager.exists(newRoof)) {
                    saveResultLabel.setText("An equal Roof already exists");
                    saveResultLabel.getStyleClass().setAll("error");
                    return;
                }

                manager.addNew(newRoof);
                saveResultLabel.setText("Roof created successfully");
                saveResultLabel.getStyleClass().setAll("success");
                this.fireEvent(new SaveEvent<>(newRoof, EventTypes.ROOF_CREATED));
            } else {
                newRoof = manager.update(newRoof);
                saveResultLabel.setText("Roof updated successfully");
                saveResultLabel.getStyleClass().setAll("success");
                fireEvent(new SaveEvent<>(newRoof, EventTypes.ROOF_UPDATED));
            }

            // Re-initialize the form with the persisted roof
            setRoof(newRoof);
        });
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Node getRootNode() {
        return roofFormRootNode;
    }

    private void setRoof(final Roof roof) {
        this.roof = roof;
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
     * Event types for {@link RoofForm} events.
     */
    public static final class EventTypes {

        public static final EventType<SaveEvent<Roof>> ROOF_CREATED = new EventType<>(Event.ANY, "ROOF_CREATED");
        public static final EventType<SaveEvent<Roof>> ROOF_UPDATED = new EventType<>(Event.ANY, "ROOF_UPDATED");

        private EventTypes() {
        }

    }

}
