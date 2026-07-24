package roofsense.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import roofsense.entities.Coordinates;
import roofsense.entities.Roof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoofsManagerTest {

    private UnitOfWork unitOfWork;
    private RoofRepository repository;
    private RoofsManager roofsManager;

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

        roofsManager = new RoofsManager(unitOfWork, repository);
    }

    @Test
    void constructor() {
        assertThrows(NullPointerException.class, () -> new RoofsManager(null, repository));
        assertThrows(NullPointerException.class, () -> new RoofsManager(unitOfWork, null));

        assertDoesNotThrow(() -> new RoofsManager(unitOfWork, repository));
    }

    @Test
    void existsShouldCallRepositoryExistsMethodAndReturnItsOutputTest() {
        // Given
        final var roof = new Roof("roof2", "roof2 address", new Coordinates(2.0, 2.0));
        when(repository.exists(roof)).thenReturn(true);

        // When
        final var result = roofsManager.exists(roof);

        // Then
        assertTrue(result);
        verify(repository, times(1)).exists(roof);
    }

    @SuppressWarnings("PMD.LinguisticNaming")
    @Test
    void getAllShouldCallRepositoryGetAllMethodAndReturnItsOutputTest() {
        // Given
        final var roof1 = new Roof("roof3", "address3", new Coordinates(3.0, 3.0));
        final var roof2 = new Roof("roof4", "address4", new Coordinates(4.0, 4.0));
        final var roof3 = new Roof("roof5", "address5", new Coordinates(5.0, 5.0));
        final var expectedRoofs = List.of(roof1, roof2, roof3);

        when(repository.getAll()).thenReturn(expectedRoofs);

        // When
        final var result = roofsManager.getAll();

        // Then
        verify(repository, times(1)).getAll();
        assertEquals(expectedRoofs, result);
    }

    @Test
    void saveShouldCallRepositorySaveMethodAndReturnItsOutputTest() {
        // given
        final var roof = new Roof("roof6", "address6", new Coordinates(6.0, 6.0));
        when(repository.save(roof)).thenReturn(roof);

        // when
        final var returnedRoof = roofsManager.save(roof);

        // then
        verify(repository, times(1)).save(roof);
        assertEquals(roof, returnedRoof);
    }

    @Test
    void removeShouldCallRepositoryRemoveMethodTest() {
        // given
        final var roof = new Roof("roof7", "address7", new Coordinates(7.0, 7.0));

        // when
        roofsManager.remove(roof);

        // then
        verify(repository, times(1)).remove(roof);
    }

    @Test
    void searchShouldCallRepositorySearchMethodTest() {
        // given
        final var searchTerm = "search_term";

        // when
        roofsManager.search(searchTerm);

        // then
        verify(repository, times(1)).search(searchTerm);
    }

}
