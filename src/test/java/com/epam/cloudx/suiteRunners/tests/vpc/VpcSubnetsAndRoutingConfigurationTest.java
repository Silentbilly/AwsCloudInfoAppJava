package com.epam.cloudx.suiteRunners.tests.vpc;


import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.RouteTable;

/**
 * Subnets and routing configuration:

 * The public instance should be accessible from the internet by Internet Gateway.
 * The public instance should have access to the private instance.
 * The private instance should have access to the internet via NAT Gateway.
 * The private instance should not be accessible from the public internet.
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VpcSubnetsAndRoutingConfigurationTest extends CloudxInfoBaseTest {
  private static final String ALL_ADDRESSES = "0.0.0.0/0";
  DescribeRouteTablesRequest publicRequest = DescribeRouteTablesRequest.builder()
      .filters(Filter.builder()
          .name("tag:Name")
          .values(publicVpcSubnetName)
          .build())
      .build();
  DescribeRouteTablesResponse publicRouteTables = ec2.describeRouteTables(publicRequest);

  DescribeRouteTablesRequest privateRequest = DescribeRouteTablesRequest.builder()
      .filters(Filter.builder()
          .name("tag:Name")
          .values(privateVpcSubnetName)
          .build())
      .build();
  DescribeRouteTablesResponse privateRouteTables = ec2.describeRouteTables(privateRequest);
  @Test
  @DisplayName("The public instance should be accessible from the internet by Internet Gateway")
  @Tag("vpc")
  @Order(1)
  void isPublicInstanceAccessibleFromInternetByGateway() {
    List<RouteTable> table = publicRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) &&
            route.gatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("The private instance should not be accessible from the public internet")
  @Tag("vpc")
  @Order(2)
  void isPrivateInstanceNotAccessibleFromInternetByGateway() {
    List<RouteTable> table = privateRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) &&
            route.gatewayId() != null);
    Assertions.assertFalse(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("The public instance should have access to the private instance")
  @Tag("vpc")
  @Order(3)
  void isPublicInstanceHasAccessToPrivate() {
    List<RouteTable> routeTables = publicRouteTables.routeTables();
    boolean publicSubnetHasAccessToPrivateSubnet = routeTables.stream()
        .flatMap(rt -> rt.routes().stream()
            .filter(r -> r.destinationCidrBlock().equals("10.0.0.0/16")))
        .findFirst()
        .isPresent();
    Assertions.assertTrue(publicSubnetHasAccessToPrivateSubnet, "Public VPC does NOT have access to Private VPC");

  }

  @Test
  @DisplayName("The private instance has access to the internet via NAT Gateway")
  @Tag("vpc")
  @Order(4)
  void privateInstanceHasAccessToInternetByNatGateway() {
    List<RouteTable> table = privateRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) && route.natGatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }
}
