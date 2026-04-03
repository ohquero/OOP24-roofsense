package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.Validate;
import roofsense.usecases.ports.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract class for JPA {@link Repository} implementations.
 *
 * @param <E> type of the entity managed by this repository.
 */
public abstract class AbstractJPARepository<E> implements Repository<E> {

    private final Class<E> entityClass;
    private final EntityManagerProvider entityManagerProvider;

    /**
     * Constructs an instance of {@link AbstractJPARepository}.
     *
     * @param entityClass           the {@link Class} object representing the type of the entity managed by this
     *                              repository. Must not be {@code null}.
     * @param entityManagerProvider the {@link EntityManagerProvider} used to obtain the {@link EntityManager} instance
     *                              for persistence operations. Must not be {@code null}.
     */
    public AbstractJPARepository(final Class<E> entityClass, final EntityManagerProvider entityManagerProvider) {
        this.entityClass = Validate.notNull(entityClass, "entityClass must not be null.");
        this.entityManagerProvider = Validate.notNull(entityManagerProvider, "entityManagerProvider must not be null.");
    }

    /**
     * Finds an entity in the database that is equal to the given entity using only business keys.
     *
     * @param entity the entity to search for in the database. Must not be {@code null}.
     *
     * @return an {@link Optional} that may contain the entity found in the database.
     */
    protected abstract Optional<E> findEqual(E entity);

    /**
     * Returns the {@link EntityManager} instance that must be used to perform operations against the persistence unit.
     *
     * @return the {@link EntityManager} instance that must be used to perform operations against the persistence unit.
     */
    protected EntityManager getEntityManager() {
        return entityManagerProvider.getEntityManager();
    }

    /**
     * Returns the repository's identifier of the given entity.
     *
     * @param entity the entity for which to retrieve the identifier. Must not be {@code null}.
     *
     * @return an {@link Optional} containing the identifier of the entity, or {@link Optional#empty()} if the entity is
     *         not yet persisted.
     */
    protected Optional<Object> getEntityId(final E entity) {
        return Optional.ofNullable(
                getEntityManager()
                        .getEntityManagerFactory()
                        .getPersistenceUnitUtil()
                        .getIdentifier(entity)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<E> getAll() {
        return getEntityManager().createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(final E entity) {
        Objects.requireNonNull(entity);
        return getEntityManager().contains(entity) || findEqual(entity).isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E save(final E entity) {
        Objects.requireNonNull(entity);

        final var entityId = getEntityId(entity).orElse(null);
        if (entityId == null) {
            getEntityManager().persist(entity);
            return entity;
        }
        if (getEntityManager().find(entity.getClass(), entityId) == null) {
            throw new IllegalArgumentException("Entity does not exist in the database (possibly removed).");
        }
        return getEntityManager().merge(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final E entity) {
        final var entityId = getEntityId(entity).orElse(null);
        if (entityId == null) {
            throw new IllegalArgumentException("Entity does hot exists in the database (never persisted).");
        }
        final E entityToRemove;
        if (!getEntityManager().contains(entity)) {
            if (getEntityManager().find(entity.getClass(), entityId) == null) {
                throw new IllegalArgumentException("Entity does not exist in the database (possibly removed).");
            }
            entityToRemove = getEntityManager().merge(entity);
        } else {
            entityToRemove = entity;
        }
        getEntityManager().remove(entityToRemove);
    }

}

