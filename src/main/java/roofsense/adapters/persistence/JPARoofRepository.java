package roofsense.adapters.persistence;

import com.google.inject.Inject;
import jakarta.persistence.EntityManager;
import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;

import java.util.Collection;
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
     * {@inheritDoc}
     */
    @Override
    protected Optional<Roof> findEqual(final Roof entity) {
        final var persistedRoof = getEntityManager()
                .createQuery("FROM Roof WHERE code = :code", Roof.class)
                .setParameter("code", entity.getCode())
                .getSingleResultOrNull();

        return Optional.ofNullable(persistedRoof);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Roof> search(final String searchTerm) {
        final var searchPattern = "%" + searchTerm + "%";
        return getEntityManager()
                .createQuery("FROM Roof WHERE code LIKE :searchTerm OR address LIKE :searchPattern", Roof.class)
                .setParameter("searchPattern", searchPattern)
                .getResultList();
    }

}
