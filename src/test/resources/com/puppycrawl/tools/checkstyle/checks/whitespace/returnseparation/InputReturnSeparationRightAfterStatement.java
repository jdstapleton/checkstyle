package com.puppycrawl.tools.checkstyle.checks.whitespace.returnseparation;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class InputReturnSeparationRightAfterStatement {
    public static void main(String[] args) {
        System.out.println(getMsg(parseInt(args[0])));
    }

    private static String getMsg(int x) {
        int y = x + 1;
        return "Hello" + Objects.toString(y);
    }
}
