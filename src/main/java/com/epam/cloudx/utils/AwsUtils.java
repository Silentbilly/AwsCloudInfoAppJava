package com.epam.cloudx.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Volume;
import com.epam.cloudx.Exceptions.DuplicationInstanceNameException;
import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
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

import com.amazonaws.services.ec2.model.Vpc;

@Log4j
@UtilityClass
public class AwsUtils {

  public static AmazonEC2 createEc2Client(String accessKey, String secretKey) {
    var credentials = new BasicAWSCredentials(accessKey, secretKey);
    var region = Regions.EU_CENTRAL_1;
    log.info("Connecting to ec2");
    return AmazonEC2ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(region)
        .build();
  }

  @SneakyThrows
  public static Instance getInstanceByName(AmazonEC2 ec2, String name) {
    log.info("Getting instance with name: " + name);
    // Filter reservations by name
    var reservations = ec2.describeInstances().getReservations().stream()
        .filter(s -> s.getInstances().toString().contains(name)).toList();

    // Find first. If more than 1, throw exception
    Reservation reservation;
    if (reservations.size() == 1) {
      reservation = reservations.get(0);
    } else {
      throw new DuplicationInstanceNameException("Duplication of names in EC2. Create unique name");
    }
    return reservation.getInstances().get(0);
  }

  public static String getInstanceStateByName(AmazonEC2 ec2, String name) {
    log.info("Getting state of instance " + name);
    return getInstanceByName(ec2, name).getState().getName();
  }

  @SneakyThrows
  public static String getPublicIpAddressByName(AmazonEC2 ec2, String name) {
    log.info("Getting public IP of instance " + name);
    if (getInstanceByName(ec2, name).getPublicIpAddress() != null) {
      return getInstanceByName(ec2, name).getPublicIpAddress();
    } else {
      String msg = "Public IP is empty. Instance is not accessible from internet";
      log.error(msg);
      throw new ServiceUnavailableFromPublicException(msg);
    }
  }

  @SneakyThrows
  public static String getPrivateIpAddressByName(AmazonEC2 ec2, String name) {
    if (getInstanceByName(ec2, name).getPublicIpAddress() != null) {
      return getInstanceByName(ec2, name).getPrivateIpAddress();
    } else {
      String msg = "Public IP is empty. Instance is not accessible from internet";
      log.error(msg);
      throw new ServiceUnavailableFromPublicException(msg);
    }
  }

  public static boolean isEc2TagsPresent(Instance instance, List<String> tags) {
    return new HashSet<>(instance.getTags()
        .stream()
        .map(Tag::getKey)
        .toList()).containsAll(tags);
  }

  @SneakyThrows
  public static Integer getVolumeSizeByInstanceName(AmazonEC2 ec2, String instanceName) {
    List<Volume> volumesList = ec2.describeVolumes().getVolumes();
    String instanceId = getInstanceByName(ec2, instanceName).getInstanceId();
    Volume volume = volumesList
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.getAttachments().toString().contains(instanceId)).findAny()
        .orElse(null);
    if (volume != null) {
      return volume.getSize();
    } else {
      throw new NoSuchElementException("No size for storage in instance " + instanceName);
    }
  }

  public static boolean isInstanceHasPublicIp(AmazonEC2 ec2, String instanceName) {
    try {
      String address = AwsUtils.getPublicIpAddressByName(ec2, instanceName);
      InetAddress inetAddress = InetAddress.getByName(address);
      return inetAddress instanceof Inet4Address || inetAddress instanceof Inet6Address;
    } catch (UnknownHostException e) {
      return false;
    }
  }

  public static Vpc getVpcByName(AmazonEC2 ec2, String vpcName) {
    // Retrieve information about all VPCs in your account
    List<Vpc> vpcList = ec2.describeVpcs().getVpcs();
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

  public static boolean isVpcDefaultByName(AmazonEC2 ec2, String vpcName) {
    return getVpcByName(ec2, vpcName).isDefault();
  }

  public static boolean isVpcTagsPresent(AmazonEC2 ec2, String vpcName, List<String> tags) {
    return new HashSet<>(AwsUtils.getVpcByName(ec2, vpcName).getTags()
        .stream()
        .map(Tag::getKey)
        .toList()).containsAll(tags);
  }

  public static Subnet getSubnetByName(AmazonEC2 ec2, String vpcSubnetName) {
    List<Subnet> subnets = ec2.describeSubnets().getSubnets();
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

  public static String getVpcSubnetTypeByName(AmazonEC2 ec2, String vpcSubnetName) {
    Subnet subnet = getSubnetByName(ec2, vpcSubnetName);
    Tag subnetTypeTag = subnet.getTags()
        .stream()
        .filter(Objects::nonNull)
        .filter(s -> s.getKey().contains("subnet-type"))
        .findAny()
        .orElse(null);
    if (subnetTypeTag != null) {
      return subnetTypeTag.getValue();
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