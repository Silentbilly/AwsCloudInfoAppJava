package com.epam.cloudx.suiteRunners.tests.ec2.ec2ConfigurationsTests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.amazon.awssdk.services.ec2.model.UnlimitedSupportedInstanceFamily.T2;

import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.Instance;

/**
 * Each EC2 instance should have the following configuration:
 * Instance type: t2.micro
 * Instance tags: Name, cloudx
 * Root block device size: 8 GB
 * Instance OS: Amazon Linux
 * The public instance should have public IP assigned
 * The private instance should not have public IP assigned
 */

@Log4j
class ConfigurationTest extends CloudxInfoBaseTest {

  private final Instance publicInstance = AwsUtils.getInstanceByName(ec2, publicInstanceName);
  private final Instance privateInstance = AwsUtils.getInstanceByName(ec2, privateInstanceName);
  private final String expectedInstancetype = String.format("%s.micro", T2).toLowerCase();
  private final List<String> expectedTagsList = new ArrayList<>(Arrays.asList(appTags));
  private static final Integer EXPECTED_DEVICE_SIZE = 8;
  private static final String EXPECTED_OS = "Linux/UNIX";


  @Test
  @DisplayName("Public EC2 instance should have the following configuration")
  @Tag("configuration")
  void checkPublicInstanceType() {
    String actualInstanceType = publicInstance.instanceType().toString();
    boolean isExpectedTagsPresent = AwsUtils.areEc2TagsPresent(publicInstance, expectedTagsList);
    Integer actualDeviceSize = AwsUtils.getVolumeSizeByInstanceName(ec2, publicInstanceName);
    String actualOs = publicInstance.platformDetails();
    boolean isInstanceHavePublicIpAddress = AwsUtils.isInstanceHasPublicIp(ec2, publicInstanceName);

    log.info("Verifying public instance configuration");
    assertAll(
        "public configuration",
        () -> assertEquals(expectedInstancetype, actualInstanceType, "Instance type must be t2.micro"),
        () -> assertTrue(isExpectedTagsPresent, "Instance tags must be: Name, cloudx"),
        () -> assertEquals(EXPECTED_DEVICE_SIZE, actualDeviceSize, "Root block device size must be 8 GB"),
        () -> assertEquals(EXPECTED_OS, actualOs, "Instance OS must be: Linux"),
        () -> assertTrue(isInstanceHavePublicIpAddress, "The private instance should not have public IP assigned")
    );
  }

  @Test
  @DisplayName("Private EC2 instance should have the following configuration")
  @Tag("configuration")
  void checkPrivateInstanceType() {
    String actualInstanceType = privateInstance.instanceType().toString();
    boolean isExpectedTagsPresent = AwsUtils.areEc2TagsPresent(privateInstance, expectedTagsList);
    Integer actualDeviceSize = AwsUtils.getVolumeSizeByInstanceName(ec2, privateInstanceName);
    String actualOs = privateInstance.platformDetails();
    boolean isInstanceHavePublicIpAddress = AwsUtils.isInstanceHasPublicIp(ec2, privateInstanceName);

    log.info("Verifying private instance configuration");
    assertAll(
        "private configuration",
        () -> assertEquals(expectedInstancetype, actualInstanceType, "Instance type must be t2.micro"),
        () -> assertTrue(isExpectedTagsPresent, "Instance tags must be: Name, cloudx"),
        () -> assertEquals(EXPECTED_DEVICE_SIZE, actualDeviceSize, "Root block device size must be 8 GB"),
        () -> assertEquals(EXPECTED_OS, actualOs, "Instance OS must be: Linux"),
        () -> assertFalse(isInstanceHavePublicIpAddress, "The private instance should not have public IP assigned")
    );
  }
}
