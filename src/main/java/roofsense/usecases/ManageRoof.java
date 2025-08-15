package roofsense.usecases;

import org.apache.commons.lang3.Validate;
import roofsense.entities.Roof;
import roofsense.usecases.ports.Repository;
import roofsense.usecases.ports.TransactionManager;

public class ManageRoof {

    private final TransactionManager transactionManager;
    private final Repository<Roof> repository;

    public ManageRoof(
            final TransactionManager transactionManager,
            final Repository<Roof> repository
    ) {
        this.transactionManager = Validate.notNull(transactionManager, "transactionManager must not be null.");
        this.repository = Validate.notNull(repository, "repository must not be null.");
    }

    public void add(final Roof roof) {
        transactionManager.executeInTransaction("ManageRoof.add", () -> repository.add(roof));
    }

}
