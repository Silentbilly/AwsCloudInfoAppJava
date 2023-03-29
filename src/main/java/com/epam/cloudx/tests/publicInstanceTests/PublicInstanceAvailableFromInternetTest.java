package com.epam.cloudx.tests.publicInstanceTests;

import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.tests.PublicInstanceTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PublicInstanceAvailableFromInternetTest extends BaseTest {
    @Test
    @DisplayName("Public instance is running")
    @Tag("public")
    public void isInstanceRunning() {
        final String expectedState = "running";
        final String actualState = AwsUtils.getInstanceStateByName(publicInstanceName, ec2);

        Assertions.assertEquals(actualState, expectedState,
                String.format("Actual instance state is %s.", actualState));
    }
}
