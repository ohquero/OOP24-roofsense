package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import testutils.jpa.JPAExtension;
import testutils.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(JPAExtension.class)
class JPARoofRepositoryTest {

    @TestEntityManager
    private EntityManager em;
    private JPARoofRepositoryForTests repository;

    @BeforeEach
    void setUp() {
        repository = new JPARoofRepositoryForTests(em);
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    void placeholderTest() {
        assertNotNull(repository);
    }

    static class JPARoofRepositoryForTests extends JPARoofRepository {

        JPARoofRepositoryForTests(final EntityManager em) {
            super(() -> em);
        }

    }

}
