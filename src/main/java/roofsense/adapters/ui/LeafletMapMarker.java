package roofsense.adapters.ui;

import java.util.Objects;

/**
 * A graphic element representing a specific geographic point on a {@link LeafletMap}.
 */
public class LeafletMapMarker {

    private final String id;

    /**
     * Default constructor.
     *
     * @param id unique identifier of the marker.
     */
    public LeafletMapMarker(final String id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier of the marker.
     *
     * @return the unique identifier of the marker.
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LeafletMapMarker that = (LeafletMapMarker) o;
        return Objects.equals(id, that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LeafletMapMarker{" + "id=" + id + "}";
    }

}
