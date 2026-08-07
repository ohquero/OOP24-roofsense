package roofsense.adapters.ui;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

/**
 * Repository of filters for {@link TextFormatter}s.
 */
public final class TextFormatterFilters {

    /**
     * Filter allowing only geographical coordinates characters.
     */
    public static final UnaryOperator<TextFormatter.Change> GEOGRAPHICAL_COORDINATES_FILTER =
            change -> change.getControlNewText().matches("-?\\d*(\\.\\d*)?") ? change : null;

    private TextFormatterFilters() {
    }

}
