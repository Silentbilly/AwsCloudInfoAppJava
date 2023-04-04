package com.epam.cloudx.tests.ec2.ec2ConfigurationsTests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.amazon.awscdk.services.ec2.InstanceClass.T2;
import static software.amazon.awscdk.services.ec2.InstanceSize.MICRO;

import com.amazonaws.services.ec2.model.Instance;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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
public class ConfigurationTest extends BaseTest {

  private final Instance publicInstance = AwsUtils.getInstanceByName(ec2, publicInstanceName);
  private final Instance privateInstance = AwsUtils.getInstanceByName(ec2, privateInstanceName);
  private final String expectedInstancetype = String.format("%s.%s", T2, MICRO).toLowerCase();
  private final List<String> expectedTagsList = new ArrayList<>(Arrays.asList(appTags));
  private static final Integer EXPECTED_DEVICE_SIZE = 8;
  private static final String EXPECTED_OS = "Linux/UNIX";


  @Test
  @DisplayName("Public EC2 instance should have the following configuration")
  @Tag("configuration")
  public void checkPublicInstanceType() {
    String actualInstanceType = publicInstance.getInstanceType();
    boolean isExpectedTagsPresent = AwsUtils.isEc2TagsPresent(publicInstance, expectedTagsList);
    Integer actualDeviceSize = AwsUtils.getVolumeSizeByInstanceName(ec2, publicInstanceName);
    String actualOs = publicInstance.getPlatformDetails();
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
  public void checkPrivateInstanceType() {
    String actualInstanceType = privateInstance.getInstanceType();
    boolean isExpectedTagsPresent = AwsUtils.isEc2TagsPresent(privateInstance, expectedTagsList);
    Integer actualDeviceSize = AwsUtils.getVolumeSizeByInstanceName(ec2, privateInstanceName);
    String actualOs = privateInstance.getPlatformDetails();
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
