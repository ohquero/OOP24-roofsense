package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import roofsense.entities.Roof;
import roofsense.utils.ConstraintViolations;
import roofsense.utils.Validators;

import java.util.Objects;

/**
 * Base class for all the UI components providing a form for editing {@link Roof} entities.
 */
public abstract class AbstractRoofForm extends AbstractNode {

    private final Roof roof;
    private final BooleanProperty roofValidityProperty;

    @FXML
    private Node rootNode;
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
    private Label operationResultLabel;
    @FXML
    private Button commitButton;

    /**
     * Default constructor.
     *
     * @param roof the roof entity to edit or create
     */
    protected AbstractRoofForm(final Roof roof) {
        this.roof = Objects.requireNonNull(roof);
        this.roofValidityProperty = new SimpleBooleanProperty(false);
        updateIsRoofValid();
    }

    /**
     * Method invoked after the FXML file has been loaded.
     *
     * <p>
     * Subclasses can override this method to add additional initialization logic,
     * but should call {@code super.initialize()} first.
     */
    @FXML
    protected void initialize() {
        // Initializing text fields with existing roof data
        codeTextField.setText(roof.getCode());
        updateRoofAttributeValidationLabel("code", codeValidationResultLabel);
        buildingAddressTextField.setText(roof.getBuildingAddress());
        updateRoofAttributeValidationLabel("buildingAddress", buildingAddressValidationResultLabel);

        // Syncing roof attributes state with form fields state
        codeTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            roof.setCode(StringUtils.trimToNull(newVal));
            updateRoofAttributeValidationLabel("code", codeValidationResultLabel);
            updateIsRoofValid();
        });
        buildingAddressTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            roof.setBuildingAddress(StringUtils.trimToNull(newVal));
            updateRoofAttributeValidationLabel("buildingAddress", buildingAddressValidationResultLabel);
            updateIsRoofValid();
        });

        // Disable the commit button if the form is not valid
        commitButton.disableProperty().bind(roofValidityProperty.not());
    }

    private void updateRoofAttributeValidationLabel(final String code, final Label validationResultLabel) {
        final var violations = Validators.validateProperty(roof, code);
        validationResultLabel.setText(ConstraintViolations.prettyPrintViolations(violations));
    }

    private void updateIsRoofValid() {
        final var isValid = Validators.validate(roof).isEmpty();
        roofValidityProperty.set(isValid);
    }

    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public final Node getRootNode() {
        return rootNode;
    }

    /**
     * Returns the {@link Roof} entity being edited.
     *
     * @return a {@link Roof}
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public final Roof getRoof() {
        return roof;
    }

    /**
     * Returns the {@link Label} displaying the title.
     *
     * @return a {@link Label}
     */
    protected final Label getTitleLabel() {
        return titleLabel;
    }

    /**
     * Returns the {@link Label} displaying the edit operation result.
     *
     * @return a {@link Label}
     */
    protected final Label getOperationResultLabel() {
        return operationResultLabel;
    }

    /**
     * Returns the {@link Button} the user clicks to commit the edit operation.
     *
     * @return a {@link Button}
     */
    protected final Button getCommitButton() {
        return commitButton;
    }

}
