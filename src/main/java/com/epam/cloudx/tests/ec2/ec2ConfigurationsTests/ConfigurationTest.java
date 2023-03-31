package com.epam.cloudx.tests.ec2.ec2ConfigurationsTests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

  private final Instance instance = AwsUtils.getInstanceByName(publicInstanceName, ec2);
  private static final String EXPECTED_INSTANCE_TYPE = String.format("%s.%s", T2, MICRO).toLowerCase();
  private final String[] expectedTags = {"Name", "cloudx"};
  private final List<String> expectedTagsList = new ArrayList<>(Arrays.asList(expectedTags));
  private String expectedRootBlockDeviceSize = "8 GB";


  @Test
  @DisplayName("Each EC2 instance should have the following configuration")
  @Tag("configuration")
  public void checkInstanceType() {
    final String actualInstanceType = instance.getInstanceType();
    boolean isExpectedTagsPresent = instance.getTags()
        .stream()
        .map(com.amazonaws.services.ec2.model.Tag::getKey)
        .toList().containsAll(expectedTagsList);
    System.out.println(instance.getBlockDeviceMappings());


        log.info("Verifying public instance configuration");
    assertAll(
        "configuration",
        () -> assertEquals(EXPECTED_INSTANCE_TYPE, actualInstanceType),
        () -> assertTrue(isExpectedTagsPresent)
    );
  }

  @Test
  @DisplayName("Public instance is available from internet")
  @Tag("public")
  public void publicInstanceIsAvailableFromInternet() {
    boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(publicInstanceName, ec2).isEmpty();
    assertTrue(isPublicIpExisting, "Public IP is empty");
  }
}
