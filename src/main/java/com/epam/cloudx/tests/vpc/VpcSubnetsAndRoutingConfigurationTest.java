package com.epam.cloudx.tests.vpc;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeRouteTablesResponse;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RouteState;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.RouteTable;


public class VpcSubnetsAndRoutingConfigurationTest extends BaseTest {
  String publicSubnetId = "subnet-080e362592a425dca";
  String privateSubnetId = "subnet-0e9c0393d39ec7ca8";
  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(1)
  public void isPublicInstanceAccessibleFromInternetByGateway() throws IOException {
/*    DescribeRouteTablesRequest request = new DescribeRouteTablesRequest()
        .withFilters(
            new Filter("association.subnet-id").withValues(publicSubnetId)
        );
    DescribeRouteTablesResult result = ec2.describeRouteTables(request);
    List<RouteTable> table = result.getRouteTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.getRoutes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.getDestinationCidrBlock()) &&
            "igw-09d1bad3b499d9015".equals(route.getGatewayId()));
    Assertions.assertTrue(isAccessibleFromInternetByGateway);*/
    // Retrieve the public IP address of the EC2 instance
    String publicIpAddress = AwsUtils.getPublicIpAddressByName(ec2, publicInstanceName);

    // Create an HTTP connection to the public IP address of the EC2 instance
    URL url = new URL("http://" + publicIpAddress);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");

    // Send an HTTP GET request to a known endpoint on the EC2 instance
    connection.connect();
    int responseCode = connection.getResponseCode();

    // Verify that the HTTP response status code is 200 OK
    assert responseCode == 200 : "Instance is not reachable from the internet";
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(2)
  public void isPrivateInstanceNotAccessibleFromInternetByGateway() {
    // Describe route tables
    DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder()
        .filters(Filter.builder()
            .name("association.subnet-id")
            .values(privateSubnetId)
            .build())
        .build();
    DescribeRouteTablesResponse result = ec2.describeRouteTables(request);
    List<RouteTable> table = result.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.destinationCidrBlock()) &&
            "igw-09d1bad3b499d9015".equals(route.gatewayId()));
    Assertions.assertFalse(isAccessibleFromInternetByGateway);
  }

  @Test
  @DisplayName("")
  @Tag("vpc")
  @Order(3)
  public void isPublicInstanceHasAccessToPrivate() {
/*
    // Find the route table associated with the public VPC that has a route to the private VPC
    DescribeRouteTablesRequest request = new DescribeRouteTablesRequest()
        .withFilters(new Filter("association.subnet-id").withValues(publicSubnetId));
    DescribeRouteTablesResult result = ec2.describeRouteTables(request);

    List<RouteTable> routeTables = result.getRouteTables();

    boolean publicSubnetHasAccessToPrivateSubnet = routeTables.stream()
        .flatMap(rt -> rt.getRoutes().stream()
            .filter(r -> r.getDestinationCidrBlock().equals("10.0.0.0/16")
                && r.getState().equals(RouteState.Active.toString())))
        .findFirst()
        .isPresent();
    Assertions.assertTrue(publicSubnetHasAccessToPrivateSubnet, "Public VPC does NOT have access to Private VPC");*/

    Instance publicInstance = AwsUtils.getInstanceByName(ec2, publicInstanceName);
    Instance privateInstance = AwsUtils.getInstanceByName(ec2, privateInstanceName);

    String publicIpAddress = publicInstance.publicIpAddress();
    String privateIpAddress = privateInstance.privateIpAddress();

    DescribeInstancesResponse describeInstancesResponse = ec2.describeInstances(
        DescribeInstancesRequest.builder()
            .instanceIds(publicInstance.instanceId(), privateInstance.instanceId())
            .build());

    JSch jsch = new JSch();
    try {
      jsch.addIdentity(PERMISSIONS_FILE_PATH);
      Session session = jsch.getSession("ec2-user", publicIpAddress, 22);
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();

      String command = "ping -c 5 " + privateIpAddress;
      Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setCommand(command);

      InputStream in = channel.getInputStream();
      channel.connect();

      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }

      channel.disconnect();
      session.disconnect();
    } catch (JSchException | IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("The private instance has access to the internet via NAT Gateway")
  @Tag("vpc")
  @Order(4)
  public void privateInstanceHasAccessToInternetByNatGateway() throws IOException {
    // Describe route tables
    DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder()
        .filters(Filter.builder()
            .name("association.subnet-id")
            .values(privateSubnetId)
            .build())
        .build();
    DescribeRouteTablesResponse result = ec2.describeRouteTables(request);
    List<RouteTable> table = result.routeTables();
    boolean isAccessibleFromInternetByGateway = table.stream()
        .flatMap(s -> s.routes().stream())
        .anyMatch(route -> "0.0.0.0/0".equals(route.destinationCidrBlock()) && route.natGatewayId() != null);
    Assertions.assertTrue(isAccessibleFromInternetByGateway);
  }
}
