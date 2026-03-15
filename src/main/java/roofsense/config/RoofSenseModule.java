package roofsense.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import roofsense.adapters.persistence.EntityManagerProvider;
import roofsense.adapters.persistence.JPARoofRepository;
import roofsense.adapters.persistence.JPAUnitOfWork;
import roofsense.adapters.ui.RoofForm;
import roofsense.usecases.ManageRoof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

/**
 * Google Guice module defining how to create and inject all the dependencies of the application.
 */
public final class RoofSenseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UnitOfWork.class).to(JPAUnitOfWork.class);
        bind(EntityManagerProvider.class).to(JPAUnitOfWork.class);
        bind(RoofRepository.class).to(JPARoofRepository.class);
    }

    /**
     * Creates a new {@link RoofForm} instance.
     *
     * @param manageRoof the use case for managing roofs
     *
     * @return a new CreateRoofNode instance
     */
    @Provides
    public RoofForm provideRoofForm(final ManageRoof manageRoof) {
        return RoofForm.build(manageRoof);
    }

    /**
     * Creates a new {@link EntityManagerFactory} instance.
     *
     * @return the EntityManagerFactory instance
     */
    @Provides
    @Singleton
    public EntityManagerFactory provideEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("H2");
    }

}
