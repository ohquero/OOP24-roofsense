package roofsense.adapters.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
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

    private final Collection<Roof> roofs = new LinkedHashSet<>(List.of(
            new Roof("code1", "address1"),
            new Roof("code2", "address2"),
            new Roof("code3", "address3"),
            new Roof("code4", "address4"),
            new Roof("code5", "address5")
    ));
    private ManageRoof manager;
    private Stage stage;

    @Start
    void start(final Stage testfxStage) {
        // Creating a mock manager that uses the roofs collection as a backing store
        manager = mock(ManageRoof.class);
        when(manager.getAll()).thenReturn(roofs);
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            return roofs.contains(roof);
        }).when(manager).exists(any(Roof.class));
        doAnswer(invocationOnMock -> {
            final Roof roof = invocationOnMock.getArgument(0);
            roofs.add(roof);
            return null;
        }).when(manager).addNew(any(Roof.class));

        this.stage = testfxStage;
        this.stage.setScene(new Scene((Parent) RoofsRegistry.create(manager).getRootNode()));
        this.stage.show();
    }

    @Override
    protected Stage getStage() {
        return this.stage;
    }

    @Test
    void roofsRegistryInitialStatusTest(final FxRobot robot) {
        final var roofsTableView = robot.lookup(ROOFS_TABLE_NQ).queryTableView();

        verify(manager).getAll();
        assertIterableEquals(roofs, roofsTableView.getItems());
    }

    @Test
    void clickingAddNewRoofButtonShowsRoofForm(final FxRobot robot) {
        // when
        robot.clickOn("#addNewRoofButton");
        waitForFxEvents();

        // then
        assertEquals(2, robot.listWindows().size());
        final var roofFormWindowsCount = robot
                .listWindows()
                .stream()
                .map(window -> window.getScene().getRoot().getId())
                .filter("roofFormRootNode"::equals)
                .count();
        assertEquals(1, roofFormWindowsCount);
    }

}
