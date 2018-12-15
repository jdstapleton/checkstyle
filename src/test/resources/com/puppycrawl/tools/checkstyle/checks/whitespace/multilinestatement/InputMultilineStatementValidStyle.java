package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.annotation.Resources;

import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.internal.utils.CheckUtil;

public class InputMultilineStatementValidStyle {
    private final String SOME_LONG_STRING = "Some strings are long enough to" +
            "make new liens in the middle of a string.";

    private static final Random random
            = new Random(0xdeadbeef);

    private final boolean x = Boolean.getBoolean("TEST_BOOLEAN");

    private static final String XML_NAME = "/google_checks.xml";

    private static final Object LOCK = new Object();

    @InputMultilineStatementAnnotation(
            names = {
                    "One",
                    "Two"
            },
            description = {
                    "description1",
                    "description2"
            }
    )
    private static final Configuration CONFIGURATION;

    private static final Set<Class<?>> CHECKSTYLE_MODULES;

    private final Consumer<String> testConsumer1 = (str) -> {
        String str2 = " " + str;
        System.out.println(SOME_LONG_STRING + str2);
    };

    static {
        try {
            CONFIGURATION = ConfigurationLoader.loadConfiguration(XML_NAME,
                    new PropertiesExpander(System.getProperties()));
        }
        catch (CheckstyleException ex) {
            throw new IllegalStateException(ex);
        }

        try {
            CHECKSTYLE_MODULES = CheckUtil.getCheckstyleModules();
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public InputMultilineStatementValidStyle() {
        // this is OK
        if (x) {
            System.out.println("X is true");
        } else if (random.nextBoolean()) {
            System.out.println("X is false and randomly true.");
        } else {
            System.out.println("X is false");
        }

        /*
          This is allowed
          And more allowance
         */
        int someRandomNumber = x == true ?
                random.nextInt(100) :
                random.nextInt(500);

        boolean y = !x;
        y = !y || random.nextBoolean();

        while (y) {
            System.out.println("The random number is: " + someRandomNumber);
            y = random.nextBoolean();

            if (!y) {
                System.out.println("Man Y is still false");
            }
        }

        Stream.of(random.nextInt()).filter((num) -> {
            return num % 2 == 0;
        }).findFirst().orElse(0);

        // This is OK
        {
            System.out.println("Some other block");
        }

        System.out.println("The constructor is completed.");
    }

    public boolean shouldBeValid() {
        return x
                || Boolean.getBoolean("SOME_REALLY_LONG_NAME_THAT_THAT_WRAPS");
    }

    public void alsoAValidCheck() {
        final String somethingHere = "Hello";
        final String theWorld = "world";

        System.out.println(String.join(",",
                somethingHere,
                theWorld, new String(new char[] {'a', 'b'})));
    }

    public void shouldAlsoBeValid() {
        if ("Hello World".length() > 5 ||
                "Good bye Planet".length() > 4) {
            if ("Universe".length() < 10) {
                System.out.println("Something within a block");
            }
        }
    }

    public static void lambdaWithMultiLineStatements() {
        int value = IntStream.of(1, 2, 3)
                .reduce(0, (a, b) -> {
                    int c = a * a * 2 +
                            b * b * 2;

                    int d = c * c * 2;

                    return d * 5;
                });

        System.out.println("The value is " + value);
    }

    public static void forLoops() {
        for (int x = 2,
             y = 6;
            x < y;
            x += 2, y += 1)
            System.out.println();

        for (int x = 2,
             y = 6;
             x < y;
             x += 2, y += 1) {
            System.out.println();
        }

        for (String item : Collections.singletonList(
                new String(new char[] { 'a', 'b' }))) {
            System.out.println(item);
        }
    }

    public static void testSynchronized() {
        synchronized (LOCK) {
            final long somethingLong =
                    (random.nextLong() * random.nextLong()) +
                            (random.nextLong() % 64) -
                            2;

            System.out.println(somethingLong);
        }
    }
}
