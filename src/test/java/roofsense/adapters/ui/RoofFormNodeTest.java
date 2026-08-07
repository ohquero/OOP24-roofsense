package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.avaje.inject.BeanScope;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import roofsense.entities.Coordinates;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.Matchers.blankString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

// CHECKSTYLE: MultipleStringLiterals OFF

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class RoofFormNodeTest {

    private static final String SAVE_BUTTON_NQ = "#saveButton";
    private static final String CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";
    private static final String LATITUDE_TEXT_FIELD_NQ = "#latitudeTextField";
    private static final String LONGITUDE_TEXT_FIELD_NQ = "#longitudeTextField";
    private static final String CODE_VALIDATION_RESULT_LABEL_NQ = "#codeValidationResultLabel";
    private static final String BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ = "#buildingAddressValidationResultLabel";
    private static final String LATITUDE_VALIDATION_RESULT_LABEL_NQ = "#latitudeValidationResultLabel";
    private static final String LONGITUDE_VALIDATION_RESULT_LABEL_NQ = "#longitudeValidationResultLabel";
    private static final String OPERATION_RESULT_LABEL_NQ = "#saveResultLabel";
    private static final String ROOF_SAVED_SUCCESSFULLY_MESSAGE = "Roof saved successfully";
    private static final String EQUAL_ROOF_ALREADY_EXISTS_MESSAGE = "An equal Roof already exists";

    @Nested
    class CreateNewRoofTest extends AbstractNodeTest {

        @SuppressFBWarnings("UwF")
        private RoofsManager manager;
        @SuppressFBWarnings("UwF")
        private RoofFormNode form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;

            final var injector = BeanScope.builder().build();

            manager = injector.get(RoofsManager.class);

            form = injector.get(RoofFormNode.class);
            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            stage.show();
        }

        @Test
        void startupFormStatusTest() {
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(LATITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(LONGITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));

            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void formFilledWithValidDataShouldCreateNewRoofTest(final FxRobot robot)
                throws ExecutionException, InterruptedException {
            // given
            final var roofCode = "R-01";
            final var roofBuildingAddress = "Main Street 1";
            final var roofLatitude = 12.34;
            final var roofLongitude = 56.78;
            final var returnedRoofFuture = new CompletableFuture<Roof>();
            form.addEventHandler(
                    RoofFormNode.EventTypes.ROOF_SAVED,
                    event -> returnedRoofFuture.complete(event.getObject())
            );

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).eraseText(roofCode.length());
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).eraseText(roofBuildingAddress.length());
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(roofBuildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLatitude));
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLongitude));

            // then
            verifyThat(SAVE_BUTTON_NQ, isEnabled());

            // when
            robot.clickOn(SAVE_BUTTON_NQ);

            // then
            final var returnedRoof = returnedRoofFuture.get();
            assertEquals(roofCode, returnedRoof.getCode());
            assertEquals(roofBuildingAddress, returnedRoof.getBuildingAddress());
            assertEquals(roofLatitude, returnedRoof.getCoordinates().getLatitude());
            assertEquals(roofLongitude, returnedRoof.getCoordinates().getLongitude());
            final var persistedRoofs = manager.getAll();
            assertEquals(1, persistedRoofs.size());
            assertEquals(returnedRoof, persistedRoofs.iterator().next());
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(ROOF_SAVED_SUCCESSFULLY_MESSAGE));
        }

        @Test
        void invalidRoofCodeShouldDisableSaveTest(final FxRobot robot) {
            // given
            final var roofCode = "invalid_roof_code";
            final var roofBuildingAddress = "Main Street 1";
            final var roofLatitude = 12.34;
            final var roofLongitude = 56.78;

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(roofBuildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLatitude));
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLongitude));

            // then
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void invalidRoofBuildingAddressShouldDisableSaveTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var roofBuildingAddress = "    ";
            final var roofLatitude = 12.34;
            final var roofLongitude = 56.78;

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(roofBuildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLatitude));
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write(String.valueOf(roofLongitude));

            // then
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void roofLatitudeOutOfRangeShouldDisableSaveTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var buildingAddress = "Main Street 1";
            final var latitude = "91.0";
            final var longitude = "12.34";

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write(latitude);
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write(longitude);

            // then
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void latitudeSetWithoutLongitudeShouldDisableSaveTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var buildingAddress = "Main Street 1";
            final var latitude = "12.34";
            final var longitude = "181.34";

            // when
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write(latitude);
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write(longitude);

            // then
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(blankString()));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(not(blankString())));
            verifyThat(SAVE_BUTTON_NQ, isDisabled());
        }

        @Test
        void clickingSaveButtonShouldDisplayErrorWhenRoofAlreadyExistsTest(final FxRobot robot) {
            //given
            final var roofCode = "R-01";
            final var roofBuildingAddress = "Main Street 1";
            final var roofCoordinates = new Coordinates(12.34, 56.78);
            final var existingRoof = new Roof(roofCode, roofBuildingAddress, roofCoordinates);
            manager.save(existingRoof);

            // when
            robot.clickOn(CODE_VALIDATION_RESULT_LABEL_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write("Main Street 2");
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write("87.65");
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write("43.21");
            robot.clickOn(SAVE_BUTTON_NQ);

            // then
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(EQUAL_ROOF_ALREADY_EXISTS_MESSAGE));
            final var persistedRoofs = manager.getAll();
            assertEquals(1, persistedRoofs.size());
            assertEquals(existingRoof, persistedRoofs.iterator().next());
        }

    }

    @Nested
    class EditRoofTest extends AbstractNodeTest {

        @SuppressFBWarnings("UwF")
        private RoofsManager manager;
        @SuppressFBWarnings("UwF")
        private RoofFormNode form;
        private Stage stage;

        @Override
        protected Stage getStage() {
            return stage;
        }

        @Start
        void start(final Stage testfxStage) {
            stage = testfxStage;

            final var injector = BeanScope.builder().build();
            manager = injector.get(RoofsManager.class);
            form = injector.get(RoofFormNode.class);

            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            stage.show();
        }

        @Test
        void formFilledWithValidDataShouldModifyRoofTest(final FxRobot robot)
                throws ExecutionException, InterruptedException {
            // given
            final var roofCode = "R-01";
            final var roofBuildingAddress = "Main Street 1";
            final var roofCoordinates = new Coordinates(1.2, 3.4);
            final var existingRoof = new Roof(roofCode, roofBuildingAddress, roofCoordinates);
            manager.save(existingRoof);
            Platform.runLater(() -> form.setRoof(existingRoof));
            waitForFxEvents();
            final var formReturnedRoofFuture = new CompletableFuture<Roof>();
            form.addEventHandler(
                    RoofFormNode.EventTypes.ROOF_SAVED,
                    event -> formReturnedRoofFuture.complete(event.getObject())
            );
            final var textToAdd = " edited";

            // when - editing the roof attributes
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(textToAdd);
            robot.clickOn(SAVE_BUTTON_NQ);

            // then
            verifyThat(OPERATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(ROOF_SAVED_SUCCESSFULLY_MESSAGE));

            final var returnedRoof = formReturnedRoofFuture.get();
            assertTrue(returnedRoof.getBuildingAddress().contains(textToAdd));

            final var persistedRoofs = manager.getAll();
            assertEquals(1, persistedRoofs.size());

            final var persistedUpdatedRoof = persistedRoofs.iterator().next();
            assertEquals(returnedRoof, persistedUpdatedRoof);
            assertEquals(returnedRoof.getBuildingAddress(), persistedUpdatedRoof.getBuildingAddress());
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

            final var injector = BeanScope.builder().build();
            form = injector.get(RoofFormNode.class);

            final var scene = new Scene(form);
            scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
            testfxStage.setScene(scene);
            stage.show();
        }

        @Test
        void setRoofToNullShouldResetNodeToCreateModeTest(final FxRobot robot) {
            // given
            final var roofCode = "R-01";
            final var buildingAddress = "Main Street 1";
            robot.clickOn(CODE_TEXT_FIELD_NQ).write(roofCode);
            robot.clickOn(BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
            robot.clickOn(LATITUDE_TEXT_FIELD_NQ).write("12.34");
            robot.clickOn(LONGITUDE_TEXT_FIELD_NQ).write("56.78");

            // when - set roof to null
            Platform.runLater(() -> form.setRoof(null));
            waitForFxEvents();

            // then - verify form is reset to create mode
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(LATITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(LONGITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_TEXT_FIELD_NQ, isEnabled());
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(LATITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(LONGITUDE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
        }

        @Test
        void setRoofWithValidRoofShouldSwitchToEditModeTest(final FxRobot robot) {
            // given
            final var roof = new Roof("R-02", "Main Street 2", new Coordinates(2.0, 2.0));

            // when
            Platform.runLater(() -> form.setRoof(roof));
            waitForFxEvents();

            // then - verify form is populated with roof data
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof.getBuildingAddress()));
            verifyThat(LATITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText("2.0"));
            verifyThat(LONGITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText("2.0"));
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());
            verifyThat(CODE_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
            verifyThat(BUILDING_ADDRESS_VALIDATION_RESULT_LABEL_NQ, LabeledMatchers.hasText(""));
        }

        @Test
        void setRoofMultipleTimesShouldUpdateFormStateProperlyTest(final FxRobot robot) {
            // given
            final var roof1 = new Roof("R-01", "Main Street 1", new Coordinates(1.2, 3.4));
            final var roof2 = new Roof("R-01", "Main Street 1", new Coordinates(5.6, 7.8));

            // when - set first roof
            Platform.runLater(() -> form.setRoof(roof1));
            waitForFxEvents();

            // then - verify first roof is set
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof1.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof1.getBuildingAddress()));
            verifyThat(
                    LATITUDE_TEXT_FIELD_NQ,
                    TextInputControlMatchers.hasText(roof1.getCoordinates().getLatitude().toString())
            );
            verifyThat(
                    LONGITUDE_TEXT_FIELD_NQ,
                    TextInputControlMatchers.hasText(roof1.getCoordinates().getLongitude().toString())
            );
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());

            // when - set second roof
            Platform.runLater(() -> form.setRoof(roof2));
            waitForFxEvents();

            // then - verify second roof replaces first
            verifyThat(CODE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof2.getCode()));
            verifyThat(BUILDING_ADDRESS_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(roof2.getBuildingAddress()));
            verifyThat(
                    LATITUDE_TEXT_FIELD_NQ,
                    TextInputControlMatchers.hasText(roof2.getCoordinates().getLatitude().toString())
            );
            verifyThat(
                    LONGITUDE_TEXT_FIELD_NQ,
                    TextInputControlMatchers.hasText(roof2.getCoordinates().getLongitude().toString())
            );
            verifyThat(CODE_TEXT_FIELD_NQ, isDisabled());
        }

        @Test
        void setRoofBackToNullShouldSwitchBackToCreateModeTest(final FxRobot robot) {
            // given
            final var roof = new Roof("R-01", "Main Street 1", new Coordinates(1.2, 3.4));

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
            verifyThat(LATITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(LONGITUDE_TEXT_FIELD_NQ, TextInputControlMatchers.hasText(""));
            verifyThat(CODE_TEXT_FIELD_NQ, isEnabled());
        }

    }

}
