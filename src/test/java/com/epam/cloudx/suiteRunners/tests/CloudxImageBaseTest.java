package com.epam.cloudx.suiteRunners.tests;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public abstract class CloudxImageBaseTest extends BaseTest {

  protected String publicInstanceName = "cloudximage/AppInstance/Instance";
  protected String filePath = "src/main/resources/data/files/soap_vs_rest.png";

  protected S3Client s3Client = S3Client.builder()
      .credentialsProvider(DefaultCredentialsProvider.create())
      .region(Region.EU_CENTRAL_1)
      .build();
}
