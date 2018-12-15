package com.puppycrawl.tools.checkstyle.checks.whitespace;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * This Check checks to see if a multi-line statement is properly padded by an empty lines on either
 * side of it.
 *
 * valid:
 * <pre>
 *   public String foo() {
 *      System.o
 *   }
 *
 *   public String bar() {
 *      String f = foo();
 *
 *      return f;
 *   }
 *
 *   public String baz() {
 *      String f = bar();
 *
 *      if (f.isEmpty()) {
 *          return "empty";
 *      }
 *
 *      return f;
 *   }
 * </pre>
 *
 * invalid:
 * <pre>
 *   public String bar() {
 *      String f = foo();
 *      return f;
 *   }
 *
 *   public String baz() {
 *      String f = bar();
 *
 *      if (f.isEmpty()) {
 *          f = "empty";
 *      }
 *      return f;
 *   }
 * </pre>
 */
@StatelessCheck
public class ReturnSeparationCheck extends AbstractCheck {
    public static final String MSG_EMPTY_LINE_BEFORE_RETURN = "noEmptyLineBeforeReturn";

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.LITERAL_RETURN };
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST token) {
        if (!isFirstInBlock(token) && !hasEmptyLineBefore(token)) {
            log(token, MSG_EMPTY_LINE_BEFORE_RETURN);
        }
    }

    /**
     * Checks if a token has a empty line before.
     * @param token token.
     * @return true, if token have empty line before.
     */
    private boolean hasEmptyLineBefore(DetailAST token) {
        // In Java, the line number is always > 1 due to 'isFirstInBlock' check will prevent this
        // block from being executed.
        final int lineNo = token.getLineNo();

        // [lineNo - 2] is the number of the previous line as the numbering starts from zero.
        final String lineBefore = getLines()[lineNo - 2];

        return CommonUtil.isBlank(lineBefore);
    }

    /**
     * Checks if a token is the first within its block
     * @param token token.
     * @return true, if token is the first within its block
     */
    private static boolean isFirstInBlock(DetailAST token) {
        DetailAST current;
        current = token.getPreviousSibling();

        while (current != null && current.getLineNo() == token.getLineNo()) {
            current = current.getPreviousSibling();
        }

        return current == null;
    }
}
