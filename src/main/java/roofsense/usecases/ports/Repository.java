package roofsense.usecases.ports;

import java.util.Collection;

/**
 * Represents a generic repository interface for performing CRUD (Create, Read, Update, Delete) operations
 * on entities. The repository acts as a bridge between the domain model and the data store, providing
 * a clear abstraction for data access logic.
 *
 * @param <E> the type of the entity managed by the repository.
 */
public interface Repository<E> {

    /**
     * Retrieves all entities managed by the repository.
     *
     * @return a list containing all entities of type {@code E}; the list may be empty if no entities are present.
     */
    Collection<E> getAll();

    /**
     * Checks if the given entity exists in the repository.
     *
     * @param entity the entity to check; must not be {@code null}.
     *
     * @return {@code true} if the entity exists, {@code false} otherwise.
     */
    boolean exists(E entity);

    /**
     * Persists or updates the given entity in the repository. If the entity does not exist, it will be created;
     * if it already exists, it will be updated with the current state.
     *
     * @param entity the entity to persist or update; must not be {@code null}.
     *
     * @return the persisted entity, reflecting the current state in the repository.
     *
     * @throws IllegalArgumentException if the entity does not exist in the repository.
     */
    E save(E entity);

    /**
     * Removes the given entity from the repository.
     *
     * @param entity the entity to be removed; must not be {@code null}.
     *
     * @throws IllegalArgumentException if the entity does not exist in the repository.
     */
    void remove(E entity);

}
