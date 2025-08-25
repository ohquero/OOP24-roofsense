package roofsense.adapters.persistence;

import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;

/**
 * JPA-based implementation of the {@link RoofRepository} interface.See {@link AbstractJPARepository} for more technical
 * details.
 */
public class JPARoofRepository extends AbstractJPARepository<Roof> implements RoofRepository {

    /**
     * Constructs a new instance of {@code JPARoofRepository}.
     */
    public JPARoofRepository() {
        super(Roof.class);
    }

}
