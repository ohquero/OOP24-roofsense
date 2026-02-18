package roofsense.adapters.persistence;

import com.google.inject.Inject;
import jakarta.persistence.EntityManager;
import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;

import java.util.Optional;

/**
 * {@link AbstractJPARepository} implementation of the {@link RoofRepository} interface.
 */
public class JPARoofRepository extends AbstractJPARepository<Roof> implements RoofRepository {

    /**
     * Constructs a new instance of {@link JPARoofRepository}.
     *
     * @param entityManagerProvider the {@link EntityManagerProvider} used to obtain the {@link EntityManager} instance
     *                              for persistence operations. Must not be {@code null}.
     */
    @Inject
    public JPARoofRepository(final EntityManagerProvider entityManagerProvider) {
        super(Roof.class, entityManagerProvider);
    }

    /**
     * Finds a persisted {@link Roof} entity that is equal to the provided one.
     *
     * <p>
     * This method is not intended to be overridden. It implements the equality check
     * based on the roof's code attribute.
     *
     * @param entity the {@link Roof} entity to find an equal match for. Must not be {@code null}.
     *
     * @return an {@link Optional} containing the equal persisted entity, or empty if none found.
     */
    @Override
    protected Optional<Roof> findEqual(final Roof entity) {
        final var persistedRoof = getEntityManager()
                .createQuery("FROM Roof WHERE code = :code", Roof.class)
                .setParameter("code", entity.getCode())
                .getSingleResultOrNull();

        return Optional.ofNullable(persistedRoof);
    }

}
