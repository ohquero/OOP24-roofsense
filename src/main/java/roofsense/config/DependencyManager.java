package roofsense.config;

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
public class DependencyManager implements AutoCloseable {

    private final EntityManagerFactory entityManagerFactory;
    private final TransactionManager transactionManager;
    private final RoofRepository roofRepository;
    private final ManageRoof manageRoof;

    public DependencyManager() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("roofsense-pu");

        this.transactionManager = new JPATransactionManager(this.entityManagerFactory);

        this.roofRepository = new JPARoofRepository();

        this.manageRoof = new ManageRoof(this.transactionManager, this.roofRepository);
    }

    public ManageRoof getManageRoof() {
        return manageRoof;
    }

    public TransactionManager getRepositoryTransactionManager() {
        return transactionManager;
    }

    public RoofRepository getRoofRepository() {
        return roofRepository;
    }

    @Override
    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

}
