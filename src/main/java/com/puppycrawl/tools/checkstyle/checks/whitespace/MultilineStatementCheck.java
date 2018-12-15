////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks.whitespace;

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This check checks to see if a return statement is properly separated by an empty line.
 * valid:
 * <pre>
 *  {@literal @}SomeAnnotation(
 *     names = {
 *         "Something",
 *         "Else"
 *   })
 *   private String something;
 *
 *   public String firstLine() {
 *      String f = "Hello " +
 *          "World";
 *
 *      String y = "!";
 *      return f + y;
 *   }
 *
 *   public String blockIsPadded() {
 *      String f = firstLine();
 *
 *      if (f.isEmpty()) {
 *          f = "I am " +
 *              "also ";
 *      }
 *
 *      String zzz = " Sleepy.";
 *      return f + zzz;
 *   }
 *
 *   public void inTheMiddle() {
 *      int age = 10;
 *
 *      String formatString = "%s is " +
 *          " %d units old.";
 *
 *      System.out.println(String.format(formatString, "Person", age));
 *   }
 *
 *   public void onlyLine() {
 *      System.out.println("Hello " +
 *          "world");
 *   }
 *
 *   public void lastLine() {
 *      String world = "world";
 *
 *      System.out.println("Hello " +
 *          world);
 *   }
 *
 *   public void innerBlock() {
 *       int number = 1;
 *
 *       {
 *          {
 *              System.out.println("Favorite number: " + number);
 *          }
 *       }
 *   }
 * </pre>
 *
 * <p>invalid:
 * <pre>
 *   public void afterMultilineRequiresNewLine() {
 *      String f = "Hello " +
 *          "world";
 *      System.out.println(f);
 *   }
 *
 *   public String beforeAndAfterBlockRequiresEmptyLine() {
 *      String f = "xyz";
 *      if (f.isEmpty()) {
 *          f = "empty";
 *      }
 *      System.out.println(f);
 *   }
 * </pre>
 */
@StatelessCheck
public class MultilineStatementCheck extends AbstractCheck {
    /**
     * The localization key for the error message.
     */
    public static final String MSG_KEY = "noPaddingAroundMultiLineStatement";

    /**
     * Tokens to consider as start of a block when encountering the right curly.
     */
    private static final int[] BLOCK_TOKENS = sorted(
        TokenTypes.SLIST,
        TokenTypes.LITERAL_WHILE,
        TokenTypes.LITERAL_FOR,
        TokenTypes.LITERAL_IF,
        TokenTypes.LITERAL_SWITCH,
        TokenTypes.LITERAL_TRY,
        TokenTypes.INSTANCE_INIT);

    /**
     * Right curly to ignore if they are part of these parts of blocks as either other checks
     * has already covered these conditions, or they are part of a larger block.
     */
    private static final int[] NON_PROCESSABLE_RCURLY = sorted(
        // covered by a different check
        TokenTypes.CTOR_DEF,
        // covered by the semi colon in the statement ending the lambda
        TokenTypes.LAMBDA,
        // covered by a different check
        TokenTypes.METHOD_DEF,
        // covered by the entry being annotated
        TokenTypes.ANNOTATION,
        // covered by the entry being annotated
        TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR,
        // else branches are always processed via the 'if' start of block
        TokenTypes.LITERAL_ELSE,
        // catch statements are always processed via the starting try
        TokenTypes.LITERAL_CATCH,
        // finally statements are always processed via the starting try
        TokenTypes.LITERAL_FINALLY);

    /**
     * The different types of for loops parts, this way we can figure out where the curly belongs.
     */
    private static final int[] FOR_LOOP_DEF = sorted(
        TokenTypes.FOR_INIT,
        TokenTypes.FOR_CONDITION,
        TokenTypes.FOR_ITERATOR);

