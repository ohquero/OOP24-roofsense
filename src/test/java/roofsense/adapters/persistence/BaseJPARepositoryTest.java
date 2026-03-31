package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import roofsense.entities.Roof;
import testutils.jpa.JPAExtension;
import testutils.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.hibernate.exception.ConstraintViolationException.ConstraintKind.UNIQUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link AbstractJPARepository}.
 *
 * <p>
 * Uses {@link Roof} entity as a test entity since it's already available and mapped.
 **/
@ExtendWith(JPAExtension.class)
@SuppressWarnings("PMD.LinguisticNaming")
class BaseJPARepositoryTest {

    @TestEntityManager
    private EntityManager em;
    private BaseJPARepositoryForTests repository;

    @BeforeEach
    void setUp() {
        repository = new BaseJPARepositoryForTests(em);
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    void getAllWhenSomeEntitiesAreAvailableShouldReturnAllOfThemTest() {
        // given
        final var entities = Set.of(
                BaseJPARepositoryForTests.createRandomValidEntity(),
                BaseJPARepositoryForTests.createRandomValidEntity(),
                BaseJPARepositoryForTests.createRandomValidEntity()
        );
        entities.forEach(em::persist);
        em.flush();
        em.clear(); // Clear persistence context to force fresh fetch

        // when
        final var result = repository.getAll();

        // then
        assertEquals(entities.size(), result.size());
        assertTrue(result.containsAll(entities));
    }

    @Test
    void getAllWhenNoEntitiesAreAvailableShouldReturnEmptyListTest() {
        //given
        final var entityClass = BaseJPARepositoryForTests.getEntityClass();
        em.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();

        // when
        final var result = repository.getAll();

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void existsNullShouldFailTest() {
        assertThrows(NullPointerException.class, () -> repository.exists(null));
    }

    @Test
    void existsWhenEntityIsManagedShouldReturnTrueTest() {
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.persist(entity);
        em.flush();

        // When & Then: return true because the entity already is in the persistence context
        assertTrue(repository.exists(entity));

        // given - entity is then deleted from persistence context
        em.remove(entity);
        em.flush();

        // when & then: returns false
        assertFalse(repository.exists(entity));
    }

    @Test
    void existsWhenEntityIsDetachedButPersistedShouldReturnTrueTest() {
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.persist(entity);
        em.flush();
        em.clear();

        assertTrue(repository.exists(entity));
    }

    @Test
    void existsWhenEntityDoesNotExistsShouldReturnFalseTest() {
        final var entity = BaseJPARepositoryForTests.createValidEntity();

        // When & Then: entity does not exist in the DB
        assertFalse(repository.exists(entity));
    }

    @Test
    void addNullShouldFailTest() {
        assertThrows(IllegalArgumentException.class, () -> repository.add(null));
    }

    @Test
    void addValidEntityWorksTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        assertNull(entity.getId());

        // when
        repository.add(entity);
        em.flush();

        // then
        assertNotNull(entity.getId());
        assertEquals(entity, em.find(Roof.class, entity.getId()));

        // TODO: when & then: is not possible to re-add an already added entity
        repository.add(entity);
        em.flush();
    }

    @ParameterizedTest
    @MethodSource(
            "roofsense.adapters.persistence.BaseJPARepositoryTest$BaseJPARepositoryForTests"
                    + "#invalidEntitiesWithConstraintViolationsCount"
    )
    void addInvalidEntityShouldFailTest(final Roof roof, final int constraintViolationsCount) {
        final var exception = assertThrows(
                ConstraintViolationException.class, () -> {
                    repository.add(roof);
                    em.flush();
                }
        );
        assertEquals(constraintViolationsCount, exception.getConstraintViolations().size());
    }

    @Test
    void addEqualEntitiesShouldFailTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        final var equalEntity = BaseJPARepositoryForTests.createValidEntity();
        assertEquals(entity, equalEntity);
        em.persist(entity);
        em.flush();

        // when & then
        final var exception = assertThrows(
                org.hibernate.exception.ConstraintViolationException.class, () -> { //TODO: correct?
                    repository.add(equalEntity);
                    em.flush();
                }
        );
        assertEquals(UNIQUE, exception.getKind());
    }

    @Test
    void updateDetachedEntityShouldWorkTest() {
        updateShouldUpdateEntity(true);
    }

    @Test
    void updateManagedEntityShouldWorkTest() {
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
    void removeDetachedEntityShouldWorkTest() {
        removeShouldDeleteEntity(true);
    }

    @Test
    void removeManagedEntityShouldWorkTest() {
        removeShouldDeleteEntity(false);
    }

    /**
     * {@link AbstractJPARepository#remove(Object)} test method core.
     *
     * @param detachEntity whether to detach the entity before removing.
     */
    void removeShouldDeleteEntity(final boolean detachEntity) {
        // Given: a roof persisted in the DB
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.persist(entity);
        em.flush();
        if (detachEntity) {
            em.clear();
        }

        // When: removing the roof
        repository.remove(entity);
        em.flush();

        // Then: entity is deleted from the DB
        assertTrue(repository.getByCode(entity.getCode()).isEmpty());
    }

    /**
     * Test implementation of {@link AbstractJPARepository} for testing purposes. Uses {@link Roof} as the entity type
     * just for convenience.
     */
    private static class BaseJPARepositoryForTests extends AbstractJPARepository<Roof> {

        private static final Random RANDOM = new Random();

        BaseJPARepositoryForTests(final EntityManager em) {
            super(Roof.class, () -> em);
        }

        public static Class<Roof> getEntityClass() {
            return Roof.class;
        }

        public static Roof createValidEntity() {
            return new Roof("R-01", "roof 1 address");
        }

        public static Roof createRandomValidEntity() {
            final var randomNumber = RANDOM.nextInt(100_000_000);
            return new Roof("R-" + randomNumber, "Address-" + randomNumber);
        }

        public static Stream<Arguments> invalidEntitiesWithConstraintViolationsCount() {
            return Stream.of(
                    Arguments.of(new Roof(null, null), 2),
                    Arguments.of(new Roof(null, "roof 3 address"), 1),
                    Arguments.of(new Roof("R-04", null), 1)
            );
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
