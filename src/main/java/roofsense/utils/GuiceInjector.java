package roofsense.utils;

import com.google.inject.Guice;
import com.google.inject.Injector;
import roofsense.config.RoofSenseModule;

/**
 * Singleton holder for the Guice injector instance.
 */
public final class GuiceInjector {

    private static final Injector INSTANCE = Guice.createInjector(new RoofSenseModule());

    private GuiceInjector() {
    }

    /**
     * Returns the shared Guice injector instance.
     *
     * @return the injector instance
     */
    public static Injector get() {
        return INSTANCE;
    }

}
