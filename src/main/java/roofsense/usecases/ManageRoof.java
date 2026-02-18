package roofsense.usecases;

import com.google.inject.Inject;
import org.apache.commons.lang3.Validate;
import roofsense.entities.Roof;
import roofsense.usecases.ports.Repository;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

/**
 * Class providing access to {@link Roof} related use cases.
 */
public class ManageRoof {

    private final UnitOfWork unitOfWork;
    private final Repository<Roof> repository;

    /**
     * Constructor for the {@code ManageRoof} class.
     *
     * @param unitOfWork the {@link UnitOfWork}. Must not be {@code null}.
     * @param repository  the {@link Repository} instance for managing {@link Roof} entities. Must not be
     *                    {@code null}.
     */
    @Inject
    public ManageRoof(final UnitOfWork unitOfWork, final RoofRepository repository) {
        this.unitOfWork = Validate.notNull(unitOfWork, "unit of work must not be null");
        this.repository = Validate.notNull(repository, "repository must not be null.");
    }

    /**
     * Adds a new {@link Roof} entity to the repository.
     *
     * @param roof the {@link Roof} entity to be added. Must not be {@code null}.
     *
     * @throws IllegalArgumentException if the roof is invalid.
     * @throws IllegalStateException    if an unexpected error occurs during the operation.
     */
    public void addNew(final Roof roof) {
        unitOfWork.execute(() -> repository.add(roof));
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

}
