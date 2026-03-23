package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.Parent;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
class RoofFormTest {

    private static final String SAVE_BUTTON_NQ = "#saveButton";
    private static final String CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";
    private static final String CODE_VALIDATION_RESULT_LABEL_NQ = "#codeValidationResultLabel";
    private static final String BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ = "#buildingAddressValidationResultLabel";
    private static final String OPERATION_RESULT_LABEL_NQ = "#saveResultLabel";

    @Nested
    class CreateNewRoofTest extends AbstractNodeTest {

        private RoofsManager manager;
        private RoofForm form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;

            manager = mock(RoofsManager.class);

            form = RoofForm.create(manager);

            testfxStage.setScene(new Scene((Parent) form.getRootNode()));
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
            form.addEventHandler(RoofForm.EventTypes.ROOF_CREATED, event -> createdRoof.set(event.getObject()));

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
            verify(manager).add(roofCaptor.capture());
            assertSame(createdRoof.get(), roofCaptor.getValue());
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("Roof created successfully"));
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
            verify(manager, never()).add(any(Roof.class));
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("An equal Roof already exists"));
        }

    }

    @Nested
    class EditRoofTest extends AbstractNodeTest {

        private RoofsManager manager;
        private Roof initialRoof;
        private RoofForm form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            this.stage = testfxStage;
            manager = mock(RoofsManager.class);
            doAnswer(invocation -> new Roof(invocation.getArgument(0))).when(manager).update(any(Roof.class));

            initialRoof = new Roof("R-02", "Main Street 2");

            form = RoofForm.create(manager, initialRoof);

            testfxStage.setScene(new Scene((Parent) form.getRootNode()));
            testfxStage.show();
        }

        @Test
        void formFilledWithValidDataShouldModifyRoofTest(final FxRobot robot) {
            // given
            when(manager.exists(any(Roof.class))).thenReturn(true);
            final var updatedRoof = new AtomicReference<Roof>();
            form.addEventHandler(RoofForm.EventTypes.ROOF_UPDATED, event -> updatedRoof.set(event.getObject()));

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
            verify(manager).update(roofCaptor.capture());
            assertEquals(updatedRoof.get(), roofCaptor.getValue());

            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText("Roof updated successfully"));
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

}
