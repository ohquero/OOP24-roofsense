package roofsense.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import roofsense.entities.Roof;
import roofsense.usecases.ports.Repository;
import roofsense.usecases.ports.TransactionManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ManageRoofTest {

    private TransactionManager transactionManager;
    private Repository<Roof> repository;
    private ManageRoof manageRoof;

    @BeforeEach
    void setup() {
        transactionManager = mock(TransactionManager.class);
        //noinspection unchecked
        repository = mock(Repository.class);
        manageRoof = new ManageRoof(transactionManager, repository);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new ManageRoof(null, repository));
        assertThrows(NullPointerException.class, () -> new ManageRoof(transactionManager, null));
        assertDoesNotThrow(() -> new ManageRoof(transactionManager, repository));
    }

    @Test
    void addNew() {
        final var roof = new Roof("roof_code", "roof_building_address");
        manageRoof.addNew(roof);

        final var operationCaptor = ArgumentCaptor.forClass(Runnable.class);
        final var operationIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(transactionManager, times(1)).executeInTransaction(
                operationIdCaptor.capture(),
                operationCaptor.capture()
        );
        operationCaptor.getValue().run();
        verify(repository, times(1)).add(roof);
    }

}