    /**
     * The checker will allow declarations immediately before blocks if set to true.
     */
    private boolean allowVarDefBeforeBlock;

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return sorted(TokenTypes.RCURLY, TokenTypes.SEMI);
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST token) {
        final int astType = token.getType();

        switch (astType) {
            case TokenTypes.SEMI:
                if (!isResource(token)
                        && !isForDefinition(token)
                        && isMultilineStatement(token)) {
                    processStatement(token);
                }

                break;
            case TokenTypes.RCURLY:
            default:
                // lambdas with RCURLY are protected by the ';'
                if (shouldProcessRcurly(token)) {
                    processBlock(token);
                }
        }
    }

    /**
     * Property to allow variable definitions directly before blocks.
     *
     * @param allowVarDefBeforeBlock true if to allow, false otherwise.
     */
    public final void setAllowVarDefBeforeBlock(boolean allowVarDefBeforeBlock) {
        this.allowVarDefBeforeBlock = allowVarDefBeforeBlock;
    }

    /**
     * Checks blocks for proper spacing on either side of it.
     *
     * @param rcurly the token pointing to the end of the block to check
     */
    private void processBlock(DetailAST rcurly) {
        final DetailAST startOfBlock = getStartOfBlock(rcurly);
        final DetailAST realRcurly = findRealEndOfBlock(startOfBlock, rcurly);

        if (missingRequiredEmptyLineBeforeBlock(startOfBlock)
                || missingRequiredEmptyLineAfterBlock(startOfBlock, realRcurly)) {
            log(startOfBlock.getLineNo(), MSG_KEY, startOfBlock.getText());
        }
    }

    /**
     * The point of this function is to get the logical end of the logical block.
     * <p>
     * Would get the ending '}' for the last else block of the if, and the last ending '}'
     * catch/finally block of a try.
     * </p>
     *
     * @param startOfBlock the startOfTheBlock (IF/TRY/OTHER)
     * @param rcurly the original rcurly for the first block of the IF/TRY/OTHER
     * @return the real rcurly for the IF/TRY, if not a IF/TRY return rcurly
     */
    private DetailAST findRealEndOfBlock(DetailAST startOfBlock, DetailAST rcurly) {
        DetailAST result = rcurly;
        if (startOfBlock.getType() == TokenTypes.LITERAL_IF
                || startOfBlock.getType() == TokenTypes.LITERAL_TRY) {
            result = lastDescendent(startOfBlock);
        }

        return result;
    }

    /**
     * Process a non-block statement.
     *
     * @param semiColonToken end token of the statement to check
     */
    private void processStatement(DetailAST semiColonToken) {
        if (missingRequiredEmptyLineAfterStatement(semiColonToken)
                || missingRequiredEmptyLineBeforeStatement(semiColonToken)) {
            final DetailAST prevSibling = semiColonToken.getPreviousSibling();
            log(prevSibling.getLineNo(), MSG_KEY, prevSibling.getText());
        }
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken the end token to check
     * @return an empty line is required after the statement and and not found
     */
    private boolean missingRequiredEmptyLineAfterStatement(DetailAST semiColonToken) {
        return !hasAllowedNextSibling(semiColonToken)
                && !hasEmptyLineAfterSemi(semiColonToken);
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken the end token to check
     * @return an empty line is required before the statement and and not found
     */
    private boolean missingRequiredEmptyLineBeforeStatement(DetailAST semiColonToken) {
        return !firstStatementOfBlock(semiColonToken)
                && !hasEmptyLineBeforeStatement(semiColonToken);
    }

    /**
     * See return documentation.
     *
     * @param startOfBlock the token to check
     * @return an empty line is required before the block and and not found
     */
    private boolean missingRequiredEmptyLineBeforeBlock(DetailAST startOfBlock) {
        return !hasAllowedPreviousSibling(startOfBlock) && !hasEmptyLineBeforeBlock(startOfBlock);
    }

    /**
     * See return documentation.
     *
     * @param startOfBlock the token to check
     * @param rcurly the end token to check
     * @return an empty line is required after the block and and not found
     */
    private boolean missingRequiredEmptyLineAfterBlock(DetailAST startOfBlock, DetailAST rcurly) {
        return startOfBlock.getNextSibling() != null
                && !hasAllowedSiblingAfterBlock(startOfBlock, rcurly)
                && !hasEmptyLineAfterBlock(rcurly);
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken the end token to check
     * @return allowed if this statement is a variable def and the following is a block, if option
     *         is set.
     */
    private boolean hasAllowedNextSibling(DetailAST semiColonToken) {
        final DetailAST prev = semiColonToken.getPreviousSibling();

        return lastStatementOfBlock(semiColonToken)
                || (allowVarDefBeforeBlock
                && prev.getType() == TokenTypes.VARIABLE_DEF
                && isBlock(semiColonToken.getNextSibling()));
    }

    /**
     * See return documentation.
     *
     * @param startOfBlock the token to check
     * @return allow if previous statement is a variable def and the option is set.
     */
    private boolean hasAllowedPreviousSibling(DetailAST startOfBlock) {
        final DetailAST prevSibling = startOfBlock.getPreviousSibling();

        return prevSibling == null
                || startOfBlock.getType() == TokenTypes.OBJBLOCK
                || (allowVarDefBeforeBlock
                && prevSibling.getType() == TokenTypes.SEMI
                && prevSibling.getPreviousSibling().getType() == TokenTypes.VARIABLE_DEF);
    }

    /**
     * Checks if a semi-colon token have empty line after.
     *
     * @param semiColonToken token.
     * @return true if semi-colon token has an empty line after.
     */
    private boolean hasEmptyLineAfterSemi(DetailAST semiColonToken) {
        DetailAST nextToken = semiColonToken.getNextSibling();

        if (nextToken == null) {
            nextToken = semiColonToken.getParent().getNextSibling();
        }

        // Start of the next token
        final int nextBegin = nextToken.getLineNo();
        // End of current token.
        final int currentEnd = semiColonToken.getLineNo();

        return hasEmptyLine(currentEnd + 1, nextBegin - 1);
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken token
     * @return true if the previous line is considered blank
     */
    private boolean hasEmptyLineBeforeStatement(DetailAST semiColonToken) {
        final int lastDescendentLineNo =
                lastDescendent(previousStatement(semiColonToken)).getLineNo();

        return hasEmptyLine(lastDescendentLineNo + 1, lastDescendentLineNo + 1);
    }

    /**
     * See return documentation.
     *
     * @param rcurlyToken token
     * @return true if the following line is considered blank
     */
    private boolean hasEmptyLineAfterBlock(DetailAST rcurlyToken) {
        final DetailAST nextToken = siblingStatement(rcurlyToken);

        // Start of the next token
        final int nextBegin = nextToken.getLineNo();

        // End of current token.
        final int currentEnd = rcurlyToken.getLineNo();

        return hasEmptyLine(currentEnd + 1, nextBegin - 1);
    }

    /**
     * See return documentation.
     *
     * @param startOfBlock token
     * @return true if the previous line is considered blank
     */
    private boolean hasEmptyLineBeforeBlock(DetailAST startOfBlock) {
        return hasEmptyLine(
                startOfBlock.getLineNo() - 1, startOfBlock.getLineNo() - 1);
    }

    /**
     * Checks, whether there are empty or comment lines within the specified line range.
     * Line numbering is started from 1 for parameter values
     *
     * @param startLine number of the first line in the range
     * @param endLine   number of the second line in the range
     * @return {@code true} if found any blank line within the range, {@code false}
     *     otherwise
     */
    private boolean hasEmptyLine(int startLine, int endLine) {
        // Initial value is false - blank line not found
        boolean result = false;

        if (startLine <= endLine) {
            final FileContents fileContents = getFileContents();

            for (int line = startLine; line <= endLine; line++) {
                // Check, if the line is blank. Lines are numbered from 0, so subtract 1
                if (fileContents.lineIsBlank(line - 1)
                    || fileContents.lineIsComment(line - 1)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * This method checks to see if the right curly is a curly to be checked.  For instance it
     * should only check an if/else if/else block once not 3 times, and validate that there are
     * spacing around the whole block.
     *
     * @param rcurly token to check if needed to be checked
     * @return true if the checker should validate spacing around the block
     */
    private static boolean shouldProcessRcurly(DetailAST rcurly) {
        boolean result = Arrays.binarySearch(NON_PROCESSABLE_RCURLY,
            rcurly.getParent().getParent().getType()) < 0
            && rcurly.getParent().getType() != TokenTypes.ARRAY_INIT;

        if (result) {
            final DetailAST lcurlyParent = rcurly.getParent().getParent();

            // 'else if (...) {'
            // Don't check these as the original 'if' statement was check.
            if (lcurlyParent.getParent() != null
                    && lcurlyParent.getParent().getType() == TokenTypes.LITERAL_ELSE) {
                result = false;
            }
        }

        return result;
    }

    /**
     * We should allow for end blocks immendiately following another end block.
     *
     * <p>For example
     * <pre>
     *       }
     *     }
     * </pre>
     *
     * <p>is valid.
     *
     * @param startOfBlock the start of the block
     * @param rcurly the end of the block
     * @return true if there exist another end block immediately following this end block.
     */
    private static boolean hasAllowedSiblingAfterBlock(DetailAST startOfBlock, DetailAST rcurly) {
        return startOfBlock.getNextSibling().getType() == TokenTypes.RCURLY;
    }

    /**
     * If the given token is a start of a block.
     *
     * @param token the token to check
     * @return true if the token is a start of a block.
     */
    private static boolean isBlock(DetailAST token) {
        return Arrays.binarySearch(BLOCK_TOKENS, token.getType()) >= 0;
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken to check
     * @return true if this is the last statement of its block
     */
    private static boolean lastStatementOfBlock(DetailAST semiColonToken) {
        DetailAST sibling = semiColonToken.getNextSibling();

        if (sibling == null) {
            sibling = semiColonToken.getParent().getNextSibling();
        }

        return sibling == null || sibling.getType() == TokenTypes.RCURLY;
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken to check
     * @return true if this is the first statement of its block
     */
    private static boolean firstStatementOfBlock(DetailAST semiColonToken) {
        final DetailAST statement = previousStatement(semiColonToken);

        // if its the first statement of its block make it be LCURLY
        int statementType = TokenTypes.LCURLY;
        if (statement != null) {
            statementType = statement.getType();
        }

        return statementType == TokenTypes.LCURLY;
    }

    /**
     * Since '{' may or may not be the actual start of a block.
     *
     * <p>For {@literal try {...}}, the try would be considered the start of the block
     * not the '{'.
     *
     * @param rcurlyToken of the block to check
     * @return the actual start of a block.
     */
    private static DetailAST getStartOfBlock(DetailAST rcurlyToken) {
        // rcurly's parent is the lcurly|slist.
        final DetailAST lcurly = rcurlyToken.getParent();
        final DetailAST result;

        switch (lcurly.getParent().getType()) {
            case TokenTypes.LITERAL_STATIC:
            case TokenTypes.LITERAL_SYNCHRONIZED:
            case TokenTypes.LITERAL_IF:
            case TokenTypes.LITERAL_WHILE:
            case TokenTypes.DO_WHILE:
            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_TRY:
                result = lcurly.getParent();
                break;
            default:
                result = lcurly;
        }

        return result;
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken the end token of the statement to get the previous
     * @return the previous statement of the given statement
     */
    private static DetailAST previousStatement(DetailAST semiColonToken) {
        final DetailAST parentStatement = semiColonToken.getParent();

        final DetailAST previousStatement;
        if (parentStatement.getType() == TokenTypes.SLIST) {
            // first previousSibling is the statement the semi colon is terminating, then its
            // previous sibling is the actual statement previous to the statement the semi colon
            // is ending.
            previousStatement = semiColonToken.getPreviousSibling().getPreviousSibling();
        }
        else {
            // within the object body (i.e. member variables and statics) the semi colon is within
            // the statement its terminating, so we care about the parentStatement's previous
            previousStatement = parentStatement.getPreviousSibling();
        }

        return previousStatement;
    }

    /**
     * Finds the deepest descendent for the last most child.
     *
     * @param token the token tree to fetch the descendent
     * @return the descendent
     */
    private static DetailAST lastDescendent(DetailAST token) {
        DetailAST result = token;

        if (token.getLastChild() != null) {
            result = lastDescendent(token.getLastChild());
        }

        return result;
    }

    /**
     * The conceptual next sibling of the given statement.
     *
     * @param rcurlyToken end of the block to get the next sibling
     * @return the next sibling
     */
    private static DetailAST siblingStatement(DetailAST rcurlyToken) {
        DetailAST parentToken = rcurlyToken.getParent();
        DetailAST nextToken = parentToken.getNextSibling();

        while (nextToken == null) {
            parentToken = parentToken.getParent();
            nextToken = parentToken.getNextSibling();
        }

        return nextToken;
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken to check
     * @return true if this statement is multiple lines long
     */
    private static boolean isMultilineStatement(DetailAST semiColonToken) {
        final DetailAST prevSibling = semiColonToken.getPreviousSibling();

        return prevSibling != null && prevSibling.getLineNo() != semiColonToken.getLineNo();
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken to check
     * @return true if this is an expression for the resource part of a try-with-resource.
     */
    private static boolean isResource(DetailAST semiColonToken) {
        final DetailAST prev = semiColonToken.getPreviousSibling();

        return prev != null && prev.getType() == TokenTypes.RESOURCE;
    }

    /**
     * See return documentation.
     *
     * @param semiColonToken to check
     * @return true if this is part of a for loop definition
     */
    private static boolean isForDefinition(DetailAST semiColonToken) {
        final DetailAST prev = semiColonToken.getPreviousSibling();

        return prev != null
                && Arrays.binarySearch(FOR_LOOP_DEF, prev.getType()) >= 0;
    }

    /**
     * Simple wrapper around Arrays.sort in order to make it easiery to initialize static arrays in
     * sorted order.
     *
     * @param toSort a list of integers to sort
     * @return the array of integers sorted
     */
    private static int[] sorted(int... toSort) {
        Arrays.sort(toSort);

        return toSort;
    }
}
