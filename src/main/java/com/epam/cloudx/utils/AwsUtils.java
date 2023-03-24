package com.epam.cloudx.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.epam.cloudx.Exceptions.DuplicationInstanceNameException;
import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AwsUtils {

  public static AmazonEC2 createEc2Client(String accessKey, String secretKey) {
    var credentials = new BasicAWSCredentials(accessKey, secretKey);
    var region = Regions.EU_CENTRAL_1;
    return AmazonEC2ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(region)
        .build();
  }

  @SneakyThrows
  public static Instance getReservationByName(String name, AmazonEC2 ec2) {
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

  public static String getInstanceStateByName(String name, AmazonEC2 ec2) {
    return getReservationByName(name, ec2).getState().getName();
  }

  @SneakyThrows
  public static String getPublicIpAddressByName(String name, AmazonEC2 ec2) {
    if(getReservationByName(name, ec2).getPublicIpAddress() != null) {
      return getReservationByName(name, ec2).getPublicIpAddress();
    } else {
      throw new ServiceUnavailableFromPublicException("Public IP is empty. Instance is not accessible from internet");
    }
  }

  @SneakyThrows
  public static String getPrivateIpAddressByName(String name, AmazonEC2 ec2) {
    if(getReservationByName(name, ec2).getPublicIpAddress() != null) {
      return getReservationByName(name, ec2).getPrivateIpAddress();
    } else {
      throw new ServiceUnavailableFromPublicException("Private IP is empty. Instance is not accessible from internet");
    }
  }
}
