package com.epam.cloudx.suiteRunners.tests.s3.deploymentValidation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.cloudx.suiteRunners.tests.CloudxImageBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import software.amazon.awssdk.services.rds.model.DBInstance;

/**
 * The application database (MySQL RDS instance) is deployed in the private subnet and should be accessible only from
 * the application's public subnet, but not from the public internet.
 * The application should have access to MySQL RDS via an IAM role.
 *  ----------------------------------------------------------------
 * RDS Instance requirements:
 * Instance type: db.t3.micro
 * Multi-AZ: no
 * Storage size: 100 GiB
 * Storage type: General Purpose SSD (gp2)
 * Encryption: not enabled
 * Instance tags: cloudx
 * Database type: MySQL
 * Database version: 8.0.28
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppDataBaseDeploymentTest extends CloudxImageBaseTest {

  private final DBInstance dbInstance = AwsUtils.getDbInstanceByNamePart(rdsClient, dbInstanceName);
  private final List<String> expectedTagsList = new ArrayList<>(Arrays.asList(appTags));


  @AfterAll
  void closeS3Client() {
    ec2.close();
    rdsClient.close();
  }

  @Test
  @DisplayName("The application database (MySQL RDS instance) is deployed in the private subnet")
  @Tag("rds")
  @Tag("smoke")
  void isRdsInPrivateSubnetTest() {
    Assertions.assertTrue(AwsUtils.areAllDbAppSubnetsPrivate(ec2, rdsClient, dbInstanceName),
        "Instance should be in private subnet");
  }

  @Test
  @DisplayName("The application is not accessible from the public internet")
  @Tag("rds")
  @Tag("smoke")
  void isNotAccessibleFromInternetTest() {
    assertFalse(dbInstance.publiclyAccessible(),
        "The application should not be accessible from the public internet");
  }

  @Test
  @DisplayName("The application should have access to MySQL RDS via an IAM role.")
  @Tag("rds")
  @Tag("smoke")
  void isAppHasAccessToMySqlRdsViaIamRoleTest() {
    Assertions.assertTrue(dbInstance.hasAssociatedRoles());
  }

  @Test
  @DisplayName("RDS Instance requirements")
  @Tag("rds")
  @Tag("smoke")
  void rdsInstanceCheckTest() {
    String expectedInstanceType = "db.t3.micro";
    String expectedStorageType = "gp2";
    String expectedDataBaseType = "mysql";
    String expectedDataBaseVersion = "8.0.28";
    assertAll(
        "RDS Instance requirements:",
        () -> assertEquals(
            expectedInstanceType, dbInstance.dbInstanceClass(), "Instance type: should be " + expectedInstanceType
        ),
        () -> assertFalse(dbInstance.multiAZ(), "Multi-AZ: should be false"),
        () -> assertEquals(
            expectedStorageType, dbInstance.storageType(), "Storage type: should be " + expectedStorageType
        ),
        () -> assertFalse(dbInstance.storageEncrypted(), "Encryption: not enabled"),
        () -> assertTrue(
            AwsUtils.areDbInstanceTagsPresent(dbInstance, expectedTagsList), "Instance tags: " + expectedTagsList
        ),
        () -> assertEquals(
            expectedDataBaseType, dbInstance.engine(), "Database type: should be " + expectedDataBaseType
        ),
        () -> assertEquals(
            expectedDataBaseVersion, dbInstance.engineVersion(), "Database version: should be " + expectedDataBaseVersion
        )
    );
  }
}
