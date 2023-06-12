package com.epam.cloudx.suiteRunners.tests;
import com.epam.cloudx.config.Config;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

public abstract class CloudxImageBaseTest extends BaseTest {
  private final Config INSTANCE = Config.getInstance();
  AwsBasicCredentials credentials = AwsBasicCredentials.create(INSTANCE.getAccessKey(), INSTANCE.getSecretKey());

  protected String publicInstanceName = "cloudximage/AppInstance/Instance";
  protected String filePath = "src/main/resources/data/files/soap_vs_rest.png";
  protected String dbInstanceName = "cloudximage-databasemysqlinstance";

  protected S3Client s3Client = S3Client.builder()
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .region(Region.EU_CENTRAL_1)
      .build();

  protected SnsClient snsClient = SnsClient.builder()
      .region(Region.EU_CENTRAL_1)
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .build();

  protected SqsClient sqsClient = SqsClient.builder()
      .region(Region.EU_CENTRAL_1)
      .credentialsProvider(StaticCredentialsProvider.create(credentials))
      .build();

  protected RdsClient rdsClient = RdsClient.builder()
      .credentialsProvider(() -> credentials)
      .region(Region.EU_CENTRAL_1)
      .build();

  protected final String[] appTags = {"cloudx"};

}
