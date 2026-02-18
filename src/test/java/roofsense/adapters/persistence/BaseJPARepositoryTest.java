package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import roofsense.entities.Roof;
import testutils.jpa.JpaExtension;
import testutils.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link AbstractJPARepository}.
 *
 * <p>Uses {@link Roof} entity as a test entity since it's already available and mapped.
 *
 **/
@ExtendWith(JpaExtension.class)
@SuppressWarnings("PMD.LinguisticNaming")
class BaseJPARepositoryTest {

    private static final String TEST_CODE = "test-code";
    private static final String TEST_ADDRESS = "test-address";

    @TestEntityManager
    private EntityManager em;
    private TestRepository repository;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        repository = new TestRepository(em);
        em.getTransaction().begin();
    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    void getAllShouldReturnAllEntities() {
        final var roofs =
                Set.of(new Roof("code1", "address1"), new Roof("code2", "address2"), new Roof("code3", "address3"));

        roofs.forEach(em::persist);
        em.flush();
        em.clear(); // Clear persistence context to force fresh fetch

        final List<Roof> result = repository.getAll();

        assertEquals(roofs.size(), result.size());
        assertTrue(result.containsAll(roofs));
    }

    @Test
    void getAllShouldReturnEmptyListWhenNoEntities() {
        final List<Roof> result = repository.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void existsShouldReturnTrueWhenEntityIsManaged() {
        final var roof = new Roof(TEST_CODE, TEST_ADDRESS);
        em.persist(roof);
        em.flush();

        // When & Then: return true because the entity already is in the persistence context
        assertTrue(repository.exists(roof));
    }

    @Test
    void existsShouldReturnTrueWhenEntityIsDetachedButPersisted() {
        final var roof = new Roof(TEST_CODE, TEST_ADDRESS);
        em.persist(roof);
        em.flush();
        em.detach(roof);

        assertTrue(repository.exists(roof));
    }

    @Test
    void existsShouldReturnFalseWhenEntityDoesNotExists() {
        final var roof = new Roof(TEST_CODE, TEST_ADDRESS);

        // When & Then: entity does not exist in the DB
        assertFalse(repository.exists(roof));
    }

    @Test
    void addShouldPersistEntity() {
        // Given: a new roof
        final var roof = new Roof("code0", "address0");

        // When: adding the roof to the repository
        repository.add(roof);
        em.flush();
        em.clear(); // Clear persistence context to force fresh fetch

        // Then: entity is persisted and has an ID
        assertTrue(repository.getByCode(roof.getCode()).isPresent());
    }

    @Test
    void updateShouldUpdateDetachedEntity() {
        updateShouldUpdateEntity(true);
    }

    @Test
    void updateShouldUpdateManagedEntity() {
        updateShouldUpdateEntity(false);
    }

    /**
     * {@link AbstractJPARepository#update(Object)} test method core.
     *
     * @param detachEntity whether to detach the entity before updating.
     */
    void updateShouldUpdateEntity(final boolean detachEntity) {
        // Given: a roof persisted in the DB
        final String originalCode = "original-code";
        final var roof = new Roof(originalCode, "original-address");
        em.persist(roof);
        em.flush();
        if (detachEntity) {
            em.clear();
        }

        // When: updating the roof
        final String updatedCode = "updated-code";
        roof.setCode(updatedCode);
        repository.update(roof);
        em.flush();
        if (detachEntity) {
            em.clear();
        }

        // Then: entity is updated and has the new code
        assertTrue(repository.getByCode(originalCode).isEmpty());
    }

    @Test
    void removeShouldDeleteDetachedEntity() {
        removeShouldDeleteEntity(true);
    }

    @Test
    void removeShouldDeleteManagedEntity() {
        removeShouldDeleteEntity(false);
    }

    /**
     * {@link AbstractJPARepository#remove(Object)} test method core.
     *
     * @param detachEntity whether to detach the entity before removing.
     */
    void removeShouldDeleteEntity(final boolean detachEntity) {
        // Given: a roof persisted in the DB
        final var roof = new Roof("roof-to-remove", "address");
        em.persist(roof);
        em.flush();
        if (detachEntity) {
            em.clear();
        }

        // When: removing the roof
        repository.remove(roof);
        em.flush();

        // Then: entity is deleted from the DB
        assertTrue(repository.getByCode(roof.getCode()).isEmpty());
    }

    /**
     * Test implementation of {@link AbstractJPARepository} for testing purposes. Uses {@link Roof} as the entity type
     * just for convenience.
     */
    private static class TestRepository extends AbstractJPARepository<Roof> {

        TestRepository(final EntityManager em) {
            super(Roof.class, () -> em);
        }

        @Override
        protected Optional<Roof> findEqual(final Roof entity) {
            return getByCode(entity.getCode());
        }

        private Optional<Roof> getByCode(final String code) {
            final var roof = getEntityManager()
                    .createQuery("FROM Roof WHERE code = :code", Roof.class)
                    .setParameter("code", code)
                    .getSingleResultOrNull();
            return Optional.ofNullable(roof);
        }

    }

}
