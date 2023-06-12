package com.epam.cloudx.suiteRunners.tests.s3.appFunctionalValidation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.epam.cloudx.ResponseCodes;
import com.epam.cloudx.objects.cloudximage.S3UploadFileResponse;
import com.epam.cloudx.suiteRunners.tests.CloudxImageBaseTest;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import java.io.File;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppFunctionalValidationTest extends CloudxImageBaseTest {

  private static Integer imageId;

  @BeforeAll
  void setup() {
    var response = HttpUtils.getAllImagesMetadata(ec2, publicInstanceName);
    var actualResponse = JsonUtils.readJsonAsList(response, S3UploadFileResponse[].class);
    if (!actualResponse.isEmpty()) {
      actualResponse.forEach(s -> HttpUtils.deleteImageById(ec2, publicInstanceName, s.id()));
    }
  }

  @Test
  @DisplayName("Upload image to S3 bucket")
  @Tag("s3")
  @Order(1)
  void uploadFileToS3() {
    var response = HttpUtils.uploadImageToS3Bucket(ec2, publicInstanceName, filePath);
    var actualResponse = JsonUtils.readJsonAsObject(response, S3UploadFileResponse.class);
    imageId = actualResponse.id();
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

  @SneakyThrows
  @ParameterizedTest(name = "{index} - for image id = {arguments}")
  @MethodSource("getImageId")
  @DisplayName("Download images from the S3 bucket")
  @Tag("s3")
  @Order(3)
  void downloadImage(Integer imageId) {
    var response = HttpUtils.getImageById(ec2, publicInstanceName, imageId);
    byte[] expectedFile = FileUtils.readFileToByteArray(new File(filePath));
    byte[] actualResponse = response.asByteArray();
    assertEquals(response.getStatusCode(), ResponseCodes.OK.getValue());
    assertArrayEquals(actualResponse, expectedFile);
  }

  @SneakyThrows
  @ParameterizedTest(name = "{index} - for image id = {arguments}")
  @MethodSource("getImageId")
  @DisplayName("Delete images from the S3 bucket")
  @Tag("s3")
  @Order(4)
  void deleteImage(Integer imageId) {
    HttpUtils.deleteImageById(ec2, publicInstanceName, imageId);
    var response = HttpUtils.getImageInfoById(ec2, publicInstanceName, imageId);
    assertEquals(response.getStatusCode(), ResponseCodes.NOT_FOUND.getValue());
  }

  static Stream<Integer> getImageId() {
    return Stream.of(AppFunctionalValidationTest.imageId);
  }
}
