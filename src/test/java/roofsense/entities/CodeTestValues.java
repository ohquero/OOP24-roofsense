package roofsense.entities;

/**
 * Shared test values for {@link roofsense.entities.validation.annotations.ValidCode @ValidCode} validation tests.
 */
final class CodeTestValues {

    static final String[] VALID = {
            "validCode",
            "code123",
            "CODE",
            "a",
            "code-with-dashes",
            "123456",
    };

    static final String[] INVALID = {
            null,
            "",
            "code.with.dots",
            "code@symbol",
            "code_with_underscores",
            "code with space",
            " codeWithLeadingSpace",
            "codeWithTrailingSpace ",
            " code with multiple spaces ",
            "code\twith\ttab",
            "code\nwith\nnewline",
            "code\rwith\rcarriagereturn",
            "\t",
            "\n",
            " ",
            "  ",
            "code with\tmixed\nwhitespace",
    };

    private CodeTestValues() {
    }

}
