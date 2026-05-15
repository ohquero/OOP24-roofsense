package roofsense.usecases.ports;

import roofsense.entities.Roof;

import java.util.Collection;

/**
 * Represents a repository for managing {@link Roof} entities. Extends the {@link Repository} interface.
 *
 * @see Repository
 */
public interface RoofRepository extends Repository<Roof> {

    /**
     * Searches for all {@link Roof} entities containing the specified search term in any field.
     *
     * @param searchTerm the {@link String} to search for.
     *
     * @return a {@link Collection} of {@link Roof} objects.
     */
    Collection<Roof> search(String searchTerm);

}
