package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import roofsense.usecases.ports.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Abstract class for JPA {@link Repository} implementations.
 *
 * <p>
 * All the operations, including retrieval ones, must be executed inside
 * {@link JPATransactionManager#executeInTransaction}, which handles the transaction.
 *
 * @param <E> type of the entity managed by this repository.
 */
public abstract class AbstractJPARepository<E> implements Repository<E> {

    private final Class<E> entityClass;

    /**
     * Constructs an instance of {@code AbstractJPARepository}.
     *
     * @param entityClass the {@code Class} object representing the type of the entity
     *                    managed by this repository. Must not be {@code null}.
     */
    public AbstractJPARepository(final Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return JPATransactionManager.getEntityManager();
    }

    @Override
    public void add(final E entity) {
        getEntityManager().persist(entity);
    }

    @Override
    public E update(final E entity) {
        return getEntityManager().merge(entity);
    }

    @Override
    public Optional<E> get(final String repositoryId) {
        return Optional.ofNullable(getEntityManager().find(entityClass, repositoryId));
    }

    @Override
    public List<E> getAll() {
        return getEntityManager().createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
    }

    @Override
    public void remove(final E entity) {
        getEntityManager().remove(getEntityManager().contains(entity) ? entity : getEntityManager().merge(entity));
    }

}

