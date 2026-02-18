package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;

/**
 * Defines a contract for providing {@link EntityManager} instances.
 *
 * <p>
 * Implementations are responsible for ensuring that the {@link EntityManager} is
 * retrieved within an active transaction context.
 */
@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface EntityManagerProvider {

    /**
     * Retrieves the {@link EntityManager}.
     *
     * @return the current {@link EntityManager} for the active transaction.
     */
    EntityManager getEntityManager();

}
