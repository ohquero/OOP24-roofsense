package roofsense.usecases.ports;

import java.util.function.Supplier;

/**
 * Defines a contract for managing operations within transactional boundaries.
 *
 * <p>
 * Implementations of this interface provide the ability to execute a given operation while ensuring transactional
 * integrity. This is particularly useful in scenarios where a series of actions must be performed atomically to
 * maintain consistency.
 *
 * <p>
 * Typical use cases include database interactions, where operations need to be grouped into transactional units to
 * ensure data integrity across multiple actions or entities.
 *
 * @see Repository
 */
public interface TransactionManager {

    /**
     * This method allows executing the specified code block within a transaction.
     *
     * <p>
     * Implementations are responsible for initiating, committing, or rolling back transactions based on the outcome of
     * the operation.
     *
     * @param operationId the unique identifier of the operation, to be used in output logs; must not be {@code null} or
     *                    empty.
     * @param operation   the code block to be executed within the transaction; must not be {@code null}.
     */
    void executeInTransaction(String operationId, Runnable operation);

    /**
     * This method allows executing the specified code block within a transaction and returning a result.
     *
     * <p>
     * Implementations are responsible for initiating, committing, or rolling back transactions based on the outcome of
     * the operation.
     *
     * @param operationId the unique identifier of the operation, to be used in output logs; must not be {@code null} or
     *                    empty.
     * @param operation   the code block to be executed within the transaction; must not be {@code null}.
     * @param <T>         the type of the returned value.
     *
     * @return the result of the executed operation.
     */
    <T> T executeInTransaction(String operationId, Supplier<T> operation);

}
