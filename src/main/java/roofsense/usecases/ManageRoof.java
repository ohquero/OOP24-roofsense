package roofsense.usecases;

import org.apache.commons.lang3.Validate;
import roofsense.entities.Roof;
import roofsense.usecases.ports.Repository;
import roofsense.usecases.ports.TransactionManager;

/**
 * Class providing access to {@link Roof} related use cases.
 */
public class ManageRoof {

    private final TransactionManager transactionManager;
    private final Repository<Roof> repository;

    /**
     * Constructor for the {@code ManageRoof} class.
     *
     * @param transactionManager the {@link TransactionManager} responsible for managing transactional operations. Must
     *                           not be {@code null}.
     * @param repository         the {@link Repository} instance for managing {@link Roof} entities. Must not be
     *                           {@code null}.
     */
    public ManageRoof(
            final TransactionManager transactionManager,
            final Repository<Roof> repository
    ) {
        this.transactionManager = Validate.notNull(transactionManager, "transactionManager must not be null.");
        this.repository = Validate.notNull(repository, "repository must not be null.");
    }

    /**
     * Adds a new {@link Roof} entity to the repository.
     *
     * @param roof the {@link Roof} entity to be added. Must not be {@code null}.
     */
    public void addNew(final Roof roof) {
        transactionManager.executeInTransaction("ManageRoof.addNew", () -> repository.add(roof));
    }

}
