package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.objects.AppInfo;
import com.epam.cloudx.tests.PublicInstanceTest;
import com.epam.cloudx.utils.AwsUtils;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import java.io.File;
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
    public void publicInstanceIsAvailableFromInternet() {
        boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(publicInstanceName, ec2).isEmpty();
        Assertions.assertTrue(isPublicIpExisting, "Public IP is empty");
    }

    @Test
    public void getApiForPublicInstance() {
        var file = new File("src/main/resources/data/appInfoPublic.json");
        var response = HttpUtils.getPublicAppInfo(publicInstanceName, ec2);
        var actualResponse = JsonUtils.readJsonAsObject(response, AppInfo.class);
        var expectedResponse = JsonUtils.readJsonFileAsObject(file, AppInfo.class);
        Assertions.assertEquals(actualResponse, expectedResponse);
    }
}