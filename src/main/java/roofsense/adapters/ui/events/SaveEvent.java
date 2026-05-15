package roofsense.adapters.ui.events;

import javafx.event.Event;
import javafx.event.EventType;

import java.io.Serial;

/**
 * Event triggered when a new object is saved.
 *
 * @param <T> the type of the object being saved.
 */
public final class SaveEvent<T> extends Event {

    @Serial
    private static final long serialVersionUID = -19386770972753512L;

    private final T object;

    /**
     * Constructor.
     *
     * @param object    the object being saved.
     * @param eventType the type of the event.
     */
    public SaveEvent(
            final T object,
            final EventType<? extends SaveEvent<?>> eventType
    ) {
        super(eventType);
        this.object = object;
    }

    /**
     * Returns the object being saved.
     *
     * @return the object being saved.
     */
    public T getObject() {
        return object;
    }

}
