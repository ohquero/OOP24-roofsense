package roofsense.adapters.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Configuration class for H2 database setup.
 */
public class H2PersistenceUnit {

    private static final String NAME = "H2";

    private H2PersistenceUnit() {
    }

    /**
     * Returns an {@link EntityManagerFactory} connected to the persistence unit.
     *
     * @return an {@link EntityManagerFactory}.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory(NAME);
    }

}
