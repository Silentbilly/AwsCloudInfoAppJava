package com.epam.cloudx.tests.privateInstanceTests;

import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.tests.PrivateInstanceTest;
import com.epam.cloudx.tests.PublicInstanceTest;
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
        final String actualState = AwsUtils.getInstanceStateByName(privateInstanceName, ec2);

        Assertions.assertEquals(actualState, expectedState, String.format("Actual instance state is %s.", actualState));
    }
}
