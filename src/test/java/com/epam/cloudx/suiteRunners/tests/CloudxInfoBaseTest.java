package com.epam.cloudx.suiteRunners.tests;

public abstract class CloudxInfoBaseTest extends BaseTest {

  protected String publicInstanceName = "cloudxinfo/PublicInstance/Instance";
  protected String privateInstanceName = "cloudxinfo/PrivateInstance/Instance";
  protected String vpcName = "cloudxinfo/Network/Vpc";
  protected String publicVpcSubnetName = "cloudxinfo/Network/Vpc/PublicSubnetSubnet1";
  protected String privateVpcSubnetName = "cloudxinfo/Network/Vpc/PrivateSubnetSubnet1";
  protected final String[] appTags = {"Name", "cloudx"};
}
