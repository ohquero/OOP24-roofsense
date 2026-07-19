package roofsense.adapters.persistence;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Factory that creates JPA-related beans managed by the DI container.
 */
@Factory
public final class JPAFactory {

    private static final String PERSISTENCE_UNIT = "RoofSense";

    /**
     * Creates the {@link EntityManagerFactory} used to create {@link jakarta.persistence.EntityManager} instances.
     *
     * @return a fully configured {@link EntityManagerFactory}.
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

}
