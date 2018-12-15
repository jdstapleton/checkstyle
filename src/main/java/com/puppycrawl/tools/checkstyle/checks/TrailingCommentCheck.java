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

package com.puppycrawl.tools.checkstyle.checks;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.StatelessCheck;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * <p>
 * The check to ensure that comments are the only thing on a line.
 * For the case of {@code //} comments that means that the only thing that should
 * precede it is whitespace.
 * It doesn't check comments if they do not end line, i.e. it accept
 * the following:
 * </p>
 * <pre><code>Thread.sleep( 10 /*some comment here&#42;/ );</code></pre>
 * <p>Format property is intended to deal with the <code>} // while</code> example.
 * </p>
 *
 * <p>Rationale: Steve McConnell in &quot;Code Complete&quot; suggests that endline
 * comments are a bad practice. An end line comment would
 * be one that is on the same line as actual code. For example:
 * <pre>
 *  a = b + c;      // Some insightful comment
 *  d = e / f;        // Another comment for this line
 * </pre>
 * Quoting &quot;Code Complete&quot; for the justification:
 * <ul>
 * <li>
 * &quot;The comments have to be aligned so that they do not
 * interfere with the visual structure of the code. If you don't
 * align them neatly, they'll make your listing look like it's been
 * through a washing machine.&quot;
 * </li>
 * <li>
 * &quot;Endline comments tend to be hard to format...It takes time
 * to align them. Such time is not spent learning more about
 * the code; it's dedicated solely to the tedious task of
 * pressing the spacebar or tab key.&quot;
 * </li>
 * <li>
 * &quot;Endline comments are also hard to maintain. If the code on
 * any line containing an endline comment grows, it bumps the
 * comment farther out, and all the other endline comments will
 * have to bumped out to match. Styles that are hard to
 * maintain aren't maintained....&quot;
 * </li>
 * <li>
 * &quot;Endline comments also tend to be cryptic. The right side of
 * the line doesn't offer much room and the desire to keep the
 * comment on one line means the comment must be short.
 * Work then goes into making the line as short as possible
 * instead of as clear as possible. The comment usually ends
 * up as cryptic as possible....&quot;
 * </li>
 * <li>
 * &quot;A systemic problem with endline comments is that it's hard
 * to write a meaningful comment for one line of code. Most
 * endline comments just repeat the line of code, which hurts
 * more than it helps.&quot;
 * </li>
 * </ul>
 * His comments on being hard to maintain when the size of
 * the line changes are even more important in the age of
 * automated refactorings.
 *
 * <p>To configure the check so it enforces only comment on a line:
 * <pre>
 * &lt;module name=&quot;TrailingComment&quot;&gt;
 *    &lt;property name=&quot;format&quot; value=&quot;^\\s*$&quot;/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @noinspection HtmlTagCanBeJavadocTag
 */
@StatelessCheck
public class TrailingCommentCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "trailing.comments";

    /** Pattern for legal trailing comment. */
    private Pattern legalComment;

    /** The regexp to match against. */
    private Pattern format = Pattern.compile("^[\\s});]*$");

    /**
     * Sets patter for legal trailing comments.
     * @param legalComment pattern to set.
     */
    public void setLegalComment(final Pattern legalComment) {
        this.legalComment = legalComment;
    }

    /**
     * Set the format for the specified regular expression.
     * @param pattern a pattern
     */
    public final void setFormat(Pattern pattern) {
        format = pattern;
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return CommonUtil.EMPTY_INT_ARRAY;
    }

    @Override
    public void visitToken(DetailAST ast) {
        throw new IllegalStateException("visitToken() shouldn't be called.");
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        final Map<Integer, TextBlock> cppComments = getFileContents()
                .getSingleLineComments();

        final Map<Integer, List<TextBlock>> cComments = getFileContents()
                .getBlockComments();

        final Set<Integer> lines = new HashSet<>();
        lines.addAll(cppComments.keySet());
        lines.addAll(cComments.keySet());

        for (Integer lineNo : lines) {
            final String line = getLines()[lineNo - 1];
            final String lineBefore;
            final TextBlock comment;

            if (cppComments.containsKey(lineNo)) {
                comment = cppComments.get(lineNo);
                lineBefore = line.substring(0, comment.getStartColNo());
            }
            else {
                final List<TextBlock> commentList = cComments.get(lineNo);
                comment = commentList.get(commentList.size() - 1);
                lineBefore = line.substring(0, comment.getStartColNo());

                // do not check comment which doesn't end line
                if (comment.getText().length == 1
                        && !CommonUtil.isBlank(line
                            .substring(comment.getEndColNo() + 1))) {
                    continue;
                }
            }
            if (!format.matcher(lineBefore).find()
                && !isLegalComment(comment)) {
                log(lineNo, MSG_KEY);
            }
        }
    }

    /**
     * Checks if given comment is legal (single-line and matches to the
     * pattern).
     * @param comment comment to check.
     * @return true if the comment if legal.
     */
    private boolean isLegalComment(final TextBlock comment) {
        final boolean legal;

        // multi-line comment can not be legal
        if (legalComment == null || comment.getStartLineNo() != comment.getEndLineNo()) {
            legal = false;
        }
        else {
            String commentText = comment.getText()[0];
            // remove chars which start comment
            commentText = commentText.substring(2);

            // if this is a C-style comment we need to remove its end
            if (commentText.endsWith("*/")) {
                commentText = commentText.substring(0, commentText.length() - 2);
            }

            commentText = commentText.trim();
            legal = legalComment.matcher(commentText).find();
        }

        return legal;
    }

}
