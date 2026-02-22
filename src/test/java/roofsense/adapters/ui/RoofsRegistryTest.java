package roofsense.adapters.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.Start;
import roofsense.entities.Roof;
import roofsense.usecases.ManageRoof;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link RoofsRegistry}.
 */
class RoofsRegistryTest extends AbstractNodeTest {

    private static final String ROOFS_TABLE_NQ = "#roofsTableView";

    private final List<Roof> roofs = List.of(
            new Roof("code1", "address1"),
            new Roof("code2", "address2"),
            new Roof("code3", "address3"),
            new Roof("code4", "address4"),
            new Roof("code5", "address5")
    );
    private ManageRoof manager;

    @Override
    protected AbstractNode getNode() {
        return RoofsRegistry.create(manager);
    }

    @Start
    void start(final Stage stage) {
        manager = mock(ManageRoof.class);
        when(manager.getAll()).thenReturn(roofs);

        stage.setScene(new Scene((Parent) getNode().getRootNode()));
        stage.show();
    }

    @Test
    void roofsTableShouldContainRoofs(final FxRobot robot) {
        final var table = robot.lookup(ROOFS_TABLE_NQ).queryTableView();
        assertIterableEquals(roofs, table.getItems());
    }

}
