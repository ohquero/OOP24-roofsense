package roofsense.adapters.persistence;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.Validate;
import roofsense.usecases.ports.UnitOfWork;

import java.util.function.Supplier;

/**
 * JPA-backed implementation of {@link UnitOfWork} that runs operations inside a single JPA transaction. Acts also as
 * {@link EntityManagerProvider}, allowing repositories to access the current {@link EntityManager} only while inside a
 * transaction.
 *
 * <p>
 * Nested transactions are not supported: attempting to call {@link #execute(Supplier)} while another transaction is
 * active results in {@link IllegalStateException}.
 */
@Singleton
public final class JPAUnitOfWork implements UnitOfWork, EntityManagerProvider {

    private static final ThreadLocal<EntityManager> CURRENT_ENTITY_MANAGER = new ThreadLocal<>();
    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructs an instance of {@code JPAUnitOfWork}.
     *
     * @param entityManagerFactory the {@link EntityManagerFactory} used to create {@link EntityManager} instances.
     *                             Must not be {@code null}.
     */
    @Inject
    public JPAUnitOfWork(final EntityManagerFactory entityManagerFactory) {
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
    @Override
    public EntityManager getEntityManager() {
        final EntityManager em = CURRENT_ENTITY_MANAGER.get();
        if (em == null) {
            throw new IllegalStateException(
                    "Statement called outside of a transaction. Use execute() method to wrap it in a "
                            + "transaction.");
        }
        return em;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(final Supplier<T> supplier) {
        if (CURRENT_ENTITY_MANAGER.get() != null) {
            throw new IllegalStateException("Nested transactions are not supported.");
        }

        final var em = entityManagerFactory.createEntityManager();
        final var tx = em.getTransaction();
        try (em) {
            CURRENT_ENTITY_MANAGER.set(em);
            tx.begin();
            final T result = supplier.get();
            tx.commit();
            return result;
        } catch (final PersistenceException e) {
            final var exceptionMessagePrefix = "UnitOfWork execution failed: ";
            final var cause = e.getCause();

            if (cause instanceof ConstraintViolationException
                    || cause instanceof org.hibernate.exception.ConstraintViolationException) {
                throw new IllegalArgumentException(exceptionMessagePrefix + "invalid entity provided.", e);
            }

            throw new IllegalStateException(exceptionMessagePrefix + "unexpected exception occurred.", e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            CURRENT_ENTITY_MANAGER.remove();
        }
    }

}
