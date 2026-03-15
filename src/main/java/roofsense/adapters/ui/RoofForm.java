package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private final ObjectProperty<Roof> roofProperty;
    private Roof editedRoof;

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
        this.roofProperty = new SimpleObjectProperty<>();
    }

    /**
     * Creates a new {@link RoofForm} instance creating a new {@link Roof} entity.
     *
     * @param manageRoof the use case for managing {@link Roof} entities. Must not be {@code null}
     *
     * @return a new {@link RoofForm}
     */
    public static RoofForm build(final ManageRoof manageRoof) {
        final var loader = loadFXMLFile("javafx/RoofForm.fxml", param -> new RoofForm(manageRoof));
        final RoofForm instance = loader.getController();
        instance.setRoof(null);
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
            final var isValid = Validators.validate(editedRoof).isEmpty();
            editedRoofIsValidProperty.set(isValid);
        };
        codeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            editedRoof.setCode(newVal);
            final var violations = Validators.validateProperty(editedRoof, "code");
            codeValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateEditedRoofIsValidProperty.run();
        });
        buildingAddressTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            editedRoof.setBuildingAddress(newVal);
            final var violations = Validators.validateProperty(editedRoof, "buildingAddress");
            buildingAddressValidationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
            updateEditedRoofIsValidProperty.run();
        });

        // Disable the commit button if the form is not valid
        saveButton.disableProperty().bind(editedRoofIsValidProperty.not());

        // Save button on click action
        saveButton.setOnAction(actionEvent -> {
            if (manager.exists(editedRoof)) {
                saveResultLabel.setText("An equal Roof already exists");
                saveResultLabel.getStyleClass().setAll("error");
                return;
            }

            if (roofProperty.getValue() == null) {
                manager.addNew(editedRoof);
                saveResultLabel.setText("Roof created successfully");
                saveResultLabel.getStyleClass().setAll("success");
            } else {
                throw new UnsupportedOperationException("Roof editing is not supported yet");
            }

            // Re-initialize the form with the persisted roof
            setRoof(editedRoof);
        });
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Node getRootNode() {
        return roofFormRootNode;
    }

    private void setRoof(final Roof roof) {
        this.roofProperty.set(roof);
        this.editedRoof = roof == null ? new Roof(null, null) : new Roof(roof);
        codeTextField.setText(editedRoof.getCode() != null ? editedRoof.getCode() : "");
        buildingAddressTextField.setText(
                editedRoof.getBuildingAddress() != null ? editedRoof.getBuildingAddress() : ""
        );
    }

    /**
     * Returns the property that holds the last saved roof.
     * You can add listeners to this property to be notified when a roof is saved.
     *
     * @return the saved roof property
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public ObjectProperty<Roof> roofProperty() {
        return this.roofProperty;
    }

}
