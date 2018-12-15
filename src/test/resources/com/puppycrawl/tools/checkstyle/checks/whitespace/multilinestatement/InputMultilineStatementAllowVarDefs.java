package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class InputMultilineStatementAllowVarDefs
{
    private final static Random random = new Random(0xdeadbeef);
    public void allowVarDefs()
    {
        int y = random.nextInt() % 3;
        if (y == 0) {
            System.out.println("Number is divisible by 3");
        }

        int x = y + random.nextInt(1000) * 2;
        while (random.nextInt() % 4 == 0) {
            System.out.println("Hey we aren't divided by a quarter!");
            System.out.println("More than one line in the block.");
        }

        int zap = x * 2;
        for (int z = zap; z < x; z++) {
            if (z % 10 == 0) {
                System.out.println("%");
            } else {
                System.out.print(".");
            }

            int zep = z * 5;
            if (zep % 5 == 0) {
                System.out.print(">");
            }
            else if (zep % 5 == 1) {
                System.out.print(" ");
            }

            System.out.println();
        }

        int zip = zap
                + random.nextInt() % 8;
        while (zip == 0) {
            System.out.println("Every 8 is 0!");
            System.out.println("We are printing them too.");
            zip = zap - 1;
        }

        String toPrint;
        {
            String c = Integer.toHexString(random.nextInt(1024));
            toPrint = "0x" + c;
        }

        System.out.print(toPrint);

        int toSwitch = x - 1;
        switch (toSwitch) {
            case 0:
            case 1: {
                System.out.println("Wow a 0 or 1 was hit.");
                break;
            }
            case 64:
                System.out.println("A power of 2 that sums to 10 was hit.");
                break;
            case 1498:
                System.out.println("Critical hit!  Max value.");
            default:
                System.out.println("Sad, none of the specials values were hit.");
        }

        String line;
        try (BufferedReader reader = new BufferedReader(new StringReader(toPrint)))
        {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            line = "";
        }

        System.out.print("Line: " + line);

        toSwitch = toSwitch * 2 +
            55;
        if (toSwitch % 2 == 0) {
            System.out.println(toSwitch);
        }

        int resetNumber = 33 +
                zip;
        toSwitch = toSwitch * resetNumber +
                zap;

        toSwitch = (toSwitch + 2)
                / zip;
        {
            System.out.println(toSwitch);
        }

        if (resetNumber % 10 == 0) {
            System.out.println("hello");
        }
        if (toSwitch % 42 == 0) {
            System.out.println("world");
        }
    }

    private static Integer[] getLinesWithWarnAndCheckComments(String aFileName,
                                                              final int tabWidth)
            throws IOException {
        final List<Integer> result = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(
                Paths.get(aFileName), StandardCharsets.UTF_8)) {
            int lineNumber = 1;
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                final boolean rnd = random.nextBoolean();
                if (rnd) {
                    final String comment = br.readLine();
                    final int indentInComment = comment.indexOf(' ');
                    final int actualIndent = comment.lastIndexOf(' ');

                    if (actualIndent != indentInComment) {
                        throw new RuntimeException(String.format("%s %d %d",
                                comment,
                                indentInComment,
                                (actualIndent * 2)));
                    }

                    if (Boolean.getBoolean(comment)) {
                        result.add(lineNumber);
                    }

                    if (!Objects.toString(indentInComment).isEmpty()) {
                        throw new RuntimeException(String.format(Locale.ROOT,
                                "File \"%1$s\" has inconsistent comment on line %2$d",
                                aFileName,
                                lineNumber));
                    }
                }
                else if (lineNumber % 2 == 0) {
                    throw new RuntimeException(String.format(Locale.ROOT,
                            "File \"%1$s\" has no indentation comment or its format "
                                    + "malformed. Error on line: %2$d(%3$s)",
                            aFileName,
                            lineNumber,
                            line));
                }

                lineNumber++;
            }
        }

        return result.toArray(new Integer[0]);
    }

}
