/*
this is without a whitespace
maybe it will fail?
 */
package com.puppycrawl.tools.checkstyle.checks.whitespace
        .multilinestatement;

public class InputMultilineStatementInnerBlockSpacing {
    public static final void foo(String input) {
        if (true) {
            System.out.println();
        }
        {
            System.out.println("Java allows for inner blocks.");
        }
        System.out.println("This is hit every time.");
    }

    public static final void bar(String input) {
        System.out.println();
        {
            System.out.println("Java allows for inner blocks.");
        }

        {
            System.out.println("Java still allows for inner blocks.");
        }
        System.out.println("This is hit every time.");
    }

    public static void baz(String input) {
        System.out.println();

        {
            System.out.println();
            {
                {
                    System.out.println("Java allows for inner blocks.");
                }
            }
        }

        {
            {
                {
                    System.out.println("More inner blocks.");
                }
                System.out.println();
            }
        }

        {
            {
                {
                    System.out.println("More inner blocks.");
                }

                System.out.println();
            }
        }

        System.out.println("This is hit every time.");
    }
}
