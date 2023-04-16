package com.epam.cloudx.suiteRunners.tests.s3.deploymentValidation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.cloudx.suiteRunners.tests.CloudxImageBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.util.List;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * The application is deployed in the public subnet and should be accessible by HTTP from the internet via an Internet
 * gateway by public IP address and FQDN.
 * The application instance should be accessible by SSH protocol.
 * The application should have access to the S3 bucket via an IAM role.
 */

@Log4j
class InstanceAccessibilityTestTest extends CloudxImageBaseTest {

  @Test
  @DisplayName("The application is deployed in the public subnet")
  @Tag("s3")
  @Tag("smoke")
  void subnetIsPublic() {
    final boolean isSubnetPublic = AwsUtils.isAppInPublicSubnet(ec2, publicInstanceName);
    Assertions.assertTrue(isSubnetPublic, "The app must be in public subnet");
  }

  @Test
  @DisplayName("The application is accessible by HTTP from the internet")
  @Tag("s3")
  @Tag("smoke")
  void instanceAccessibleByPublicIpAddress() {
    final boolean isAccessibleByPublicIp = AwsUtils.isInstanceAccessibleByPublicIpAddress(ec2, publicInstanceName);
    final boolean isAccessibleByHttp = AwsUtils.isInstanceAccessibleByHttp(ec2, publicInstanceName);
    assertAll(
        "accessibility by HTTP and public ip",
        () -> assertTrue(isAccessibleByPublicIp, "The app must be accessible by public IP"),
        () -> assertTrue(isAccessibleByHttp, "The app must be accessible by public HTTP")
    );
  }

  @Test
  @DisplayName("The application is accessible by FQDN from the internet")
  @Tag("s3")
  @Tag("smoke")
  void instanceAccessibleByFqdn() {
    final boolean isAccessible = AwsUtils.isInstanceAccessibleByFqdn(ec2, publicInstanceName);
    Assertions.assertTrue(isAccessible, "The app must be accessible by FQDN");
  }

  @Test
  @DisplayName("The application instance should be accessible by SSH protocol")
  @Tag("s3")
  @Tag("smoke")
  void appAccessibleBySsh() {
    final boolean isAccessible = AwsUtils.isInstanceAccessibleBySsh(ec2, publicInstanceName);
    Assertions.assertTrue(isAccessible, "The app must be accessible by SSH");
  }

  @Test
  @DisplayName("The application should have access to the S3 bucket via an IAM role.")
  @Tag("s3")
  @Tag("smoke")
  void appAccessibleToS3ByIamRole() {
    List<Bucket> bucketList = s3Client.listBuckets().buckets();

    // Check access to each bucket in the list
    for (Bucket bucket : bucketList) {
      String bucketName = bucket.name();
      log.info("Checking access to bucket: " + bucketName);

      // Call listObjectsV2() to check access to bucket
      ListObjectsV2Response result = s3Client.listObjectsV2(builder -> builder.bucket(bucketName));
      List<S3Object> objects = result.contents();

      // Print the current IAM role being used by the client
      AwsCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
      AwsCredentials credentials = credentialsProvider.resolveCredentials();
      String accessKeyId = credentials.accessKeyId();
      log.info("Using IAM role: " + accessKeyId);

      // Check response for expected objects or data
      if (objects.size() > 0) {
        System.out.println("Application has access to S3 bucket " + bucketName + " via IAM role.");
      } else {
        System.out.println("Application does not have access to S3 bucket " + bucketName + " via IAM role.");
      }
    }
  }
}
