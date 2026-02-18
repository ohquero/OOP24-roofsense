package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import roofsense.entities.Roof;
import roofsense.entities.validation.annotations.ValidCode;
import testutils.jpa.JpaExtension;
import testutils.jpa.TestEntityManager;

import static org.hibernate.exception.ConstraintViolationException.ConstraintKind.UNIQUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(JpaExtension.class)
class JPARoofRepositoryTest {

    private Roof roof;
    @TestEntityManager
    private EntityManager em;
    private JPARoofRepositoryForTesting repository;

    @BeforeEach
    void setUp() {
        roof = new Roof("roofcode", "roofaddress");
        repository = new JPARoofRepositoryForTesting(em);
        em.getTransaction().begin(); // Avvia la transazione qui
    }

    @AfterEach
    void tearDown() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback(); // Annulla la transazione alla fine del test
        }
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
        final var invalidRoof = new Roof(null, "address");

        final var exception = assertThrows(
                ConstraintViolationException.class,
                () -> {
                    repository.add(invalidRoof);
                    em.flush();
                }
        );

        final var constraintViolations = exception.getConstraintViolations();

        assertEquals(1, constraintViolations.size());
        final var constraintViolation = constraintViolations.iterator().next();
        assertEquals(invalidRoof, constraintViolation.getRootBean());
        assertEquals("code", constraintViolation.getPropertyPath().toString());
        assertEquals(ValidCode.class, constraintViolation.getConstraintDescriptor().getAnnotation().annotationType());
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

        JPARoofRepositoryForTesting(final EntityManager em) {
            super(() -> em);
        }

    }

}
