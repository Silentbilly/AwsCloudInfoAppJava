package com.epam.cloudx.tests.vpc;


import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import com.epam.cloudx.tests.BaseTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.RouteTable;


public class VpcSubnetsAndRoutingConfigurationTest extends BaseTest {
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
  @DisplayName("")
  @Tag("vpc")
  @Order(1)
  public void isPublicInstanceAccessibleFromInternetByGateway() {
    List<RouteTable> table = publicRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) &&
            route.gatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(2)
  public void isPrivateInstanceNotAccessibleFromInternetByGateway() {
    List<RouteTable> table = privateRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) &&
            route.gatewayId() != null);
    Assertions.assertFalse(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(3)
  public void isPublicInstanceHasAccessToPrivate() {
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
  public void privateInstanceHasAccessToInternetByNatGateway() {
    List<RouteTable> table = privateRouteTables.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> ALL_ADDRESSES.equals(route.destinationCidrBlock()) && route.natGatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }
}
