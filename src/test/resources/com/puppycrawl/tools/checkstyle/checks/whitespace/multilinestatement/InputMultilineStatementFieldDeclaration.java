package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.io.File;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.meta.When;

import com.google.common.io.Files;

public class InputMultilineStatementFieldDeclaration {
    private final String SOME_LONG_STRING = "Some strings are long enough to" +
            "make new liens in the middle of a string.";

    private final Random random =
            new Random(0xdeadbeef);
    private final boolean x = Boolean.getBoolean(SOME_LONG_STRING);

    /**
     * Helper class encapsulating the command line options and positional parameters.
     */
    private static class CliOptions {

        /**
         * The command line option to specify the output file.
         */
        @Nonnull(when = When.MAYBE)
        private File outputFile;

        /**
         * The command line positional parameter to specify the input file.
         */
        @Nonnull(when = When.UNKNOWN)
        private File inputFile =
                Files.createTempDir();
    }
}
