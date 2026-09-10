package roofsense.adapters.ui;

import java.util.Objects;

/**
 * A graphic element representing a specific geographic point on a {@link LeafletMap}.
 */
public class LeafletMapMarker {

    private final Integer id;

    /**
     * Default constructor.
     *
     * @param id unique identifier of the marker.
     */
    public LeafletMapMarker(final Integer id) {
        this.id = id;
    }

    /**
     * Returns the unique identifier of the marker.
     *
     * @return the unique identifier of the marker.
     */
    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LeafletMapMarker that = (LeafletMapMarker) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "LeafletMapMarker{" + "id=" + id + "}";
    }

}
