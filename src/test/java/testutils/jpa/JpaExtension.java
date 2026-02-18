package testutils.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit 5 Extension for classes testing classes operating with the JPA framework.
 *
 * <p>
 * This extension automatically manages the lifecycle of an {@link EntityManagerFactory} and
 * {@link EntityManager} for JPA-based tests. The {@link EntityManager} is injected into test methods with an
 * {@link EntityManager} parameter.
 */
public class JpaExtension
        implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(JpaExtension.class);
    private static final String EMF_KEY = "EntityManagerFactory";
    private static final String EM_KEY = "EntityManager";

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(final ExtensionContext context) {
        final var emf = Persistence.createEntityManagerFactory("H2");
        context.getStore(NAMESPACE).put(EMF_KEY, emf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(final ExtensionContext context) {
        final var emf = context.getStore(NAMESPACE).get(EMF_KEY, EntityManagerFactory.class);
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.AvoidAccessibilityAlteration")
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final EntityManagerFactory emf = context.getStore(NAMESPACE).get(EMF_KEY, EntityManagerFactory.class);
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory not initialized");
        }

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
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        final var type = parameterContext.getParameter().getType();
        return type.equals(EntityManager.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        final var type = parameterContext.getParameter().getType();

        if (type.equals(EntityManager.class)) {
            return extensionContext.getStore(NAMESPACE).get(EM_KEY, EntityManager.class);
        }

        return null;
    }

}
