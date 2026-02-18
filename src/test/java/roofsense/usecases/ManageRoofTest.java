package roofsense.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManageRoofTest {

    private UnitOfWork unitOfWork;
    private RoofRepository repository;
    private ManageRoof manageRoof;

    @BeforeEach
    void setUp() {
        unitOfWork = mock(UnitOfWork.class);
        repository = mock(RoofRepository.class);

        // Make execute(Runnable) actually run the runnable (default method delegates to execute(Supplier))
        doAnswer(invocation -> {
            final Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(unitOfWork).execute(any(Runnable.class));

        manageRoof = new ManageRoof(unitOfWork, repository);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new ManageRoof(null, repository));
        assertThrows(NullPointerException.class, () -> new ManageRoof(unitOfWork, null));

        assertDoesNotThrow(() -> new ManageRoof(unitOfWork, repository));
    }

    @Test
    void addNew() {
        // Given
        final var roof = new Roof("roof1", "roof1 address");

        // When
        manageRoof.addNew(roof);

        // Then: the repository.add() method is called with the roof
        verify(repository, times(1)).add(roof);
    }

    @Test
    @SuppressWarnings("unchecked")
    void existsWhenRoofExistsReturnsTrue() {
        // Given
        final var roof = new Roof("roof2", "roof2 address");
        when(unitOfWork.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    final Supplier<Boolean> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(repository.exists(roof)).thenReturn(true);

        // When
        final var result = manageRoof.exists(roof);

        // Then
        assertTrue(result);
        verify(repository, times(1)).exists(roof);
    }

    @Test
    @SuppressWarnings("unchecked")
    void existsWhenRoofDoesNotExistReturnsFalse() {
        // Given
        final var roof = new Roof("roof3", "roof3 address");
        when(unitOfWork.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    final Supplier<Boolean> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
        when(repository.exists(roof)).thenReturn(false);

        // When
        final var result = manageRoof.exists(roof);

        // Then
        assertFalse(result);
        verify(repository, times(1)).exists(roof);
    }

}
