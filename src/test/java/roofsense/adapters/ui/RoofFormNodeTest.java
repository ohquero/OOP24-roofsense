package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
class RoofFormNodeTest {

    private static final String SAVE_BUTTON_NQ = "#saveButton";
    private static final String CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";
    private static final String CODE_VALIDATION_RESULT_LABEL_NQ = "#codeValidationResultLabel";
    private static final String BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ = "#buildingAddressValidationResultLabel";
    private static final String OPERATION_RESULT_LABEL_NQ = "#saveResultLabel";

    @Nested
    class CreateNewRoofTest extends AbstractNodeTest {

        private RoofsManager manager;
        private RoofFormNode form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;

            manager = mock(RoofsManager.class);
            doAnswer(invocation -> invocation.getArgument(0)).when(manager).save(any(Roof.class));

            form = new RoofFormNode(manager);

            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            testfxStage.show();
        }

        @Test
        void startupFormStatusTest() {
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));

            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void formFilledWithValidDataShouldCreateNewRoofTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var buildingAddress = "Main Street 1";
            when(manager.exists(any(Roof.class))).thenReturn(false);
            final var createdRoof = new AtomicReference<>();
            form.addEventHandler(RoofFormNode.EventTypes.ROOF_SAVED, event -> createdRoof.set(event.getObject()));

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).eraseText(roofCode.length());
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).eraseText(buildingAddress.length());
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

            // then
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roofCode));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(buildingAddress));
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(SAVE_BUTTON_NQ, isEnabled());

            // when
            robot.clickOn(SAVE_BUTTON_NQ);

            // then
            final var roofCaptor = ArgumentCaptor.forClass(Roof.class);
            verify(manager).save(roofCaptor.capture());
            assertEquals(createdRoof.get(), roofCaptor.getValue());
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("Roof saved successfully"));
        }

        @Test
        void formFilledWithInvalidDataShouldNotAllowToSaveTest(final FxRobot robot) {
            // given
            final var roofCode = "invalid_roof_code";
            final var buildingAddress = "   ";

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

            // then
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
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
            verify(manager, never()).save(any(Roof.class));
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("An equal Roof already exists"));
        }

    }

    @Nested
    class EditRoofTest extends AbstractNodeTest {

        private RoofsManager manager;
        private Roof initialRoof;
        private RoofFormNode form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;
            manager = mock(RoofsManager.class);
            doAnswer(invocation -> invocation.getArgument(0)).when(manager).save(any(Roof.class));

            initialRoof = new Roof("R-02", "Main Street 2");

            form = new RoofFormNode(manager);
            form.setRoof(initialRoof);

            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            testfxStage.show();
        }

        @Test
        void formFilledWithValidDataShouldModifyRoofTest(final FxRobot robot) {
            // given
            when(manager.exists(any(Roof.class))).thenReturn(true);
            final var updatedRoof = new AtomicReference<Roof>();
            form.addEventHandler(RoofFormNode.EventTypes.ROOF_SAVED, event -> updatedRoof.set(event.getObject()));

            // then
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(initialRoof.getCode()));
            verifyThat(
                    BUILDING_ADDRESS_TEXT_FIELD_NQ,
                    TextInputControlMatchers.hasText(initialRoof.getBuildingAddress())
            );
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(SAVE_BUTTON_NQ, isEnabled());

            // given
            final var newRoofBuildingAddress = "Modified Street 2";

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).eraseText(initialRoof.getCode().length());
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(newRoofBuildingAddress);
            robot.clickOn(SAVE_BUTTON_NQ);

            // then
            assertEquals(newRoofBuildingAddress, updatedRoof.get().getBuildingAddress());

            final var roofCaptor = ArgumentCaptor.forClass(Roof.class);
            verify(manager).save(roofCaptor.capture());
            assertEquals(updatedRoof.get(), roofCaptor.getValue());

            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("Roof saved successfully"));
        }

        @Test
        void formFilledWithInvalidDataShouldNotAllowToSaveTest(final FxRobot robot) {
            // given
            final var buildingAddress = "   ";

            // when
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).eraseText(initialRoof.getBuildingAddress().length());
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

            // then
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

    }

    @Nested
    class SetRoofTest extends AbstractNodeTest {

        private RoofFormNode form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;

            form = new RoofFormNode(mock(RoofsManager.class));

            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            testfxStage.show();
        }

        @Test
        void setRoofToNullShouldResetNodeToCreateModeTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var buildingAddress = "Main Street 1";
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);

            // when - set roof to null
            Platform.runLater(() -> form.setRoof(null));
            waitForFxEvents();

            // then - verify form is reset to create mode
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_TEXT_FIELD_NQ, isEnabled());
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not("")));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not("")));
        }

        @Test
        void setRoofWithValidRoofShouldSwitchToEditModeTest(final FxRobot robot) {
            // given
            final var roof = new Roof("R-02", "Main Street 2");

            // when
            Platform.runLater(() -> form.setRoof(roof));
            waitForFxEvents();

            // then - verify form is populated with roof data
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof.getBuildingAddress()));
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
        }

        @Test
        void setRoofMultipleTimesShouldUpdateFormStateProperlyTest(final FxRobot robot) {
            // given
            final var roof1 = new Roof("R-05", "Street 5");
            final var roof2 = new Roof("R-06", "Street 6");

            // when - set first roof
            Platform.runLater(() -> form.setRoof(roof1));
            waitForFxEvents();

            // then - verify first roof is set
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof1.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof1.getBuildingAddress()));
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());

            // when - set second roof
            Platform.runLater(() -> form.setRoof(roof2));
            waitForFxEvents();

            // then - verify second roof replaces first
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof2.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof2.getBuildingAddress()));
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());
        }

        @Test
        void setRoofThenSetToNullShouldSwitchBackToCreateModeTest(final FxRobot robot) {
            // given
            final var roof = new Roof("R-07", "Street 7");

            // when - set a roof
            Platform.runLater(() -> form.setRoof(roof));
            waitForFxEvents();

            // then - verify edit mode
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof.getCode()));
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());

            // when - set to null
            Platform.runLater(() -> form.setRoof(null));
            waitForFxEvents();

            // then - verify back to create mode
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_TEXT_FIELD_NQ, isEnabled());
        }

    }

}
