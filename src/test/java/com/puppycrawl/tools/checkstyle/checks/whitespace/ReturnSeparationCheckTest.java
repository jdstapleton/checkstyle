package com.puppycrawl.tools.checkstyle.checks.whitespace;

import static com.puppycrawl.tools.checkstyle.checks.whitespace.ReturnSeparationCheck.MSG_EMPTY_LINE_BEFORE_RETURN;

import org.junit.Test;

import com.google.common.reflect.Reflection;
import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class ReturnSeparationCheckTest
        extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return Reflection.getPackageName(ReturnSeparationCheckTest.class).replace(".", "/")
                + "/returnseparation";
    }

    @Test
    public void disallowReturnRightAfterAnotherStatement() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ReturnSeparationCheck.class);

        final String[] expected = {
            "14:9: " + getCheckMessage(MSG_EMPTY_LINE_BEFORE_RETURN),
        };

        verify(
                createChecker(checkConfig),
                getPath("InputReturnSeparationRightAfterStatement.java"),
                expected);
    }

    @Test
    public void disallowReturnRightAfterBlock() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ReturnSeparationCheck.class);

        final String[] expected = {
            "16:9: " + getCheckMessage(MSG_EMPTY_LINE_BEFORE_RETURN),
        };

        verify(
                createChecker(checkConfig),
                getPath("InputReturnSeparationRightAfterBlock.java"),
                expected);
    }

    @Test
    public void verifyValidReturnStyle() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ReturnSeparationCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(
            createChecker(checkConfig),
            getPath("InputReturnSeparationValidStyle.java"),
            expected);
    }

}
