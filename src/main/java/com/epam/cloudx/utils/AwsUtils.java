package com.epam.cloudx.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Reservation;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AwsUtils {
  @SneakyThrows
    public static String getInstanceStateByName(String name, AmazonEC2 ec2) throws Exception {
      // Filter reservations by name
      List<Reservation> reservations = ec2.describeInstances().getReservations().stream()
          .filter(s -> s.getInstances().toString().contains(name)).toList();

      // Find first. If more than 1, throw exception
      Reservation reservation;
      if(reservations.size() == 1) {
        reservation = reservations.get(0);
      } else {
        throw new Exception("Duplication of names in EC2. Create unique name");
      }
      return reservation.getInstances().get(0).getState().getName();
    }

    public static AmazonEC2 createEc2Client(String accessKey, String secretKey) {
      BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
      Regions region = Regions.EU_CENTRAL_1;
      return AmazonEC2ClientBuilder.standard()
          .withCredentials(new AWSStaticCredentialsProvider(credentials))
          .withRegion(region)
          .build();
    }
}
