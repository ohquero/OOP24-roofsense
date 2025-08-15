package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.entities.Roof;

import static org.hibernate.exception.ConstraintViolationException.ConstraintKind.UNIQUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JPARoofRepositoryTest {

    private static final EntityManagerFactory emf = H2PersistenceUnit.getEntityManagerFactory();
    private static final JPARoofRepositoryForTesting repository = new JPARoofRepositoryForTesting();

    private Roof roof;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        roof = new Roof("roof_code", "roof_address");
        em = emf.createEntityManager();
        repository.setEntityManager(em);
        em.getTransaction().begin(); // Avvia la transazione qui
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // Annulla la transazione alla fine del test
        }
        em.close();
    }

    @Test
    void add() {
        assertNull(roof.getId());

        repository.add(roof);
        em.flush();

        assertNotNull(roof.getId());
        assertEquals(roof, em.find(Roof.class, roof.getId()));
    }

    @Test
    void addInvalidRoof() {
        final var roof = new Roof(null, "address");

        final var exception = assertThrows(
                ConstraintViolationException.class,
                () -> {
                    repository.add(roof);
                    em.flush();
                }
        );

        final var constraintViolations = exception.getConstraintViolations();

        assertEquals(1, constraintViolations.size());
        final var constraintViolation = constraintViolations.iterator().next();
        assertEquals(roof, constraintViolation.getRootBean());
        assertEquals("code", constraintViolation.getPropertyPath().toString());
        assertEquals(NotNull.class, constraintViolation.getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void addWithEqualRoofs() {
        final var roof2 = new Roof(roof.getCode(), "another_roof_address");

        assertEquals(roof, roof2);

        repository.add(roof);
        em.flush();

        final var exception = assertThrows(
                org.hibernate.exception.ConstraintViolationException.class,
                () -> {
                    repository.add(roof2);
                    em.flush();
                }
        );

        assertEquals(UNIQUE, exception.getKind());
    }

    private static class JPARoofRepositoryForTesting extends JPARoofRepository {

        private EntityManager entityManager;

        @Override
        protected EntityManager getEntityManager() {
            return entityManager;
        }

        public void setEntityManager(final EntityManager entityManager) {
            this.entityManager = entityManager;
        }

    }

}
