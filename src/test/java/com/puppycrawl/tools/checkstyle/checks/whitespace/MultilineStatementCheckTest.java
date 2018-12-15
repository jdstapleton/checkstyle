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

import static com.puppycrawl.tools.checkstyle.checks.whitespace.MultilineStatementCheck.MSG_KEY;

import org.junit.Test;

import com.google.common.reflect.Reflection;
import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class MultilineStatementCheckTest
        extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return Reflection.getPackageName(ReturnSeparationCheckTest.class).replace(".", "/")
                + "/multilinestatement";
    }

    @Test
    public void validateAgainstPackageInfo() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(createChecker(checkConfig),
                getPath("package-info.java"),
                expected);
    }

    @Test
    public void validateAroundMultilineDeclarations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "11: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementRightAfterDecl.java"),
                expected);
    }

    @Test
    public void validateAroundMultiLineExpressions() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "11: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementRightAfterExpr.java"),
                expected);
    }

    @Test
    public void validateAroundFieldDeclarations() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "14: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementFieldDeclaration.java"),
                expected);
    }

    @Test
    public void validateInnerBlocks() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "10: " + getCheckMessage(MSG_KEY),
            "13: " + getCheckMessage(MSG_KEY),
            "21: " + getCheckMessage(MSG_KEY),
            "25: " + getCheckMessage(MSG_KEY),
            "36: " + getCheckMessage(MSG_KEY),
            "45: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementInnerBlockSpacing.java"),
                expected);
    }

    @Test
    public void validateAroundConditionals() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            // if (random.nextInt() % 3 == 0) {
            "10: " + getCheckMessage(MSG_KEY),
            // if (y % 3 == 0) {
            "16: " + getCheckMessage(MSG_KEY),
            // if (y % 6 == 0) {
            "23: " + getCheckMessage(MSG_KEY),
            // while (random.nextInt() % 4 == 0) {
            "34: " + getCheckMessage(MSG_KEY),
            // for (int z = y; z < x; z++) {
            "40: " + getCheckMessage(MSG_KEY),
            // while (random.nextInt() % 8 == 0) {
            "49: " + getCheckMessage(MSG_KEY),
            // switch (x) {
            "55: " + getCheckMessage(MSG_KEY),
            // { with: "No one should do this."
            "65: " + getCheckMessage(MSG_KEY),
            // if (random.nextInt() % 3 == 0) {
            "86: " + getCheckMessage(MSG_KEY),
            // while (random.nextInt() % 4 == 0) {
            "91: " + getCheckMessage(MSG_KEY),
            // for (int z = y; z < x; z++) {
            "97: " + getCheckMessage(MSG_KEY),
            // while (random.nextInt() % 8 == 0) {
            "106: " + getCheckMessage(MSG_KEY),
            // switch (x) {
            "112: " + getCheckMessage(MSG_KEY),
            // case 64: {
            "118: " + getCheckMessage(MSG_KEY),
            // { with: "No one should do this also."
            "121: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementConditionals.java"),
                expected);
    }

    @Test
    public void validateAroundTry() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "18: " + getCheckMessage(MSG_KEY),
            "28: " + getCheckMessage(MSG_KEY),
            "38: " + getCheckMessage(MSG_KEY),
            "56: " + getCheckMessage(MSG_KEY),
            "66: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementTrys.java"),
                expected);
    }

    @Test
    public void validateAgainstLambdas() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "9: " + getCheckMessage(MSG_KEY),
            "14: " + getCheckMessage(MSG_KEY),
            "21: " + getCheckMessage(MSG_KEY),
            "48: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementLambdas.java"),
                expected);
    }

    @Test
    public void validateAgainstAnnotationDefinition() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);

        final String[] expected = {
            "17: " + getCheckMessage(MSG_KEY),
            "19: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementAnnotation.java"),
                expected);
    }

    @Test
    public void validateAllowVariableDefsPriorToBlocks() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);
        checkConfig.addAttribute("allowVarDefBeforeBlock", "true");

        final String[] expected = {
            "93: " + getCheckMessage(MSG_KEY),
            "95: " + getCheckMessage(MSG_KEY),
            "99: " + getCheckMessage(MSG_KEY),
            "101: " + getCheckMessage(MSG_KEY),
            "104: " + getCheckMessage(MSG_KEY),
            "106: " + getCheckMessage(MSG_KEY),
            "110: " + getCheckMessage(MSG_KEY),
            "113: " + getCheckMessage(MSG_KEY),
        };

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementAllowVarDefs.java"),
                expected);
    }

    @Test
    public void validStyleShouldWork() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(MultilineStatementCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(createChecker(checkConfig),
                getPath("InputMultilineStatementValidStyle.java"),
                expected);
    }
}
