package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetEc2InstancesTest extends BaseTest {
    private static final String PUBLIC_INSTANCE_NAME = "cloudxinfo/PublicInstance/Instance";

    @Test
    public void isInstanceRunning() {
        final String expectedState = "running";
        final String actualState = AwsUtils.getInstanceStateByName(PUBLIC_INSTANCE_NAME, ec2);

        Assertions.assertEquals(actualState, expectedState,
            String.format("Actual instance state is %s.", actualState));
    }

    @Test
    public void isInstanceAvailableFromInternet() {
        boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(PUBLIC_INSTANCE_NAME, ec2).isEmpty();
        Assertions.assertTrue(isPublicIpExisting, "Public IP is empty");
    }
}