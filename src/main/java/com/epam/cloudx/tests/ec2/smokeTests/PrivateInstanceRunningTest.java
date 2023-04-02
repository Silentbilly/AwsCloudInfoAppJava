package com.epam.cloudx.tests.ec2.smokeTests;

import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PrivateInstanceRunningTest extends BaseTest {
    @Test
    @DisplayName("Private instance is running")
    @Tag("private")
    public void isInstanceRunning() {
        final String expectedState = "running";
        final String actualState = AwsUtils.getInstanceStateByName(ec2, privateInstanceName);

        Assertions.assertEquals(actualState, expectedState, String.format("Actual instance state is %s.", actualState));
    }
}
