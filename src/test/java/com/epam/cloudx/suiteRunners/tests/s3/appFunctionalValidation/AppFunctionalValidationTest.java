package com.epam.cloudx.suiteRunners.tests.s3.appFunctionalValidation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.cloudx.objects.cloudximage.S3UploadFileResponse;
import com.epam.cloudx.objects.cloudximage.S3UploadedFilesListResponse;
import com.epam.cloudx.suiteRunners.tests.CloudxImageBaseTest;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class AppFunctionalValidationTest extends CloudxImageBaseTest {

  @Test
  @DisplayName("Upload image to S3 bucket")
  @Tag("s3")
  @Order(1)
  void uploadFileToS3() {
    String filePath = "src/main/resources/data/files/soap_vs_rest.png";
    var response = HttpUtils.uploadImageToS3Bucket(ec2, publicInstanceName, filePath);
    var actualResponse = JsonUtils.readJsonAsObject(response, S3UploadFileResponse.class);
    assertNotNull(actualResponse.id());
  }

  @Test
  @DisplayName("View a list of uploaded images")
  @Tag("s3")
  @Order(2)
  void viewUploadedImages() {
    var response = HttpUtils.getAllImagesMetadata(ec2, publicInstanceName);
    var actualResponse = JsonUtils.readJsonAsList(response, S3UploadFileResponse[].class);
    assertNotEquals(0, actualResponse.size());
  }
}
