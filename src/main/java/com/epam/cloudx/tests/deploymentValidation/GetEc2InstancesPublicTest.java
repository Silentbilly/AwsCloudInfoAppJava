package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetEc2InstancesPublicTest extends PublicInstanceTest {
    @Test
    public void isInstanceRunning() {
        final String expectedState = "running";
        final String actualState = AwsUtils.getInstanceStateByName(publicInstanceName, ec2);

        Assertions.assertEquals(actualState, expectedState,
            String.format("Actual instance state is %s.", actualState));
    }

    @Test
    public void isInstanceAvailableFromInternet() {
        boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(publicInstanceName, ec2).isEmpty();
        Assertions.assertTrue(isPublicIpExisting, "Public IP is empty");
    }
}