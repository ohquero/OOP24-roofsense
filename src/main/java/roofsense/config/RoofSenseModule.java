package roofsense.config;

import com.google.inject.AbstractModule;
import roofsense.adapters.persistence.JPARoofRepository;
import roofsense.adapters.persistence.JPATransactionManager;
import roofsense.usecases.ports.RoofRepository;
import roofsense.usecases.ports.TransactionManager;

/**
 * Google Guice module defining how to create and inject all the dependencies of the application.
 */
public final class RoofSenseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransactionManager.class).to(JPATransactionManager.class);
        bind(RoofRepository.class).to(JPARoofRepository.class);
    }

}
