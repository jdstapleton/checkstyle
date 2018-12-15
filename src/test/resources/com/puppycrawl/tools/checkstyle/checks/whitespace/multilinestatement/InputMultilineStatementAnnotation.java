package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER
})
public @interface InputMultilineStatementAnnotation {
    String[] names();

    String[] description() default
            {};
    boolean interactive() default
            false;

    boolean validMultilineStatement()
            default false;
}
