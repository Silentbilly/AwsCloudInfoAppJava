package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.tests.PrivateInstanceTest;
import com.epam.cloudx.utils.AwsUtils;
import com.epam.cloudx.utils.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GetEc2InstancesPrivateTest extends PrivateInstanceTest {

  @Test
  @DisplayName("Private instance is running")
  public void isInstanceRunning() {
    final String expectedState = "running";
    final String actualState = AwsUtils.getInstanceStateByName(privateInstanceName, ec2);

    Assertions.assertEquals(actualState, expectedState, String.format("Actual instance state is %s.", actualState));
  }

  @Test
  @DisplayName("Private instance is not available from internet")
  public void privateInstanceIsNotAvailableFromInternet() {
    Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
        () -> AwsUtils.getPublicIpAddressByName(privateInstanceName, ec2));
  }

  @Test
  @DisplayName("API for private instance is not available from internet")
  public void getApiForPrivateInstance() {
    Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
        () -> HttpUtils.getPrivateAppInfo(privateInstanceName, ec2));
  }
}