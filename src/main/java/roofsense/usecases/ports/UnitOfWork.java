package roofsense.usecases.ports;

import java.util.function.Supplier;

/**
 * Coordinates a group of operations as a single atomic transaction.
 *
 * <p>
 * Ensures the "all-or-nothing" principle: all changes are persisted only if every operation succeeds. If any operation
 * fails, a global rollback is triggered to maintain data integrity and consistency.
 */
@FunctionalInterface
public interface UnitOfWork {

    /**
     * Execute the specified {@link Runnable} within this unit of work.
     *
     * <p>
     * Implementations are responsible for initiating, committing, or rolling back transactions based on the outcome of
     * the runnable.
     *
     * @param runnable the {@link Runnable} to be executed within the unit of work. Must not be {@code null}.
     */
    default void execute(final Runnable runnable) {
        execute(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Execute the specified {@link Supplier} within this unit of work.
     *
     * <p>
     * Implementations are responsible for initiating, committing, or rolling back transactions based on the outcome of
     * the supplier.
     *
     * @param supplier the code block to be executed within the transaction; must not be {@code null}.
     * @param <T>      the type of the returned value.
     *
     * @return the result of the executed supplier.
     */
    <T> T execute(Supplier<T> supplier);

}
