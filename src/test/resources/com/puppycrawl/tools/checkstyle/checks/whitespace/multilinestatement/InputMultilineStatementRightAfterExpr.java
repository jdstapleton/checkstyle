package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.util.Random;

public class InputMultilineStatementRightAfterExpr {
    private final Random random = new Random(0xdeadbeef);
    private final boolean x = Boolean.getBoolean("TEST_BOOLEAN");
    private final boolean y;

    public InputMultilineStatementRightAfterExpr() {
        y = !x
                || random.nextBoolean();
        System.out.println("Y is " + y);
    }
}
