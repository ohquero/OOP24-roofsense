package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import roofsense.entities.Roof;
import testutils.jpa.JPAExtension;
import testutils.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

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
    }

    @Test
    void getAllWhenSomeEntitiesAreAvailableShouldReturnAllOfThemTest() {
        // given
        final var entities = Set.of(
                BaseJPARepositoryForTests.createRandomValidEntity(),
                BaseJPARepositoryForTests.createRandomValidEntity(),
                BaseJPARepositoryForTests.createRandomValidEntity()
        );
        em.getTransaction().begin();
        entities.forEach(em::persist);
        em.getTransaction().commit();
        em.clear(); // Clear persistence context to force fresh fetch

        // when
        final var result = repository.getAll();

        // then
        assertEquals(entities.size(), result.size());
        assertTrue(result.containsAll(entities));
    }

    @Test
    void getAllWhenNoEntitiesAreAvailableShouldReturnEmptyCollectionTest() {
        //given
        final var entityClass = BaseJPARepositoryForTests.getEntityClass();
        assertEquals(0, em.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList().size());

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
    void existsTransientEntityShouldReturnFalseTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();

        // when & then
        assertFalse(repository.exists(entity));
    }

    @Test
    void existsManagedEntityShouldReturnTrueTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();

        // when & then
        assertTrue(repository.exists(entity));
    }

    @Test
    void existsDetachedEntityShouldReturnTrueTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        // when & then
        assertTrue(repository.exists(entity));
    }

    @Test
    void existsRemovedEntityShouldReturnFalseTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();

        // when & then
        assertFalse(repository.exists(entity));
    }

    @Test
    void saveNullShouldFailTest() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    @Test
    void saveTransientEntityShouldWorkTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();

        // when
        em.getTransaction().begin();
        final var savedEntity = repository.save(entity);
        em.getTransaction().commit();

        // then
        assertNotNull(savedEntity);
    }

    @Test
    void saveManagedEntityShouldMergeTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();

        // when
        final var newAddress = "new address";
        entity.setBuildingAddress(newAddress);
        em.getTransaction().begin();
        final var savedEntity = repository.save(entity);
        em.getTransaction().commit();

        // then
        assertNotNull(savedEntity);
        assertEquals(newAddress, savedEntity.getBuildingAddress());
    }

    @Test
    void saveDetachedEntityShouldMergeTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        // when - entity is now detached, modify and save
        final var newAddress = "New Address";
        entity.setBuildingAddress(newAddress);
        em.getTransaction().begin();
        final var savedEntity = repository.save(entity);
        em.getTransaction().commit();

        // then
        assertNotNull(savedEntity);
        assertEquals(newAddress, savedEntity.getBuildingAddress());
    }

    @Test
    void saveRemovedEntityShouldFailTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.flush();
        em.remove(entity);
        em.getTransaction().commit();

        // when & then
        em.getTransaction().begin();
        assertThrows(IllegalArgumentException.class, () -> repository.save(entity));
        em.getTransaction().commit();
    }

    @Test
    void removeTransientEntityShouldFailTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> repository.remove(entity));
    }

    @Test
    void removeManagedEntityShouldWorkTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();

        // when
        em.getTransaction().begin();
        repository.remove(entity);
        em.getTransaction().commit();

        // then
        assertFalse(em.contains(entity));
    }

    @Test
    void removeDetachedEntityShouldWorkTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.clear();

        // when
        em.getTransaction().begin();
        repository.remove(entity);
        em.getTransaction().commit();

        // then - entity should be removed from database
        final var unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
        final var entityId = unitUtil.getIdentifier(entity);
        final var removedEntity = em.find(Roof.class, entityId);
        assertNull(removedEntity);
    }

    @Test
    void removeRemovedEntityShouldFailTest() {
        // given
        final var entity = BaseJPARepositoryForTests.createValidEntity();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.getTransaction().begin();
        em.remove(entity);
        em.getTransaction().commit();
        em.clear();

        // when & then - attempting to remove an already removed entity throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> repository.remove(entity));
    }

    /**
     * Test implementation of {@link AbstractJPARepository} for testing purposes. Uses {@link Roof} as the entity
     * type just for convenience.
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
