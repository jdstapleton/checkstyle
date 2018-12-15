package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.util.Random;

public class InputMultilineStatementRightAfterDecl {
    private final Random random = new Random(0xdeadbeef);
    private final boolean x = Boolean.getBoolean("TEST_BOOLEAN");
    private final boolean y;

    public InputMultilineStatementRightAfterDecl() {
        int someRandomNumber = x == true ?
                random.nextInt(100) :
                random.nextInt(500);
        y = someRandomNumber % 2 == 0;
    }
}
