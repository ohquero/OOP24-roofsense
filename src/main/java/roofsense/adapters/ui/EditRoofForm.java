package roofsense.adapters.ui;

import roofsense.entities.Roof;

/**
 * JavaFX node for editing existing {@link Roof} entities.
 */
public final class EditRoofForm extends AbstractRoofForm {

    private EditRoofForm(final Roof roofToEdit) {
        super(roofToEdit);
    }

    /**
     * Builds and initializes an EditRoofNode instance.
     *
     * @param roofToEdit the {@link Roof} entity to edit
     *
     * @return a new {@link EditRoofForm} instance
     */
    public static EditRoofForm build(final Roof roofToEdit) {
        final var loader = loadFXMLFile("javafx/RoofForm.fxml", param -> new EditRoofForm(roofToEdit));
        final EditRoofForm instance = loader.getController();
        instance.getTitleLabel().setText("Edit roof " + instance.getRoof().getCode());
        return instance;
    }

    @Override
    protected void initialize() {
        super.initialize();

        getCommitButton().setText("Save edits");
    }

}
