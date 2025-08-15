package roofsense.adapters.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class H2PersistenceUnitTest {

    @Test
    void testEntityManagerFactory() {
        try (final EntityManagerFactory entityManagerFactory = H2PersistenceUnit.getEntityManagerFactory()) {
            assertNotNull(entityManagerFactory);
            assertTrue(entityManagerFactory.isOpen());

            // Re-calling the method should create a new factory instance
            assertNotSame(entityManagerFactory, H2PersistenceUnit.getEntityManagerFactory());

            return;
        } catch (final PersistenceException e) {
            fail(e.getMessage(), e.getCause());
        }
        fail("Should not reach here");
    }

}
