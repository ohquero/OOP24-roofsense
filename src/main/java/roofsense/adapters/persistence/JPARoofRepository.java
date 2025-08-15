package roofsense.adapters.persistence;

import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;

public class JPARoofRepository extends AbstractJPARepository<Roof> implements RoofRepository {

    public JPARoofRepository() {
        super(Roof.class);
    }

}
