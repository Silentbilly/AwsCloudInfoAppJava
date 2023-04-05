package com.epam.cloudx.tests;

import com.epam.cloudx.config.Config;
import com.epam.cloudx.utils.AwsUtils;
import com.epam.reportportal.junit5.ReportPortalExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.ec2.Ec2Client;

@ExtendWith(ReportPortalExtension.class)
public class BaseTest {

  private static final Config INSTANCE = Config.getInstance();
  protected String publicInstanceName = "cloudxinfo/PublicInstance/Instance";
  protected String privateInstanceName = "cloudxinfo/PrivateInstance/Instance";
  protected String vpcName = "cloudxinfo/Network/Vpc";
  protected String publicVpcSubnetName = "cloudxinfo/Network/Vpc/PublicSubnetSubnet1";
  protected String privateVpcSubnetName = "cloudxinfo/Network/Vpc/PrivateSubnetSubnet1";
  protected final String[] appTags = {"Name", "cloudx"};

  protected final String PERMISSIONS_FILE_PATH = "resources/data/keys/cloudxinfo-eu-central-1.pem";

  protected Ec2Client ec2 = AwsUtils.createEc2Client(INSTANCE.getAccessKey(), INSTANCE.getSecretKey());
}
