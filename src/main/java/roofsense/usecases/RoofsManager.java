package roofsense.usecases;

import com.google.inject.Inject;
import org.apache.commons.lang3.Validate;
import roofsense.entities.Roof;
import roofsense.usecases.ports.Repository;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

import java.util.Collection;

/**
 * Class providing access to {@link Roof} related use cases.
 */
public final class RoofsManager {

    private final UnitOfWork unitOfWork;
    private final RoofRepository repository;

    /**
     * Constructor for the {@code RoofsManager} class.
     *
     * @param unitOfWork the {@link UnitOfWork}. Must not be {@code null}.
     * @param repository the {@link Repository} instance for managing {@link Roof} entities. Must not be
     *                   {@code null}.
     */
    @Inject
    public RoofsManager(final UnitOfWork unitOfWork, final RoofRepository repository) {
        this.unitOfWork = Validate.notNull(unitOfWork, "unit of work must not be null");
        this.repository = Validate.notNull(repository, "repository must not be null.");
    }

    /**
     * Retrieves all {@link Roof} entities from the repository.
     *
     * @return a list of {@link Roof} entities.
     */
    public Collection<Roof> getAll() {
        return unitOfWork.execute(repository::getAll);
    }

    /**
     * Checks if the given {@link Roof} entity exists in the repository.
     *
     * @param roof the {@link Roof} entity to be checked. Must not be {@code null}.
     *
     * @return {@code true} if the roof exists, {@code false} otherwise.
     */
    public boolean exists(final Roof roof) {
        return unitOfWork.execute(() -> repository.exists(roof));
    }

    /**
     * Adds or updates the given {@link Roof} in the repository. Creates a new entry if it does not exist,
     * otherwise updates the existing one.
     *
     * @param roof the {@link Roof} entity to be added or updated. Must not be {@code null}.
     *
     * @return the saved {@link Roof} entity.
     *
     * @throws IllegalArgumentException if the roof is invalid.
     * @throws IllegalStateException    if an unexpected error occurs during the operation.
     */
    public Roof save(final Roof roof) {
        return unitOfWork.execute(() -> repository.save(roof));
    }

    /**
     * Removes the given {@link Roof} entity from the repository.
     *
     * @param selectedRoof the {@link Roof} entity to be removed.
     */
    public void remove(final Roof selectedRoof) {
        unitOfWork.execute(() -> repository.remove(selectedRoof));
    }

    /**
     * Searches for all {@link Roof} entities containing the specified search term in any field.
     *
     * @param searchTerm the {@link String} to search for.
     *
     * @return a {@link Collection} of {@link Roof} objects.
     */
    public Collection<Roof> search(final String searchTerm) {
        return unitOfWork.execute(() -> repository.search(searchTerm));
    }

}
