package com.epam.cloudx.suiteRunners.tests;
import com.epam.cloudx.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class CloudxImageBaseTest extends BaseTest {
  private Config INSTANCE = Config.getInstance();

  protected String publicInstanceName = "cloudximage/AppInstance/Instance";
  protected String filePath = "src/main/resources/data/files/soap_vs_rest.png";
  protected String dbInstanceName = "cloudximage-databasemysqlinstance";

  protected S3Client s3Client = S3Client.builder()
      .credentialsProvider(DefaultCredentialsProvider.create())
      .region(Region.EU_CENTRAL_1)
      .build();

  AwsBasicCredentials credentials = AwsBasicCredentials.create(INSTANCE.getAccessKey(), INSTANCE.getSecretKey());
  protected RdsClient rdsClient = RdsClient.builder()
      .credentialsProvider(() -> credentials)
      .region(Region.EU_CENTRAL_1)
      .build();

  protected final String[] appTags = {"cloudx"};

}
