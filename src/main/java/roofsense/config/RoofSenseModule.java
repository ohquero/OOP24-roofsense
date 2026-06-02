package roofsense.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import roofsense.adapters.persistence.EntityManagerProvider;
import roofsense.adapters.persistence.JPARoofRepository;
import roofsense.adapters.persistence.JPAUnitOfWork;
import roofsense.adapters.ui.RoofFormNode;
import roofsense.adapters.ui.RoofsRegistryNode;
import roofsense.usecases.RoofsManager;
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
     * Creates a new {@link RoofFormNode}.
     *
     * @param roofsManager the use case for managing roofs
     *
     * @return a {@link RoofFormNode}.
     */
    @Provides
    public RoofFormNode provideRoofFormNode(final RoofsManager roofsManager) {
        return new RoofFormNode(roofsManager);
    }

    /**
     * Creates a new {@link RoofsRegistryNode}.
     *
     * @param roofsManager the use case for managing roofs
     *
     * @return a {@link RoofsRegistryNode}.
     */
    @Provides
    public RoofsRegistryNode provideRoofsRegistryNode(final RoofsManager roofsManager) {
        return new RoofsRegistryNode(roofsManager);
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
