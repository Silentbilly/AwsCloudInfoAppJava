package com.epam.cloudx.tests.deploymentValidation;

import com.amazonaws.services.ec2.AmazonEC2;
import com.epam.cloudx.config.Config;
import com.epam.cloudx.utils.AwsUtils;

public class BaseTest {
  private static final Config INSTANCE = Config.getInstance();
  protected AmazonEC2 ec2 = AwsUtils.createEc2Client(INSTANCE.getAccessKey(), INSTANCE.getSecretKey());
}
