package roofsense.adapters.ui;

/**
 * Test class for {@link RoofsRegistry}.
 */
class RoofsRegistryTest extends AbstractNodeTest {

    @Override
    protected AbstractNode getNode() {
        return RoofsRegistry.create();
    }

}
