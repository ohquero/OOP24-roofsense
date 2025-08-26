package roofsense.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import roofsense.adapters.persistence.JPARoofRepository;
import roofsense.adapters.persistence.JPATransactionManager;
import roofsense.usecases.ManageRoof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.TransactionManager;

/**
 * Application composition root. This class is responsible for creating and injecting all the dependencies of the
 * application.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public class DependencyManager implements AutoCloseable {

    private final EntityManagerFactory entityManagerFactory;
    private final TransactionManager transactionManager;
    private final RoofRepository roofRepository;
    private final ManageRoof manageRoof;

    /**
     * Default constructor.
     */
    public DependencyManager() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("roofsense-pu");

        this.transactionManager = new JPATransactionManager(this.entityManagerFactory);

        this.roofRepository = new JPARoofRepository();

        this.manageRoof = new ManageRoof(this.transactionManager, this.roofRepository);
    }

    /**
     * Provides access to the {@link ManageRoof} instance used for managing roof-related operations.
     *
     * @return the instance of {@link ManageRoof} responsible for handling roof management operations.
     */
    public ManageRoof getManageRoof() {
        return manageRoof;
    }

    /**
     * Retrieves the {@link TransactionManager} instance associated with the repository layer.
     *
     * @return the {@link TransactionManager} used for handling transactional operations.
     */
    public TransactionManager getRepositoryTransactionManager() {
        return transactionManager;
    }

    /**
     * Retrieves the {@link RoofRepository} instance used for accessing and managing roof entities.
     *
     * @return the instance of {@link RoofRepository} responsible for handling roof-related data operations.
     */
    public RoofRepository getRoofRepository() {
        return roofRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

}
