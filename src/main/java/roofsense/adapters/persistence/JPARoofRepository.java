package roofsense.adapters.persistence;

import com.google.inject.Inject;
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
    @Inject
    public JPARoofRepository() {
        super(Roof.class);
    }

}
