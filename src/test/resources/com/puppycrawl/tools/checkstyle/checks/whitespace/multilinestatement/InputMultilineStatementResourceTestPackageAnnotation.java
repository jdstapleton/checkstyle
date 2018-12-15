package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PACKAGE)
public @interface InputMultilineStatementResourceTestPackageAnnotation {
    String value() default "";
    String veryLongLongLongExtraExtendedNameValueHere() default "";
}
