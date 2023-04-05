package com.epam.cloudx.tests.vpc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.amazonaws.services.ec2.model.DescribeRouteTablesRequest;
import com.amazonaws.services.ec2.model.DescribeRouteTablesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Route;
import com.amazonaws.services.ec2.model.RouteTable;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * VPC configuration:
 * The application should be deployed in non-default VPC with has 2 subnets: public and private.
 * VPC CIDR Block: 10.0.0.0/16
 * VPC tags: Name, cloudx
 */
@Log4j
public class VpcConfigurationTest extends BaseTest {

  private static final String EXPECTED_PUBLIC_VPC_SUBNET_TYPE = "Public";
  private static final String EXPECTED_PRIVATE_VPC_SUBNET_TYPE = "Private";
  private static final String EXPECTED_VPC_CIDR_BLOCK = "10.0.0.0/16";
  private final List<String> expectedTagsList = new ArrayList<>(Arrays.asList(appTags));


  @Test
  @DisplayName("The application should be deployed in non-default VPC")
  @Tag("vpc")
  @Tag("smoke")
  @Order(1)
  public void nonDefaultVpcTest() {
    String actualVpcCidrBlock = AwsUtils.getVpcByName(ec2, vpcName).cidrBlock();
    boolean isVpcContainTags = AwsUtils.isVpcTagsPresent(ec2, vpcName, expectedTagsList);

    log.info("Verifying non-default VOC, CIDR block, VPC tags");
    assertAll(
        () -> assertFalse(AwsUtils.isVpcDefaultByName(ec2, vpcName),
            "The application should be deployed in non-default VPC"),
        () -> assertEquals(EXPECTED_VPC_CIDR_BLOCK, actualVpcCidrBlock, "VPC CIDR Block should be: "
            + EXPECTED_VPC_CIDR_BLOCK),
        () -> assertTrue(isVpcContainTags, "VPC tags should be: " + expectedTagsList)
    );
  }

  @Test
  @DisplayName("The application should have 2 subnets: public and private")
  @Tag("vpc")
  @Order(2)
  public void vpcHasPublicSubnetTest() {
    String actualPublicVpcSubnetType = AwsUtils.getVpcSubnetTypeByName(ec2, publicVpcSubnetName);
    String actualPrivateVpcSubnetType = AwsUtils.getVpcSubnetTypeByName(ec2, privateVpcSubnetName);

    log.info("Verifying of type for public and private subnet");
    assertAll(
        () -> assertEquals(EXPECTED_PUBLIC_VPC_SUBNET_TYPE, actualPublicVpcSubnetType,
            String.format("The type of %s instance should be %s", publicVpcSubnetName,
                EXPECTED_PRIVATE_VPC_SUBNET_TYPE)),
        () -> assertEquals(EXPECTED_PRIVATE_VPC_SUBNET_TYPE, actualPrivateVpcSubnetType,
            String.format("The type of %s instance should be %s", privateVpcSubnetName,
                EXPECTED_PRIVATE_VPC_SUBNET_TYPE))
    );
  }
}
