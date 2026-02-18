package roofsense.adapters.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link JPAUnitOfWork}.
 */
@SuppressWarnings("PMD.LinguisticNaming")
class JPAUnitOfWorkTest {

    private EntityManager em;
    private EntityTransaction tx;
    private JPAUnitOfWork unitOfWork;

    @BeforeEach
    void setUp() {
        final EntityManagerFactory emf = mock(EntityManagerFactory.class);
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);
        unitOfWork = new JPAUnitOfWork(emf);
    }

    @Test
    void constructorRejectsNull() {
        assertThrows(NullPointerException.class, () -> new JPAUnitOfWork(null));
    }

    @Test
    void executeReturnsSupplierResult() {
        final var expected = 42;
        final var result = unitOfWork.execute(() -> expected);
        assertEquals(expected, result);
    }

    @Test
    void executeCommitsAndClosesEntityManagerOnSuccess() {
        unitOfWork.execute(() -> null);

        verify(tx).begin();
        verify(tx).commit();
        verify(tx, never()).rollback();
        verify(em).close();
    }

    @Test
    void executeRollsBackAndWrapsConstraintViolationException() {
        when(tx.isActive()).thenReturn(true);
        final var cause = mock(ConstraintViolationException.class);
        final var persistenceException = new PersistenceException(cause);

        final var thrown = assertThrows(
                IllegalArgumentException.class, () -> unitOfWork.execute(() -> {
                    throw persistenceException;
                })
        );

        assertSame(persistenceException, thrown.getCause());
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void executeRollsBackAndWrapsHibernateConstraintViolationException() {
        when(tx.isActive()).thenReturn(true);
        final var cause = mock(org.hibernate.exception.ConstraintViolationException.class);
        final var persistenceException = new PersistenceException(cause);

        final var thrown = assertThrows(
                IllegalArgumentException.class, () -> unitOfWork.execute(() -> {
                    throw persistenceException;
                })
        );

        assertSame(persistenceException, thrown.getCause());
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void executeRollsBackAndWrapsGenericPersistenceException() {
        doReturn(true).when(tx).isActive();
        final var exception = new PersistenceException("generic failure");
        doThrow(exception).when(tx).commit();

        final var thrown = assertThrows(
                IllegalStateException.class, () -> unitOfWork.execute(() -> true)
        );

        assertSame(exception, thrown.getCause());
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void executeRollsBackAndSignalsUnexpectedException() {
        doReturn(true).when(tx).isActive();
        final var exception = new RuntimeException("unexpected exception");
        doThrow(exception).when(tx).commit();

        final var thrown = assertThrows(
                exception.getClass(), () -> unitOfWork.execute(() -> true)
        );

        assertSame(exception, thrown);
        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void executeSkipsRollbackWhenTransactionNotActive() {
        when(tx.isActive()).thenReturn(false);
        final var persistenceException = new PersistenceException("fail");

        assertThrows(
                IllegalStateException.class, () -> unitOfWork.execute(() -> {
                    throw persistenceException;
                })
        );

        verify(tx, never()).rollback();
        verify(em).close();
    }

    @Test
    void executeRejectsNestedTransactions() {
        assertThrows(IllegalStateException.class, () -> unitOfWork.execute(() -> unitOfWork.execute(() -> "nested")));
    }

    @Test
    void getEntityManagerThrowsOutsideTransaction() {
        assertThrows(IllegalStateException.class, unitOfWork::getEntityManager);
    }

    @Test
    void getEntityManagerReturnsCurrentEmDuringTransaction() {
        unitOfWork.execute(() -> {
            final var currentEm = unitOfWork.getEntityManager();
            assertSame(em, currentEm);
            return null;
        });
    }

    @Test
    void executeClosesEntityManagerEvenOnCommitFailure() {
        doThrow(new IllegalStateException("commit failed")).when(tx).commit();
        when(tx.isActive()).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> unitOfWork.execute(() -> "value"));

        verify(em).close();
    }

}
