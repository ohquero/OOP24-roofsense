package testutils.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 Extension for test classes testing classes operating with the JPA framework.
 *
 * <p>
 * This extension automatically manages the lifecycle of {@link EntityManager} and {@link EntityManagerFactory},
 * which are re-created for each test to ensure a clean database state for every test.
 * The EntityManager is made available to the test class via fields annotated with {@link TestEntityManager}.
 *
 * <p>
 * The provided EntityManager works with an in-memory H2 database. This approach has been implemented to make tests
 * more useful by simulating interactions with a real database.
 */
public final class JPAExtension implements BeforeEachCallback, AfterEachCallback {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(JPAExtension.class);
    private static final String EMF_KEY = "EntityManagerFactory";
    private static final String EM_KEY = "EntityManager";

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var emf = Persistence.createEntityManagerFactory("H2");
        context.getStore(NAMESPACE).put(EMF_KEY, emf);

        final EntityManager em = emf.createEntityManager();
        context.getStore(NAMESPACE).put(EM_KEY, em);

        final var instance = context.getRequiredTestInstance();
        Class<?> clazz = instance.getClass();

        // Inject EntityManager into test instance fields
        while (clazz != null) {
            for (final var field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(TestEntityManager.class)) {
                    field.setAccessible(true);
                    field.set(instance, em);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterEach(final ExtensionContext context) {
        final EntityManager em = context.getStore(NAMESPACE).get(EM_KEY, EntityManager.class);
        if (em != null && em.isOpen()) {
            em.close();
        }

        final EntityManagerFactory emf = context.getStore(NAMESPACE).get(EMF_KEY, EntityManagerFactory.class);
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
