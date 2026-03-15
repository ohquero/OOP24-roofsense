package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.TextInputControlMatchers;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

class RoofFormTest extends AbstractNodeTest {

    private static final String SAVE_BUTTON_NQ = "#saveButton";
    private static final String CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";
    private static final String CODE_VALIDATION_RESULT_LABEL_NQ = "#codeValidationResultLabel";
    private static final String BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ = "#buildingAddressValidationResultLabel";
    private static final String OPERATION_RESULT_LABEL_NQ = "#saveResultLabel";

    @SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    private ManageRoof manager;
    private Stage stage;
    private RoofForm form;

    @Override
    protected Stage getStage() {
        return stage;
    }

    @Start
    void start(final Stage testfxStage) {
        this.manager = mock(ManageRoof.class);

        this.stage = testfxStage;
        this.form = RoofForm.build(this.manager);
        this.stage.setScene(new Scene((Parent) form.getRootNode()));
        this.stage.show();
    }

    @Test
    void verifyFormStatusAtStartup(final FxRobot robot) {
        assertNull(this.form.roofProperty().getValue());

        verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
        verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, hasText(""));
        verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
        verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, hasText(""));

        verifyThat(SAVE_BUTTON_NQ, isDisabled());
    }

    @Test
    void formFilledWithValidDataStatusTest(final FxRobot robot) {
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
    void formFilledWithInvalidDataStatusTest(final FxRobot robot) {
        // given
        final var roofCode = "invalid_roof_code";
        final var buildingAddress = "   ";

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
        final var initialRoof = form.roofProperty().getValue();

        // when
        robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then
        final var roofCaptor = ArgumentCaptor.forClass(Roof.class);
        verify(manager).addNew(roofCaptor.capture());
        assertEquals(form.roofProperty().getValue(), roofCaptor.getValue());
        assertNotEquals(initialRoof, form.roofProperty().getValue());
        verifyThat(OPERATION_RESULT_LABEL_NQ, hasText("Roof created successfully"));
    }

    @Test
    void clickingSaveButtonShouldDisplayErrorWhenRoofAlreadyExistsTest(final FxRobot robot) {
        //given
        when(manager.exists(any(Roof.class))).thenReturn(true);
        final var roofCode = "R-03";
        final var buildingAddress = "Main Street 3";
        final var initialRoof = form.roofProperty().getValue();

        // when
        robot.clickOn(CODE_VALIDATION_RESULT_LABEL_NQ).write(roofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then
        verify(manager, never()).addNew(any(Roof.class));
        assertSame(initialRoof, form.roofProperty().getValue());
        verifyThat(OPERATION_RESULT_LABEL_NQ, hasText("An equal Roof already exists"));
    }

    @Test
    //TODO: enable when roof editing will be supported
    @Disabled("This test is currently disabled because roof editing is not supported yet")
    void multipleModificationsBehaviourTest(final FxRobot robot) {
        // given
        when(manager.exists(any(Roof.class))).thenReturn(false);
        final var firstRoofCode = "R-04";
        final var firstRoofBuildingAddress = "Main Street 4";

        // when
        robot.clickOn(CODE_TEXT_FIELD_NQ).write(firstRoofCode);
        robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(firstRoofBuildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then
        final var firstRoofCaptor = ArgumentCaptor.forClass(Roof.class);
        verify(manager).addNew(firstRoofCaptor.capture());
        final Roof firstRoof = firstRoofCaptor.getValue();
        assertEquals(firstRoofCode, firstRoof.getCode());
        assertEquals(firstRoofBuildingAddress, firstRoof.getBuildingAddress());
        assertSame(firstRoof, form.roofProperty().getValue());

        // when - modify the form again with different data and save
        final var secondRoofCode = "R-05";
        final var secondRoofBuildingAddress = "Main Street 5";
        robot.clickOn(CODE_TEXT_FIELD_NQ).eraseText(firstRoofCode.length()).write(secondRoofCode);
        robot
                .clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ)
                .eraseText(firstRoofBuildingAddress.length())
                .write(secondRoofBuildingAddress);
        robot.clickOn(SAVE_BUTTON_NQ);

        // then - the second roof should be saved without affecting the first one
        final var secondRoofCaptor = ArgumentCaptor.forClass(Roof.class);
        //verify(manager).save(secondRoofCaptor.capture()); TODO: when editing is supported, this should be replaced
        final Roof secondRoof = secondRoofCaptor.getValue();
        assertEquals(secondRoofCode, secondRoof.getCode());
        assertEquals(secondRoofBuildingAddress, secondRoof.getBuildingAddress());
        assertSame(secondRoof, form.roofProperty().getValue());
    }

}
