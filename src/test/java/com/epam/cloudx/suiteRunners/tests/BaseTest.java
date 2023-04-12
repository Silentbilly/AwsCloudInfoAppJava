package com.epam.cloudx.suiteRunners.tests;

import com.epam.cloudx.utils.AwsUtils;
import com.epam.reportportal.junit5.ReportPortalExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.ec2.Ec2Client;

@ExtendWith(ReportPortalExtension.class)
public abstract class BaseTest {

  protected Ec2Client ec2 = AwsUtils.createEc2Client();
}
