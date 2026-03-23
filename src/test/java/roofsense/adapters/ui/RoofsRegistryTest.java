package roofsense.adapters.ui;

import javafx.scene.Node;
import javafx.scene.Parent;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

/**
 * Test class for {@link RoofsRegistry}.
 */
class RoofsRegistryTest extends AbstractNodeTest {

    private static final String ROOFS_TABLE_NQ = "#roofsTableView";
    private static final String ADD_NEW_ROOF_BUTTON_NQ = "#addNewRoofButton";
    private static final String EDIT_ROOF_BUTTON_NQ = "#editRoofButton";
    private static final String REMOVE_ROOF_BUTTON_NQ = "#removeRoofButton";

    private static final String ROOF_FORM_ROOT_NODE_NQ = "#roofFormRootNode";
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
        // Creating a mock manager that uses the roofs collection as a backing store
        manager = mock(RoofsManager.class);
        when(manager.getAll()).thenReturn(roofs);
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            return roofs.contains(roof);
        }).when(manager).exists(any(Roof.class));
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            roofs.add(roof);
            return null;
        }).when(manager).add(any(Roof.class));
        doAnswer(invocation -> {
            final Roof roof = invocation.getArgument(0);
            roofs.stream().filter(r -> r.getCode().equals(roof.getCode())).findFirst().ifPresent(roofs::remove);
            roofs.add(roof);
            return new Roof(roof);
        }).when(manager).update(any(Roof.class));
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            roofs.remove(roof);
            return null;
        }).when(manager).remove(any(Roof.class));

        this.stage = testfxStage;
        this.stage.setScene(new Scene((Parent) RoofsRegistry.create(manager).getRootNode()));
        this.stage.show();
    }

    @Override
    protected Stage getStage() {
        return this.stage;
    }

    @Test
    void nodeInitialStatusTest(final FxRobot robot) {
        final var roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        verify(manager).getAll();
        assertIterableEquals(roofs, roofsTableView.getItems());

        assertFalse(robot.lookup(ADD_NEW_ROOF_BUTTON_NQ).queryButton().isDisabled());
        assertTrue(robot.lookup(REMOVE_ROOF_BUTTON_NQ).queryButton().isDisabled());
    }

    @Test
    void addNewRoofSuccessScenarioTest(final FxRobot robot) {
        // when
        robot.clickOn(ADD_NEW_ROOF_BUTTON_NQ);
        waitForFxEvents();

        // then
        final Node roofFormRootNode = robot.lookup(ROOF_FORM_ROOT_NODE_NQ).queryParent();
        assertNotNull(roofFormRootNode);

        // given
        final var roofCode = "R-01";
        final var buildingAddress = "Main Street 1";

        // when - filling the roofForm correctly and clicking the save button
        robot.clickOn(ROOF_FORM_CODE_TEXT_FIELD_NQ).write(roofCode);
        robot.clickOn(ROOF_FORM_BUILDING_ADDRESS_TEXT_FIELD_NQ).write(buildingAddress);
        robot.clickOn(ROOF_FORM_SAVE_BUTTON_NQ);

        // then - the roof should be added to the roofs table and the roofForm should be closed
        final TableView<Roof> roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        final var addedRoof =
                roofsTableView.getItems().stream().filter(r -> r.getCode().equals(roofCode)).findFirst().orElse(null);
        assertNotNull(addedRoof);
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
