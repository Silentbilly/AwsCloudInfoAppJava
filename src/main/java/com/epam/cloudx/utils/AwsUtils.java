package com.epam.cloudx.utils;

import com.epam.cloudx.Exceptions.DuplicationInstanceNameException;
import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.InstanceTypes;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.Instance;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j;

import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Vpc;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DBSubnetGroup;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbSubnetGroupsRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbSubnetGroupsResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicRequest;
import software.amazon.awssdk.services.sns.model.ListSubscriptionsByTopicResponse;
import software.amazon.awssdk.services.sns.model.ListTopicsRequest;
import software.amazon.awssdk.services.sns.model.ListTopicsResponse;
import software.amazon.awssdk.services.sns.model.Subscription;
import software.amazon.awssdk.services.sns.model.Topic;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;

@Log4j
@UtilityClass
public class AwsUtils {

  private static final int CONNECTION_TIMEOUT = 3000; // 3 seconds
  private static final int READ_TIMEOUT = 5000; // 5 seconds
  private static final Integer PORT_22 = 22;
  private static final Integer PORT_80 = 80;
  private static final String HTTP_FROM_INTERNET = "HTTP from Internet";
  private static final String SSH_FROM_INTERNET = "SSH from Internet";

  public static Ec2Client createEc2Client() {
    log.info("Connecting to ec2");
    return Ec2Client.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.EU_CENTRAL_1)
        .build();
  }

  @SneakyThrows
  public static Instance getInstanceByName(Ec2Client ec2, String name) {
    log.info("Getting instance with name: " + name);
    // Filter reservations by name
    var reservations = ec2.describeInstances().reservations().stream()
        .filter(s -> s.instances().toString().contains("running"))
        .filter(s -> s.instances().toString().contains(name)).toList();
    // Find first. If more than 1, throw exception
    Reservation reservation;
    if (reservations.size() == 1) {
      reservation = reservations.get(0);
    } else {
      throw new DuplicationInstanceNameException("Duplication of names in EC2. Create unique name");
    }
    return reservation.instances().get(0);
  }

  public static String getInstanceStateByName(Ec2Client ec2, String name) {
    log.info("Getting state of instance " + name);
    return getInstanceByName(ec2, name).state().name().toString();
  }

  @SneakyThrows
  public static String getPublicIpAddressByName(Ec2Client ec2, String name) {
    log.info("Getting public IP of instance " + name);
    if (getInstanceByName(ec2, name).publicIpAddress() != null) {
      return getInstanceByName(ec2, name).publicIpAddress();
    } else {
      String msg = "Public IP is empty. Instance is not accessible from internet";
      log.error(msg);
      throw new ServiceUnavailableFromPublicException(msg);
    }
  }

  @SneakyThrows
  public static String getPrivateIpAddressByName(Ec2Client ec2, String name) {
    if (getInstanceByName(ec2, name).publicIpAddress() != null) {
      return getInstanceByName(ec2, name).privateIpAddress();
    } else {
      String msg = "Public IP is empty. Instance is not accessible from internet";
      log.error(msg);
      throw new ServiceUnavailableFromPublicException(msg);
    }
  }

  public static boolean areEc2TagsPresent(Instance instance, List<String> tags) {
    return new HashSet<>(instance.tags()
        .stream()
        .map(Tag::key)
        .toList()).containsAll(tags);
  }

  public static boolean areDbInstanceTagsPresent(DBInstance dbInstance, List<String> tags) {
    return new HashSet<>(dbInstance.tagList()
        .stream()
        .map(software.amazon.awssdk.services.rds.model.Tag::key)
        .toList()).containsAll(tags);
  }

  @SneakyThrows
  public static Integer getVolumeSizeByInstanceName(Ec2Client ec2, String instanceName) {
    var volumesList = ec2.describeVolumes().volumes();
    String instanceId = getInstanceByName(ec2, instanceName).instanceId();
    var volume = volumesList
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.attachments().toString().contains(instanceId)).findAny()
        .orElse(null);
    if (volume != null) {
      return volume.size();
    } else {
      throw new NoSuchElementException("No size for storage in instance " + instanceName);
    }
  }

  public static boolean isInstanceHasPublicIp(Ec2Client ec2, String instanceName) {
    try {
      String address = AwsUtils.getPublicIpAddressByName(ec2, instanceName);
      var inetAddress = InetAddress.getByName(address);
      return inetAddress instanceof Inet4Address || inetAddress instanceof Inet6Address;
    } catch (UnknownHostException e) {
      return false;
    }
  }

  public static Vpc getVpcByName(Ec2Client ec2, String vpcName) {
    // Retrieve information about all VPCs in your account
    var vpcList = ec2.describeVpcs().vpcs();
    var vpc = vpcList
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.toString().contains(vpcName)).findAny()
        .orElse(null);
    if (vpc != null) {
      return vpc;
    } else {
      throw new NoSuchElementException("No such vpc: " + vpcName);
    }
  }

  public static boolean isVpcDefaultByName(Ec2Client ec2, String vpcName) {
    return getVpcByName(ec2, vpcName).isDefault();
  }

  public static boolean isVpcTagsPresent(Ec2Client ec2, String vpcName, List<String> tags) {
    return new HashSet<>(AwsUtils.getVpcByName(ec2, vpcName).tags()
        .stream()
        .map(Tag::key)
        .toList()).containsAll(tags);
  }

  public static Subnet getSubnetByName(Ec2Client ec2, String vpcSubnetName) {
    var subnets = ec2.describeSubnets().subnets();
    var subnet = subnets
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.toString().contains(vpcSubnetName))
        .findAny()
        .orElse(null);
    if (subnet != null) {
      return subnet;
    } else {
      throw new NoSuchElementException(String.format("The vpc doesn't have subnet %s", vpcSubnetName));
    }
  }

  public static String getVpcSubnetTypeByName(Ec2Client ec2, String vpcSubnetName) {
    var subnet = getSubnetByName(ec2, vpcSubnetName);
    var subnetTypeTag = subnet.tags()
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.key().contains("subnet-type"))
        .findAny()
        .orElse(null);
    if (subnetTypeTag != null) {
      return subnetTypeTag.value();
    } else {
      throw new NoSuchElementException(String.format("The subnet tags of %s are empty", vpcSubnetName));
    }
  }

  public static boolean isAppInPublicSubnet(Ec2Client ec2, String vpcSubnetName) {
    var instance = getInstanceByName(ec2, vpcSubnetName);
    var networkInterface = instance.networkInterfaces().get(0);

    String subnetId = networkInterface.subnetId();

    // Retrieve information about the subnet
    var subnetRequest = DescribeSubnetsRequest.builder()
        .subnetIds(subnetId)
        .build();
    var subnetResult = ec2.describeSubnets(subnetRequest);
    var subnet = subnetResult.subnets().get(0);
    return subnet.mapPublicIpOnLaunch();
  }

  public static boolean isInstanceAccessibleByPublicIpAddress(Ec2Client ec2, String instanceName) {
    var instance = getInstanceByName(ec2, instanceName);
    String publicIpAddress = instance.publicIpAddress();
    return isAccessible(publicIpAddress);
  }

  public static boolean isInstanceAccessibleByFqdn(Ec2Client ec2, String instanceName) {
    var instance = getInstanceByName(ec2, instanceName);
    String publicDnsName = instance.publicDnsName();
    return isAccessible(publicDnsName);
  }

  public static boolean isInstanceAccessibleBySsh(Ec2Client ec2, String instanceName) {
    var ipPermission = getIpPermissionsByPort(ec2, instanceName, PORT_22);
    if (ipPermission != null) {
      return ipPermission.ipRanges().get(0).description().equals(SSH_FROM_INTERNET);
    } else {
      throw new NoSuchElementException("No such IP range");
    }
  }
  public static boolean isInstanceAccessibleByHttp(Ec2Client ec2, String instanceName) {
    var ipPermission = getIpPermissionsByPort(ec2, instanceName, PORT_80);
    if (ipPermission != null) {
      return ipPermission.ipRanges().get(0).description().equals(HTTP_FROM_INTERNET);
    } else {
      throw new NoSuchElementException("No such IP range");
    }
  }
  private static IpPermission getIpPermissionsByPort(Ec2Client ec2, String instanceName, Integer port) {
    Instance instance = getInstanceByName(ec2, instanceName);

    String securityGroupId = instance.securityGroups().get(0).groupId();

    var describeSecurityGroupsRequest = DescribeSecurityGroupsRequest.builder()
        .groupIds(securityGroupId)
        .build();
    var securityGroup = ec2.describeSecurityGroups(describeSecurityGroupsRequest)
        .securityGroups().get(0);

    var ipPermissions = securityGroup.ipPermissions();
    return ipPermissions
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.fromPort()
            .equals(port)).findAny()
        .orElse(null);
  }

  private static boolean isAccessible(String url) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL("http://" + url + "/api/ui").openConnection();
      connection.setConnectTimeout(CONNECTION_TIMEOUT);
      connection.setReadTimeout(READ_TIMEOUT);
      connection.setRequestMethod("HEAD");
      int responseCode = connection.getResponseCode();
      return (200 <= responseCode && responseCode <= 399);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean isS3fromBucketListAccessibleByIamRole(S3Client s3Client, List<Bucket> bucketList) {
    boolean accessResult = false;
    for (Bucket bucket : bucketList) {
      String bucketName = bucket.name();
      log.info("Checking access to bucket: " + bucketName);

      // Call listObjectsV2() to check access to bucket
      ListObjectsV2Response result = s3Client.listObjectsV2(builder -> builder.bucket(bucketName));
      List<S3Object> objects = result.contents();

      // Print the current IAM role being used by the client
      var credentialsProvider = DefaultCredentialsProvider.create();
      var credentials = credentialsProvider.resolveCredentials();
      String accessKeyId = credentials.accessKeyId();
      log.info("Using IAM role: " + accessKeyId);

      // Check response for expected objects or data
      if (!objects.isEmpty()) {
        accessResult = true;
        log.info("User with IAM role: " + accessKeyId + " has access to bucket: " + bucketName);
      } else {
        accessResult = false;
        break;
      }
    }
    return accessResult;
  }

  public static DBInstance getDbInstanceByNamePart(RdsClient rdsClient, String dbName) {
    // Build the request to describe all RDS instances
    var request = DescribeDbInstancesRequest.builder().build();

    // Call the RDS client to retrieve information about all RDS instances
    var response = rdsClient.describeDBInstances(request);

    // Filter the RDS instances to find the one whose name contains the given string
    return response.dbInstances().stream()
        .filter(Objects::nonNull)
        .filter(instance -> instance.dbInstanceIdentifier().contains(dbName))
        .findFirst()
        .orElse(null);
  }
  public static boolean areAllDbAppSubnetsPrivate(Ec2Client ec2, RdsClient rdsClient, String dbName) {
    var subnets = getDbInstanceByNamePart(rdsClient, dbName).dbSubnetGroup().subnets();

    for (var subnet : subnets) {
      var subnetRequest = DescribeSubnetsRequest.builder()
          .subnetIds(subnet.subnetIdentifier())
          .build();
      var subnetResult = ec2.describeSubnets(subnetRequest);
      var subnetDetails = subnetResult.subnets().get(0);

      String subnetType = subnetDetails.tags().stream()
          .filter(Objects::nonNull)
          .filter(s -> s.key().equals("aws-cdk:subnet-type"))
          .findAny()
          .map(Tag::value)
          .orElse(null);

      if (subnetType.equals(InstanceTypes.PUBLIC.getValue())) {
        return false;
      }
    }
    return true;
  }

  public static void isRdsAccessibleFromAppPublic(Ec2Client ec2, RdsClient rdsClient, String dbName) {
    var dbInstance = getDbInstanceByNamePart(rdsClient, dbName);
    String subnetGroupId = dbInstance.dbSubnetGroup().toString();
    System.out.println(subnetGroupId);
    System.out.println(dbInstance.vpcSecurityGroups().get(0).vpcSecurityGroupId());
    System.out.println(dbInstance.vpcSecurityGroups().size());

    String groupId = dbInstance.vpcSecurityGroups().get(0).vpcSecurityGroupId();

    DescribeSecurityGroupsRequest sgRequest = DescribeSecurityGroupsRequest.builder()
        .groupIds(groupId)
        .build();
    DescribeSecurityGroupsResponse sgResult = ec2.describeSecurityGroups(sgRequest);
    SecurityGroup securityGroup = sgResult.securityGroups().get(0);

    System.out.println(securityGroup.ipPermissions());
  }

  public static List<Topic> getListOfSnsTopicsArn(SnsClient snsClient) {
    ListTopicsRequest request = ListTopicsRequest.builder().build();
    log.info("Getting list of all SNS topics");
    return snsClient.listTopics(request).topics();
  }

  public static List<String> getListOfSqsQueues(SqsClient sqsClient) {
    ListQueuesRequest request = ListQueuesRequest.builder().build();
    log.info("Getting list of SQS urls");
    return sqsClient.listQueues(request).queueUrls();
  }

/*  public static List<Subscription> getListOfSubscriptionsToTopic(SnsClient snsClient) {
    ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
        .topicArn(getFirstSnsTopicArn(snsClient))
        .build();
    return snsClient.listSubscriptionsByTopic(request).subscriptions();
  }*/
}
