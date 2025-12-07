package roofsense.adapters.ui;

import roofsense.entities.Roof;

class EditRoofFormTest extends AbstractNodeTest {

    @Override
    protected AbstractNode getNode() {
        return EditRoofForm.build(new Roof("code", "address"));
    }

}
