package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.io.*;
import java.util.*;

public class InputMultilineStatementConditionals {
    private final Random random = new Random(0xdeadbeef);
    private int x, y;
    public void failOnNoEmptyLineAfter() {
        if (random.nextInt() % 3 == 0) {
            System.out.println("Number is divisible by 3");
        }
        System.out.println();
        y = random.nextInt(500);

        if (y % 3 == 0) {
            System.out.println("Number is divisible by 3");
        } else {
            System.out.println("Number is not divisible by 3");
        }
        System.out.println();

        if (y % 6 == 0) {
            System.out.println("Number is divisible by 6");
        } else if (y % 4 == 0) {
            System.out.println("Number is not divisible by 4");
        } else if (y % 7 == 0) {
            System.out.println("Number is not divisible by 7");
        } else {
            System.out.println("Number is not divisible by 4, 6, or 7");
        }
        System.out.println();

        while (random.nextInt() % 4 == 0) {
            System.out.println("Hey we aren't divided by a quarter!");
            System.out.println("More than one line in the block.");
        }
        x = y + random.nextInt(1000) * 2;

        for (int z = y; z < x; z++) {
            if (z % 10 == 0) {
                System.out.println("%");
            } else {
                System.out.print(".");
            }
        }
        System.out.println();

        while (random.nextInt() % 8 == 0) {
            System.out.println("Every 8 is 0!");
            System.out.println("We are printing them too.");
        }
        System.out.println();

        switch (x) {
            case 0:
            case 1: {
                System.out.println("Wow a 0 or 1 was hit.");
                break;
            }
            case 64: {
                System.out.println("A power of 2 that sums to 10 was hit.");
            }

            {
                System.out.println("No one should do this.");
            }
            break;
            case 1498: {
                System.out.println("Critial hit!  Max value.");
            }

            {
                break;
            }
            default:
                System.out.println("Sad, none of the specials values were hit.");
        }
        System.out.println();
    }
    {
        System.out.println("Why add this to an initializer in the middle of methods");
    }
    public void failOnNoEmptyLineBefore() {
        y = random.nextInt(500);
        if (random.nextInt() % 3 == 0) {
            System.out.println("Number is divisible by 3");
        }

        x = y + random.nextInt(1000) * 2;
        while (random.nextInt() % 4 == 0) {
            System.out.println("Hey we aren't divided by a quarter!");
            System.out.println("More than one line in the block.");
        }

        System.out.println();
        for (int z = y; z < x; z++) {
            if (z % 10 == 0) {
                System.out.println("%");
            } else {
                System.out.print(".");
            }
        }

        System.out.println();
        while (random.nextInt() % 8 == 0) {
            System.out.println("Every 8 is 0!");
            System.out.println("We are printing them too.");
        }

        System.out.println();
        switch (x) {
            case 0:
            case 1: {
                System.out.println("Wow a 0 or 1 was hit.");
                break;
            }
            case 64: {
                System.out.println("A power of 2 that sums to 10 was hit.");
            }
            {
                System.out.println("No one should do this also.");
                break;
            }
            case 1498: {
                System.out.println("Critial hit!  Max value.");
            }

            {
                break;
            }
            default:
                System.out.println("Sad, none of the specials values were hit.");
        }
    }

    // This caused a nullpointer, as the sibling of the throw's ; was null, this is
    // not the case outside of a switch.
    public void throwWithinDefaultCase() {
        int x = random.nextInt();

        switch (x) {
            case 0:
                System.out.println();
            default:
                throw new RuntimeException("Unsupported input/output operation " +
                        String.format("Please do something supported: %s.", this.toString()));
        }
    }

    public void validForLoop() {
        for (int z = y;
             z < x;
             z++) {
            if (z % 10 == 0) {
                System.out.println("%");
            } else {
                System.out.print(".");
            }
        }

        for (int x = 2,
             y = 6;
             x < y;
             x += 2,
                     y += 1)
            System.out.println();

        for (int x = 2,
             y = 6;
             x < y;
             x += 2, y += 1) {
            System.out.println();
        }
    }
}
