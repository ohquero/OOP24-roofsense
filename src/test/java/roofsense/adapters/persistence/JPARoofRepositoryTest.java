package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import roofsense.entities.Roof;
import testutils.jpa.JPAExtension;
import testutils.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void searchByCodeTest() {
        final var roof = new Roof("R-01", "address 01");
        repository.add(roof);
        em.flush();

        final var results = repository.search("roof");

        assertEquals(1, results.size());
        assertEquals(roof, results.iterator().next());
    }

    @Test
    void searchByAddress() {
        final var roof = new Roof("R-02", "address 02");
        repository.add(roof);
        em.flush();

        final var results = repository.search("address");

        assertEquals(1, results.size());
        assertEquals(roof, results.iterator().next());
    }

    @Test
    void searchReturnsMultipleRoofs() {
        final var roof = new Roof("R-03", "address 03");
        final var roof2 = new Roof("R-04", "address 04");
        repository.add(roof);
        repository.add(roof2);
        em.flush();

        final var results = repository.search("roof");

        assertEquals(2, results.size());
    }

    @Test
    void searchReturnsEmptyWhenNoMatch() {
        final var roof = new Roof("R-05", "address 05");
        repository.add(roof);
        em.flush();

        final var results = repository.search("nonexistent");

        assertEquals(0, results.size());
    }


    static class JPARoofRepositoryForTests extends JPARoofRepository {

        JPARoofRepositoryForTests(final EntityManager em) {
            super(() -> em);
        }

    }

}
