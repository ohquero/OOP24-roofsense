package roofsense.usecases.ports;

/**
 * Rappresenta una serie di operazioni eseguite tramite i repository che devono essere eseguite in un'unica transazione.
 */
@FunctionalInterface
public interface TransactionManager {

    void executeInTransaction(String operationId, Runnable operation);

}
