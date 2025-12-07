package roofsense.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.persistence.EntityManagerFactory;
import roofsense.adapters.persistence.H2PersistenceUnit;
import roofsense.adapters.persistence.JPARoofRepository;
import roofsense.adapters.persistence.JPATransactionManager;
import roofsense.adapters.ui.CreateRoofForm;
import roofsense.usecases.ManageRoof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.TransactionManager;

/**
 * Google Guice module defining how to create and inject all the dependencies of the application.
 */
public final class RoofSenseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransactionManager.class).to(JPATransactionManager.class);
        bind(RoofRepository.class).to(JPARoofRepository.class);
    }

    /**
     * Creates a new {@link CreateRoofForm} instance.
     *
     * @param manageRoof the use case for managing roofs
     *
     * @return a new CreateRoofNode instance
     */
    @Provides
    public CreateRoofForm provideCreateRoofNode(final ManageRoof manageRoof) {
        return CreateRoofForm.build(manageRoof);
    }

    /**
     * Creates a new {@link EntityManagerFactory} instance.
     *
     * @return the EntityManagerFactory instance
     */
    @Provides
    public EntityManagerFactory provideEntityManagerFactory() {
        return H2PersistenceUnit.getEntityManagerFactory();
    }

}
