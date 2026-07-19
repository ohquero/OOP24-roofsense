package roofsense.adapters.ui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.avaje.inject.BeanScope;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import org.testfx.service.query.EmptyNodeQueryException;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

// CHECKSTYLE: MultipleStringLiterals OFF
// CHECKSTYLE: MagicNumber OFF

/**
 * Test class for {@link RoofsRegistryNode}.
 */
class RoofsRegistryNodeTest extends AbstractNodeTest {

    private static final String ROOFS_TABLE_NQ = "#roofsTableView";
    private static final String SEARCH_ROOFS_TEXT_FIELD_NQ = "#searchRoofsTextField";
    private static final String ADD_NEW_ROOF_BUTTON_NQ = "#addRoofButton";
    private static final String EDIT_ROOF_BUTTON_NQ = "#editRoofButton";
    private static final String REMOVE_ROOF_BUTTON_NQ = "#removeRoofButton";

    private static final String ROOF_FORM_ROOT_NODE_NQ = "#roofFormNode";
    private static final String ROOF_FORM_SAVE_BUTTON_NQ = "#saveButton";
    private static final String ROOF_FORM_CODE_TEXT_FIELD_NQ = "#codeTextField";
    private static final String ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ = "#buildingAddressTextField";

    @SuppressFBWarnings("UwF")
    private List<Roof> roofs;
    @SuppressFBWarnings("UwF")
    private RoofsManager manager;
    private Stage stage;

    @Start
    void start(final Stage testfxStage) {
        stage = testfxStage;

        final var injector = BeanScope.builder().build();

        manager = injector.get(RoofsManager.class);

        roofs = List.of(
                new Roof("code1", "address1"),
                new Roof("code2", "address2"),
                new Roof("code3", "address3"),
                new Roof("code4", "address4"),
                new Roof("code5", "address5")
        );
        roofs.forEach(manager::save);

        final var node = injector.get(RoofsRegistryNode.class);
        final var scene = new Scene(node);
        scene.getStylesheets().add(Stages.STYLESHEET_URL_STRING);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    protected Stage getStage() {
        return this.stage;
    }

    @Test
    void nodeInitialStatusTest(final FxRobot robot) {
        final var roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        assertIterableEquals(roofs, roofsTableView.getItems());

        assertFalse(robot.lookup(ADD_NEW_ROOF_BUTTON_NQ).queryButton().isDisabled());
        assertTrue(robot.lookup(REMOVE_ROOF_BUTTON_NQ).queryButton().isDisabled());
    }

    @Test
    void searchingRoofWithSearchTermMatchingOneRoofCodeTest(final FxRobot robot) {
        // given
        final var roof = roofs.getFirst();
        final var searchTerm = roof.getCode();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);
        waitForFxEvents();

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertEquals(1, roofsTableView.getItems().size());
        assertEquals(roof, roofsTableView.getItems().getFirst());
    }

    @Test
    void searchingRoofsWithSearchTermMatchingAllRoofsCodeTest(final FxRobot robot) {
        // given
        final var searchTerm = "code";
        final var matchingRoofs = roofs.stream().filter(r -> r.getCode().contains(searchTerm)).toList();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);
        waitForFxEvents();

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertIterableEquals(matchingRoofs, roofsTableView.getItems());
    }

    @Test
    void searchingRoofsWithSearchTermMatchingNoneTest(final FxRobot robot) {
        // given
        final var searchTerm = "a_random_string";

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertEquals(0, roofsTableView.getItems().size());
    }

    @Test
    void addRoofSuccessfulJourneyTest(final FxRobot robot) throws TimeoutException {
        // given
        final var searchTerm = "code1";
        final var newRoofCode = "code12";
        final var newRoofBuildingAddress = "address 12";
        final var newRoof = new Roof(newRoofCode, newRoofBuildingAddress);
        final var roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);
        robot.clickOn(ADD_NEW_ROOF_BUTTON_NQ);
        waitForFxEvents();
        robot.clickOn(ROOF_FORM_CODE_TEXT_FIELD_NQ).write(newRoofCode);
        robot.clickOn(ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ).write(newRoofBuildingAddress);
        robot.clickOn(ROOF_FORM_SAVE_BUTTON_NQ);
        waitForFxEvents();
        // wait for the roof form to be closed
        waitFor(
                5, TimeUnit.SECONDS, () -> {
                    try {
                        robot.lookup(ROOF_FORM_ROOT_NODE_NQ).queryParent();
                        return false;
                    } catch (final EmptyNodeQueryException e) {
                        return true;
                    }
                }
        );

        // then - the new roof should be visible in the table and persisted in the database
        assertTrue(roofsTableView.getItems().contains(newRoof));
        assertIterableEquals(manager.search(searchTerm), roofsTableView.getItems());
    }

    @Test
    void editRoofSuccessfulJourneyTest(final FxRobot robot) throws TimeoutException {
        // given
        final var searchTerm = "code1";
        final var textToAdd = " - edited";
        final var roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);
        final var roofToEdit = (Roof) roofsTableView.getItems().getFirst();
        roofsTableView.getSelectionModel().select(roofToEdit);
        robot.clickOn(EDIT_ROOF_BUTTON_NQ);
        waitForFxEvents();
        robot.clickOn(ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ).write(textToAdd);
        robot.clickOn(ROOF_FORM_SAVE_BUTTON_NQ);
        waitForFxEvents();
        // wait for the roof form to be closed
        waitFor(
                5, TimeUnit.SECONDS, () -> {
                    try {
                        robot.lookup(ROOF_FORM_ROOT_NODE_NQ).queryParent();
                        return false;
                    } catch (final EmptyNodeQueryException e) {
                        return true;
                    }
                }
        );

        // then - the roofs table should have been updated and the updated roof persisted in the database
        final var updatedRoof = robot
                .lookup(ROOFS_TABLE_NQ)
                .<Roof>queryTableView()
                .getItems()
                .stream()
                .filter(r -> r.getCode().equals(roofToEdit.getCode()))
                .findFirst()
                .orElse(null);
        assertNotNull(updatedRoof);
        assertEquals(roofToEdit.getBuildingAddress() + textToAdd, updatedRoof.getBuildingAddress());
        assertTrue(manager.exists(updatedRoof));

        assertTrue(robot.lookup(EDIT_ROOF_BUTTON_NQ).queryButton().isDisabled());
    }

    @Test
    void removeRoofSuccessfulJourneyTest(final FxRobot robot) {
        // given
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        // when
        roofsTableView.getSelectionModel().select(0);
        final var roofToRemove = roofsTableView.getSelectionModel().getSelectedItem();
        robot.clickOn(REMOVE_ROOF_BUTTON_NQ);
        waitForFxEvents();

        // then
        assertFalse(manager.exists(roofToRemove));
        assertFalse(roofsTableView.getItems().contains(roofToRemove));
        assertTrue(robot.lookup(REMOVE_ROOF_BUTTON_NQ).queryButton().isDisabled());
    }

}
