package com.puppycrawl.tools.checkstyle.checks.whitespace.returnseparation;

import java.util.Objects;

import static java.lang.Integer.parseInt;

public class InputReturnSeparationRightAfterBlock {
    public static void main(String[] args) {
        System.out.println(getMsg(parseInt(args[0])));
    }

    private static String getMsg(int x) {
        if (x % 2 == 0) {
            x++;
        }
        return "Hello" + Objects.toString(x);
    }
}
