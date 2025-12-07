package roofsense.usecases.ports;

import java.util.List;

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
    List<E> getAll();

    /**
     * Checks if the given entity exists in the repository.
     *
     * @param entity the entity to check; must not be {@code null}.
     *
     * @return {@code true} if the entity exists, {@code false} otherwise.
     */
    boolean exists(E entity);

    /**
     * Adds a new entity to the repository.
     *
     * @param entity the entity to be added; must not be {@code null}.
     */
    void add(E entity);

    /**
     * Updates the given entity in the repository. The entity must already exist,
     * and any modifications to its state will be persisted.
     *
     * @param entity the entity to be updated; must not be {@code null} and must exist in the repository.
     *
     * @return the updated entity, which reflects the saved state in the repository.
     */
    E update(E entity);

    /**
     * Removes the specified entity from the repository. If the entity does not exist,
     * no action will be performed. This method ensures the entity is deleted from the underlying data store.
     *
     * @param entity the entity to be removed; must not be {@code null}.
     */
    void remove(E entity);

}
