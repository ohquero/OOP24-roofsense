package roofsense.utils;

import jakarta.validation.ConstraintViolation;

import java.util.Set;

/**
 * {@link ConstraintViolations} utilities class.
 */
public final class ConstraintViolations {

    private ConstraintViolations() {
    }

    /**
     * Formats a set of constraint violations into a human-readable string.
     *
     * @param violations the set of constraint violations to format
     *
     * @return a formatted string containing all violation messages
     */
    public static String prettyPrintViolations(final Set<? extends ConstraintViolation<?>> violations) {
        if (violations == null || violations.isEmpty()) {
            return "";
        }

        final var builder = new StringBuilder();
        for (final var violation : violations) {
            builder.append("- ").append(violation.getMessage()).append(System.lineSeparator());
        }
        return builder.toString();
    }

}
