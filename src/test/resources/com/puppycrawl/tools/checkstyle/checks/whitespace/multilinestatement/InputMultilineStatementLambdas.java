package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public class InputMultilineStatementLambdas {
    private String HELLO = "Hello";
    private Consumer<String> testConsumer1 = (str) -> {
        String str2 = " " + str;
        System.out.println(HELLO + str2);
    };

    private Consumer<String> testConsumer2 = (str) -> {
        String str2 = " " + str;
        System.out.println(HELLO + str2);
    };
    private static final String AFTER = "After";

    public static void streamTestFailure() {
        int value = IntStream.of(1, 2, 3)
                .reduce(0, (a, b) -> {
                    int c = a * a +
                            b * b;

                    int d = c * c;

                    return d + AFTER.length();
                });
        System.out.println("The value is " + value);
    }

    public static void streamTestPasses() {
        int value = IntStream.of(1, 2, 3)
                .reduce(0, (a, b) -> {
                    int c = a * a +
                            b * b;

                    int d = c * c;

                    return d;
                });

        System.out.println("The value is " + value);
    }

    public static void localLambdaFail() {
        Function<Integer, Integer> someFunc = (x) ->
                x * 0xdeadbeef + 1 +
                        "world".length();
        System.out.println(someFunc.apply(123));
    }

    public static void localLambdaPass() {
        Function<Integer, Integer> someFunc = (x) ->
                x * 0xdeadbeef + 1 +
                        "world".length();

        System.out.println(someFunc.apply(123));
    }
}
