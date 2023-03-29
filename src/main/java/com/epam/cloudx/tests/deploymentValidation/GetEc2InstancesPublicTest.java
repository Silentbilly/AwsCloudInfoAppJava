package com.epam.cloudx.tests.deploymentValidation;

import com.amazonaws.services.ec2.model.Instance;
import com.epam.cloudx.objects.AppInfo;
import com.epam.cloudx.tests.PublicInstanceTest;
import com.epam.cloudx.utils.AwsUtils;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GetEc2InstancesPublicTest extends PublicInstanceTest {

  @Test
  @DisplayName("Public instance is running")
  public void isInstanceRunning() {
    final String expectedState = "running";
    final String actualState = AwsUtils.getInstanceStateByName(publicInstanceName, ec2);

    Assertions.assertEquals(actualState, expectedState,
        String.format("Actual instance state is %s.", actualState));
  }

  @Test
  @DisplayName("Public instance is available from internet")
  public void publicInstanceIsAvailableFromInternet() {
    boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(publicInstanceName, ec2).isEmpty();
    Assertions.assertTrue(isPublicIpExisting, "Public IP is empty");
  }

  @Test
  @DisplayName("Public instance application functional validation")
  public void getApiForPublicInstance() {
    var file = new File("src/main/resources/data/json/appInfoPublic.json");
    var response = HttpUtils.getPublicAppInfo(publicInstanceName, ec2);
    var actualResponse = JsonUtils.readJsonAsObject(response, AppInfo.class);
    var expectedResponse = JsonUtils.readJsonFileAsObject(file, AppInfo.class);
    Assertions.assertEquals(actualResponse, expectedResponse);
  }
/*
  @Test
  public void isPublicInstanceAccessibleBySsh() {
    Instance instance = AwsUtils.getInstanceByName(publicInstanceName, ec2);
    Assertions.assertTrue(AwsUtils.isSshAccessible(instance, PERMISSIONS_FILE_PATH));
  }*/
}