package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import roofsense.entities.Coordinates;
import roofsense.entities.Roof;
import testutils.jpa.JPAExtension;
import testutils.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JPAExtension.class)
class JPARoofRepositoryTest {

    private final Collection<Roof> roofs = new ArrayList<>(List.of(
            new Roof("R-02", "street 1 02", new Coordinates(2.0, 2.0)),
            new Roof("R-03", "street 1 03", new Coordinates(3.0, 3.0)),
            new Roof("R-04", "street 2 04", new Coordinates(4.0, 4.0)),
            new Roof("R-05", "street 2 05", new Coordinates(5.0, 5.0))
    ));

    @TestEntityManager
    private EntityManager em;
    private JPARoofRepositoryForTests repository;

    @BeforeEach
    void setUp() {
        repository = new JPARoofRepositoryForTests(em);

        em.getTransaction().begin();
        roofs.forEach(em::persist);
        em.getTransaction().commit();
    }

    @Test
    void searchWithEmptySearchTerm() {
        final var results = repository.search("");

        assertEquals(roofs.size(), results.size());
    }

    @Test
    void searchWithSearchTermMatchingAllRoofs() {
        final var results = repository.search("R-");

        assertEquals(roofs.size(), results.size());
    }

    @Test
    void searchWithSearchTermMatchingSomeRoofs() {
        final var searchTerm = "street 1";
        final var results = repository.search(searchTerm);

        assertEquals(
                roofs.stream().filter(roof -> roof.getBuildingAddress().contains(searchTerm)).count(),
                results.size()
        );
    }

    @Test
    void searchWithSearchTermNotMatchingAnyRoof() {
        final var results = repository.search("street 3");

        assertEquals(0, results.size());
    }

    static final class JPARoofRepositoryForTests extends JPARoofRepository {

        JPARoofRepositoryForTests(final EntityManager em) {
            super(() -> em);
        }

    }

}
