package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

class CreateRoofFormTest extends AbstractNodeTest {

    private static final String SAVE_BUTTON_NQ = "#commitButton";
    private static final String CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";
    private static final String CODE_VALIDATION_RESULT_LABEL_NQ = "#codeValidationResultLabel";
    private static final String BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ = "#buildingAddressValidationResultLabel";
    private static final String OPERATION_RESULT_LABEL_NQ = "#operationResultLabel";

    @SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    private ManageRoof manager;

    @Override
    protected AbstractNode getNode() {
        return CreateRoofForm.build(manager);
    }

    @Start
    void start(final Stage stage) {
        this.manager = mock(ManageRoof.class);
        stage.setScene(new Scene((Parent) getNode().getRootNode()));
        stage.show();
    }

    @Test
    void saveButtonShouldBeDisabledByDefaultTest(final FxRobot robot) {
        verifyThat(SAVE_BUTTON_NQ, isDisabled());
    }

    @Test
    void saveButtonShouldBeEnabledWhenFormIsValidTest(final FxRobot robot) {
        // given
        final var roofCode = "R-01";
        final var buildingAddress = "Main Street 1";

        // when
        robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

        // then
        verifyThat(SAVE_BUTTON_NQ, isEnabled());
        verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, hasText(""));
        verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, hasText(""));
    }

    @Test
    void saveButtonShouldBeDisabledWhenFormIsNotValidTest(final FxRobot robot) {
        // given
        final var roofCode = "invalid_roof_code";
        final var buildingAddress = "";

        // when
        robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

        // then
        verifyThat(SAVE_BUTTON_NQ, isDisabled());
        verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, not(hasText("")));
        verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, not(hasText("")));
    }

    @Test
    void clickingSaveButtonShouldSaveNewRoofTest(final FxRobot robot) {
        // given
        when(manager.exists(any(Roof.class))).thenReturn(false);
        final var roofCode = "R-02";
        final var buildingAddress = "Main Street 2";

        // when
        robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then
        verify(manager).addNew(any(Roof.class));
        verifyThat(OPERATION_RESULT_LABEL_NQ, hasText("Roof created successfully"));
    }

    @Test
    void clickingSaveButtonShouldDisplayErrorWhenRoofAlreadyExistsTest(final FxRobot robot) {
        //given
        when(manager.exists(any(Roof.class))).thenReturn(true);
        final var roofCode = "R-03";
        final var buildingAddress = "Main Street 3";

        // when
        robot.clickOn(CODE_VALIDATION_RESULT_LABEL_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then
        verify(manager, never()).addNew(any(Roof.class));
        verifyThat(OPERATION_RESULT_LABEL_NQ, hasText("An equal Roof already exists"));
    }

}
