package roofsense.adapters.ui;

import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import java.util.Objects;

/**
 * UI component for creating new {@link Roof} entities.
 */
public final class CreateRoofForm extends AbstractRoofForm {

    private final ManageRoof manageRoof;

    /**
     * Default constructor.
     *
     * @param manageRoof the use case for managing roofs
     */
    private CreateRoofForm(final ManageRoof manageRoof) {
        super(new Roof(null, null));
        this.manageRoof = Objects.requireNonNull(manageRoof);
    }

    /**
     * Creates a new {@link CreateRoofForm} instance.
     *
     * @param manageRoof the use case for managing roofs
     *
     * @return a new {@link CreateRoofForm}
     */
    public static CreateRoofForm build(final ManageRoof manageRoof) {
        final var loader = loadFXMLFile("javafx/RoofForm.fxml", param -> new CreateRoofForm(manageRoof));
        final CreateRoofForm instance = loader.getController();
        instance.getTitleLabel().setText("Create new roof");
        return instance;
    }

    @Override
    protected void initialize() {
        super.initialize();

        getCommitButton().setText("Create new roof");
        getCommitButton().setOnAction(event -> createNewRoof());
    }

    private void createNewRoof() {
        if (manageRoof.exists(getRoof())) {
            getOperationResultLabel().setText("An equal Roof already exists");
            getOperationResultLabel().getStyleClass().setAll("error");
            return;
        }

        manageRoof.addNew(getRoof());
        getOperationResultLabel().setText("Roof created successfully");
        getOperationResultLabel().getStyleClass().setAll("success");
    }

}
