package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.Validate;
import roofsense.usecases.ports.TransactionManager;

/**
 * Implementation of {@link TransactionManager} providing transaction management capabilities using the JPA API. It
 * manages the lifecycle of {@link EntityManager} objects and ensures that all transactional operations are executed
 * within a single transaction.
 */
public class JPATransactionManager implements TransactionManager {

    private static final ThreadLocal<EntityManager> currentEntityManager = new ThreadLocal<>();
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructs an instance of {@code JPATransactionManager}.
     *
     * @param entityManagerFactory the {@link EntityManagerFactory} used to create {@link EntityManager} instances.
     *                             Must not be {@code null}.
     */
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
        final EntityManager em = currentEntityManager.get();
        if (em == null) {
            throw new IllegalStateException(
                    "Statement called outside of a transaction. Use inTransaction() method to wrap it in a " +
                            "transaction.");
        }
        return em;
    }

    private static RuntimeException wrapPersistenceException(
            final PersistenceException exception,
            final String unitOfWorkName
    ) {
        final var exceptionMessagePrefix = unitOfWorkName + " unit of work execution failed : ";
        final var cause = exception.getCause();
        if (cause != null) {
            // Code for handling specific exception scenarios
            if (cause instanceof ConstraintViolationException ||
                    cause instanceof org.hibernate.exception.ConstraintViolationException) {
                return new IllegalArgumentException(exceptionMessagePrefix + "invalid entity provided.", exception);
            }
        }
        return new IllegalStateException(exceptionMessagePrefix + "unexpected exception occurred.", exception);
    }

    @Override
    public void executeInTransaction(final String operationId, final Runnable operation) {
        if (currentEntityManager.get() != null) {
            throw new IllegalStateException("Nested transactions are not supported.");
        }

        final EntityManager em = entityManagerFactory.createEntityManager();
        currentEntityManager.set(em);
        try {
            em.getTransaction().begin();
            operation.run();
            em.getTransaction().commit();
        } catch (final PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw wrapPersistenceException(e, operationId);
        } finally {
            currentEntityManager.remove();
            em.close();
        }
    }

}
