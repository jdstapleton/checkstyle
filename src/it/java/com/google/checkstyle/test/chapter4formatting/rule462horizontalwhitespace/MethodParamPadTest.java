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

package com.google.checkstyle.test.chapter4formatting.rule462horizontalwhitespace;

import org.junit.Test;

import com.google.checkstyle.test.base.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.checks.whitespace.MethodParamPadCheck;

public class MethodParamPadTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/google/checkstyle/test/chapter4formatting/rule462horizontalwhitespace";
    }

    @Test
    public void testOperatorWrap() throws Exception {
        final Class<MethodParamPadCheck> clazz = MethodParamPadCheck.class;
        final String messageKeyPreceded = "ws.preceded";

        final String[] expected = {
            "11:32: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "13:15: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "20:24: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "29:39: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "35:16: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "41:21: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "47:18: " + getCheckMessage(clazz, messageKeyPreceded, "("),
            "52:36: " + getCheckMessage(clazz, messageKeyPreceded, "("),
        };

        final Configuration checkConfig = getModuleConfig("MethodParamPad");
        final String filePath = getPath("InputMethodParamPad.java");

        final Integer[] warnList = getLinesWithWarn(filePath);
        verify(checkConfig, filePath, expected, warnList);
    }

}
