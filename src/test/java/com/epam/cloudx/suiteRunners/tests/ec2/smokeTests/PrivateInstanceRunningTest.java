package com.epam.cloudx.suiteRunners.tests.ec2.smokeTests;

import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PrivateInstanceRunningTest extends CloudxInfoBaseTest {
    @Test
    @DisplayName("Private instance is running")
    @Tag("private")
    @Tag("smoke")
    public void isInstanceRunning() {
        final String expectedState = "running";
        final String actualState = AwsUtils.getInstanceStateByName(ec2, privateInstanceName);

        Assertions.assertEquals(actualState, expectedState, String.format("Actual instance state is %s.", actualState));
    }
}
