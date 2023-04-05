package com.epam.cloudx.utils;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.epam.cloudx.Exceptions.DuplicationInstanceNameException;
import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Instance;
/*import com.sshtools.client.SshClient;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshKeyPair;*/
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

import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.ec2.model.Tag;
import software.amazon.awssdk.services.ec2.model.Volume;
import software.amazon.awssdk.services.ec2.model.Vpc;

@Log4j
@UtilityClass
public class AwsUtils {

  public static Ec2Client createEc2Client(String accessKey, String secretKey) {
    var credentials = new BasicAWSCredentials(accessKey, secretKey);
    var region = Regions.EU_CENTRAL_1;
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

  public static boolean isEc2TagsPresent(Instance instance, List<String> tags) {
    return new HashSet<>(instance.tags()
        .stream()
        .map(Tag::key)
        .toList()).containsAll(tags);
  }

  @SneakyThrows
  public static Integer getVolumeSizeByInstanceName(Ec2Client ec2, String instanceName) {
    List<Volume> volumesList = ec2.describeVolumes().volumes();
    String instanceId = getInstanceByName(ec2, instanceName).instanceId();
    Volume volume = volumesList
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
      InetAddress inetAddress = InetAddress.getByName(address);
      return inetAddress instanceof Inet4Address || inetAddress instanceof Inet6Address;
    } catch (UnknownHostException e) {
      return false;
    }
  }

  public static Vpc getVpcByName(Ec2Client ec2, String vpcName) {
    // Retrieve information about all VPCs in your account
    List<Vpc> vpcList = ec2.describeVpcs().vpcs();
    Vpc vpc = vpcList
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
    List<Subnet> subnets = ec2.describeSubnets().subnets();
    Subnet subnet = subnets
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
    Subnet subnet = getSubnetByName(ec2, vpcSubnetName);
    Tag subnetTypeTag = subnet.tags()
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

/*    @SneakyThrows
    public static boolean isSshAccessible(Instance instance, String keyPairFilePath) {
        final String USER = "user_name";
        final String HOST = "ec2_public_address";
        final int PORT = 22;
        final String PRIVATE_KEY = "/home/aleks/Projects/AwsCloudInfoAppJava/src/main/resources/data/keys/cloudxinfo-eu-central-1.pem";
        final SshKeyPair pair = SshKeyUtils.getRSAPrivateKeyWithSHA256Signature(new File(PRIVATE_KEY), null);
        try (SshClient ssh = new SshClient(HOST, PORT, USER, pair)) {
            ssh.executeCommand("ssh -i \"cloudxinfo-eu-central-1.pem\" ec2-user@ec2-35-158-26-49.eu-central-1.compute.amazonaws.com");
            return true;
        } catch (SshException e) {
            return false; // SSH connection failed
        }
    }*/
}