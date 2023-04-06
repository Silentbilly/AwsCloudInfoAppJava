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
  String publicSubnetId = "subnet-00290567ebe2f5ebc";
  String privateSubnetId = "subnet-070cd849b050a4687";

  DescribeRouteTablesRequest publicRequest = DescribeRouteTablesRequest.builder()
      .filters(Filter.builder()
          .name("association.subnet-id")
          .values(publicSubnetId)
          .build())
      .build();
  DescribeRouteTablesResponse publicResult = ec2.describeRouteTables(publicRequest);

  DescribeRouteTablesRequest privateRequest = DescribeRouteTablesRequest.builder()
      .filters(Filter.builder()
          .name("association.subnet-id")
          .values(privateSubnetId)
          .build())
      .build();
  DescribeRouteTablesResponse privateResult = ec2.describeRouteTables(privateRequest);
  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(1)
  public void isPublicInstanceAccessibleFromInternetByGateway() {
    List<RouteTable> table = publicResult.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.destinationCidrBlock()) &&
            "igw-06785e6a2bb39c343".equals(route.gatewayId()));
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(2)
  public void isPrivateInstanceNotAccessibleFromInternetByGateway() {
    List<RouteTable> table = privateResult.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.destinationCidrBlock()) &&
            "igw-06785e6a2bb39c343".equals(route.gatewayId()));
    Assertions.assertFalse(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(3)
  public void isPublicInstanceHasAccessToPrivate() {
    List<RouteTable> routeTables = publicResult.routeTables();
    boolean publicSubnetHasAccessToPrivateSubnet = routeTables.stream()
        .flatMap(rt -> rt.routes().stream()
            .filter(r -> r.destinationCidrBlock().equals("10.0.0.0/16")))
        .findFirst()
        .isPresent();
    Assertions.assertTrue(publicSubnetHasAccessToPrivateSubnet, () -> "Public VPC does NOT have access to Private VPC");

  }

  @Test
  @DisplayName("The private instance has access to the internet via NAT Gateway")
  @Tag("vpc")
  @Order(4)
  public void privateInstanceHasAccessToInternetByNatGateway() {
    List<RouteTable> table = privateResult.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.destinationCidrBlock()) && route.natGatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }
}
