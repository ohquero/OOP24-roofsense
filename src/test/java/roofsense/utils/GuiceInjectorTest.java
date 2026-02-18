package roofsense.utils;

import com.google.inject.Injector;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import roofsense.usecases.ManageRoof;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.UnitOfWork;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Unit tests for {@link GuiceInjector}.
 */
class GuiceInjectorTest {

    @Test
    void testGetReturnsSingletonInjector() {
        final Injector i1 = GuiceInjector.get();
        final Injector i2 = GuiceInjector.get();

        assertNotNull(i1, "Injector instance should not be null");
        assertSame(i1, i2, "GuiceInjector.get() must return the same singleton instance");
    }

    @Test
    void testInjectorProvidesCoreBindings() {
        final Injector injector = GuiceInjector.get();

        // Verify that key bindings from RoofSenseModule are resolvable
        assertNotNull(injector.getInstance(UnitOfWork.class), "UnitOfWork should be bound");
        assertNotNull(injector.getInstance(RoofRepository.class), "RoofRepository should be bound");
        assertNotNull(injector.getInstance(ManageRoof.class), "ManageRoof should be injectable");
        assertNotNull(injector.getInstance(EntityManagerFactory.class), "EntityManagerFactory should be provided");
    }

}
