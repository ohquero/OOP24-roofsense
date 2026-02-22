package roofsense.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @SuppressWarnings("unchecked")
    void setUp() {
        unitOfWork = mock(UnitOfWork.class);
        when(unitOfWork.execute(any(Supplier.class)))
                .thenAnswer(invocation -> {
                    final Supplier<Boolean> supplier = invocation.getArgument(0);
                    return supplier.get();
                });
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
    void existsWhenRoofExistsReturnsTrue() {
        // Given
        final var roof = new Roof("roof2", "roof2 address");
        when(repository.exists(roof)).thenReturn(true);

        // When
        final var result = manageRoof.exists(roof);

        // Then
        assertTrue(result);
        verify(repository, times(1)).exists(roof);
    }

    @Test
    void existsWhenRoofDoesNotExistReturnsFalse() {
        // Given
        final var roof = new Roof("roof3", "roof3 address");
        when(repository.exists(roof)).thenReturn(false);

        // When
        final var result = manageRoof.exists(roof);

        // Then
        assertFalse(result);
        verify(repository, times(1)).exists(roof);
    }

    @Test
    void testGetAllReturnsAllRoofs() {
        // Given
        final var roof1 = new Roof("roof1", "address1");
        final var roof2 = new Roof("roof2", "address2");
        final var roof3 = new Roof("roof3", "address3");
        final var expectedRoofs = List.of(roof1, roof2, roof3);

        when(repository.getAll()).thenReturn(expectedRoofs);

        // When
        final var result = manageRoof.getAll();

        // Then
        assertEquals(expectedRoofs, result);
        verify(repository, times(1)).getAll();
    }

    @Test
    void testGetAllReturnsEmptyListWhenNoRoofs() {
        // Given
        final var expectedRoofs = List.<Roof>of();
        when(repository.getAll()).thenReturn(expectedRoofs);

        // When
        final var result = manageRoof.getAll();

        // Then
        assertEquals(expectedRoofs, result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).getAll();
    }

}
