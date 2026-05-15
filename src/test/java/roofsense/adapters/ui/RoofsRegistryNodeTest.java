package roofsense.adapters.ui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Roof;
import roofsense.usecases.RoofsManager;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

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

    private final Set<Roof> roofs = new LinkedHashSet<>(List.of(
            new Roof("code1", "address1"),
            new Roof("code2", "address2"),
            new Roof("code3", "address3"),
            new Roof("code4", "address4"),
            new Roof("code5", "address5")
    ));
    private RoofsManager manager;
    private Stage stage;

    @Start
    void start(final Stage testfxStage) {
        stage = testfxStage;

        // Creating a mock manager that uses the roofs collection as a backing store
        manager = mock(RoofsManager.class);
        when(manager.getAll()).thenReturn(roofs);
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            return roofs.contains(roof);
        }).when(manager).exists(any(Roof.class));
        doAnswer(invocation -> {
            final Roof roof = invocation.getArgument(0);
            roofs.stream().filter(r -> r.getCode().equals(roof.getCode())).findFirst().ifPresent(roofs::remove);
            roofs.add(roof);
            return new Roof(roof);
        }).when(manager).save(any(Roof.class));
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            roofs.remove(roof);
            return null;
        }).when(manager).remove(any(Roof.class));
        doAnswer(invocation -> {
            final String searchTerm = invocation.getArgument(0);
            return roofs
                    .stream()
                    .filter(
                            roof -> roof.getCode().contains(searchTerm)
                                    || roof.getBuildingAddress().contains(searchTerm)
                    )
                    .toList();
        }).when(manager).search(any(String.class));

        final var scene = new Scene(new RoofsRegistryNode(manager));
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
    void searchingRoofUsingFirstRoofCodeShouldDisplayOnlyOneRoofTest(final FxRobot robot) {
        // given
        final var firstRoof = roofs.iterator().next();
        final var searchTerm = firstRoof.getCode();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertEquals(1, roofsTableView.getItems().size());
        assertSame(firstRoof, roofsTableView.getItems().getFirst());
    }

    @Test
    void searchingRoofUsingFirstRoofAddressShouldDisplayOnlyOneRoofTest(final FxRobot robot) {
        // given
        final var firstRoof = roofs.iterator().next();
        final var searchTerm = firstRoof.getBuildingAddress();

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertEquals(1, roofsTableView.getItems().size());
        assertSame(firstRoof, roofsTableView.getItems().getFirst());
    }

    @Test
    void searchingRoofsUsingAllRoofsCodeEqualsPrefixShouldDisplayAllTheRoofsTest(final FxRobot robot) {
        // given
        final var searchTerm = "code";

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        final var expectedRoofsInTableViewCount = 5;
        assertEquals(expectedRoofsInTableViewCount, roofsTableView.getItems().size());
        assertIterableEquals(roofs, roofsTableView.getItems());
    }

    @Test
    void searchingRoofsUsingTotallyDifferentStringShouldNotDisplayAnythingTest(final FxRobot robot) {
        // given
        final var searchTerm = "a_random_string";

        // when
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // then
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertEquals(0, roofsTableView.getItems().size());
    }

    @Test
    void addRoofSuccessScenarioTest(final FxRobot robot) {
        // given
        final var searchTerm = "code1";
        robot.clickOn(SEARCH_ROOFS_TEXT_FIELD_NQ).write(searchTerm);

        // when
        robot.clickOn(ADD_NEW_ROOF_BUTTON_NQ);
        waitForFxEvents();

        // then
        final Node roofFormRootNode = robot.lookup(ROOF_FORM_ROOT_NODE_NQ).queryParent();
        assertNotNull(roofFormRootNode);

        // given
        final var roofCode = "code12";
        final var buildingAddress = "Main Street 12";

        // when - filling the roofForm correctly and clicking the save button
        robot.clickOn(ROOF_FORM_CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(ROOF_FORM_SAVE_BUTTON_NQ);

        // then - the roof should be added to the roofs table and the roofForm should be closed
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        final var roofsThatShouldBeDisplayed =
                roofs.stream().filter(r -> r.getCode().contains(searchTerm)).toList();
        assertIterableEquals(roofsThatShouldBeDisplayed, roofsTableView.getItems());
    }

    @Test
    void editRoofSuccessScenarioTest(final FxRobot robot) {
        // given
        final Roof roofToEdit = roofs.iterator().next();
        final var roofToEditCode = roofToEdit.getCode();
        final var roofToEditAddress = roofToEdit.getBuildingAddress();
        final var roofToEditNewAddress = roofToEditAddress + " - Edited";

        // when
        robot.clickOn(roofToEditCode);
        robot.clickOn(EDIT_ROOF_BUTTON_NQ);
        waitForFxEvents();
        robot.clickOn(ROOF_FORM_CODE_TEXT_FIELD_NQ).eraseText(roofToEditCode.length());
        robot.clickOn(ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ).write(roofToEditNewAddress);
        robot.clickOn(ROOF_FORM_SAVE_BUTTON_NQ);
        waitForFxEvents();

        // then
        final var updatedRoof = robot
                .lookup(ROOFS_TABLE_NQ)
                .<Roof>queryTableView()
                .getItems()
                .stream()
                .filter(r -> r.getCode().equals(roofToEditCode))
                .findFirst()
                .orElse(null);
        assertNotNull(updatedRoof);
        assertEquals(roofToEditNewAddress, updatedRoof.getBuildingAddress());
    }

    @Test
    void removeRoofSuccessScenarioTest(final FxRobot robot) {
        // given
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        final var secondRoof = roofsTableView.getItems().get(1);

        // when
        robot.clickOn(secondRoof.getCode());
        robot.clickOn(REMOVE_ROOF_BUTTON_NQ);
        waitForFxEvents();

        // then
        verify(manager).remove(secondRoof);
        assertTrue(robot.lookup(REMOVE_ROOF_BUTTON_NQ).queryButton().isDisabled());
    }

}
