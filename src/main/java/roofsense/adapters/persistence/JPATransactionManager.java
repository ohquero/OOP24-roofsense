package roofsense.adapters.persistence;

import com.google.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.Validate;
import roofsense.usecases.ports.TransactionManager;

import java.util.function.Supplier;

/**
 * Implementation of {@link TransactionManager} providing transaction management capabilities using the JPA API. It
 * manages the lifecycle of {@link EntityManager} objects and ensures that all transactional operations are executed
 * within a single transaction.
 */
public class JPATransactionManager implements TransactionManager {

    private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructs an instance of {@code JPATransactionManager}.
     *
     * @param entityManagerFactory the {@link EntityManagerFactory} used to create {@link EntityManager} instances.
     *                             Must not be {@code null}.
     */
    @Inject
    public JPATransactionManager(final EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Validate.notNull(entityManagerFactory, "entityManagerFactory must not be null.");
    }

    /**
     * Retrieves the {@link EntityManager} associated with the executing thread.
     *
     * <p>
     * This method ensures that the {@link EntityManager} is retrieved only within the context of a transaction.
     * If called outside a transaction, it throws an {@link IllegalStateException}.
     *
     * @return the current {@link EntityManager} for the active transaction.
     *
     * @throws IllegalStateException if no transaction is active when this method is called.
     */
    public static EntityManager getEntityManager() {
        final EntityManager em = CURRENT_ENTITY_MANAGER.get();
        if (em == null) {
            throw new IllegalStateException(
                    "Statement called outside of a transaction. Use inTransaction() method to wrap it in a "
                            + "transaction.");
        }
        return em;
    }

    private static void wrapAndThrowPersistenceException(
            final PersistenceException exception,
            final String unitOfWorkName
    ) {
        final var exceptionMessagePrefix = unitOfWorkName + " unit of work execution failed: ";
        final var cause = exception.getCause();
        if (cause instanceof ConstraintViolationException
                || cause instanceof org.hibernate.exception.ConstraintViolationException) {
            throw new IllegalArgumentException(exceptionMessagePrefix + "invalid entity provided.", exception);
        }
        throw new IllegalStateException(exceptionMessagePrefix + "unexpected exception occurred.", exception);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public void executeInTransaction(final String operationId, final Runnable operation) {
        if (CURRENT_ENTITY_MANAGER.get() != null) {
            throw new IllegalStateException("Nested transactions are not supported.");
        }

        final EntityManager em = entityManagerFactory.createEntityManager();
        CURRENT_ENTITY_MANAGER.set(em);
        try {
            em.getTransaction().begin();
            operation.run();
            em.getTransaction().commit();
        } catch (final PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            wrapAndThrowPersistenceException(e, operationId);
            throw new AssertionError("This line should not be reachable.", e);
        } finally {
            CURRENT_ENTITY_MANAGER.remove();
            em.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.UseTryWithResources")
    public <T> T executeInTransaction(final String operationId, final Supplier<T> operation) {
        if (CURRENT_ENTITY_MANAGER.get() != null) {
            throw new IllegalStateException("Nested transactions are not supported.");
        }

        final EntityManager em = entityManagerFactory.createEntityManager();
        CURRENT_ENTITY_MANAGER.set(em);
        try {
            em.getTransaction().begin();
            final T result = operation.get();
            em.getTransaction().commit();
            return result;
        } catch (final PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            wrapAndThrowPersistenceException(e, operationId);
            throw new AssertionError("This line should not be reachable.", e);
        } finally {
            CURRENT_ENTITY_MANAGER.remove();
            em.close();
        }
    }

}
