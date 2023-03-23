package com.epam.cloudx;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import com.epam.cloudx.config.Config;
import com.epam.cloudx.utils.AwsUtils;

public class GetEC2Instances {

  public static void main(String[] args) throws Exception {
    /*private static */final Config INSTANCE = Config.getInstance();

    // configure credentials and region
    BasicAWSCredentials creds = new BasicAWSCredentials(INSTANCE.getAccessKey(), INSTANCE.getSecretKey());
    Regions region = Regions.EU_CENTRAL_1;

    // create an EC2 client
    AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(creds))
        .withRegion(region)
        .build();

    String publicInstanceName = "cloudxinfo/PublicInstance/Instance";

    String actualState = AwsUtils.getInstanceStateByName(publicInstanceName, ec2);

    System.out.println(actualState);
  }
}