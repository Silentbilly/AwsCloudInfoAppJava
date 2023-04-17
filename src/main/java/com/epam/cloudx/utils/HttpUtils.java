package com.epam.cloudx.utils;

import static io.restassured.RestAssured.given;

import com.epam.cloudx.endpoints.ApiGateEndpoints;
import io.restassured.http.ContentType;
import java.io.File;
import lombok.experimental.UtilityClass;
import io.restassured.response.Response;
import software.amazon.awssdk.services.ec2.Ec2Client;

@UtilityClass
public class HttpUtils {
  private final String HTTP = "http://";
  public static Response getPublicAppInfo(Ec2Client ec2, String instanceName) {
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(HTTP + AwsUtils.getPublicIpAddressByName(ec2, instanceName))
        .when()
        .get()
        .then()
        .log().all()
        .extract()
        .response();
  }

  public static Response getPrivateAppInfo(Ec2Client ec2, String instanceName) {
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(HTTP + AwsUtils.getPrivateIpAddressByName(ec2, instanceName))
        .when()
        .get()
        .then()
        .log().all()
        .extract()
        .response();
  }

  public static Response uploadImageToS3Bucket(Ec2Client ec2, String instanceName, String filePath) {
    File fileToUpload = new File(filePath);
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(HTTP + AwsUtils.getPublicIpAddressByName(ec2, instanceName))
        .contentType(ContentType.MULTIPART)
        .multiPart("upfile", fileToUpload)
        .when()
        .post(ApiGateEndpoints.S3_BUCKET_UPLOAD_IMAGE.getValue())
        .then()
        .log().all()
        .extract()
        .response();
  }

  public static Response getAllImagesMetadata(Ec2Client ec2, String instanceName) {
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(HTTP + AwsUtils.getPublicIpAddressByName(ec2, instanceName))
        .when()
        .get(ApiGateEndpoints.S3_BUCKET_GET_ALL_IMAGES_METADATA.getValue())
        .then()
        .log().all()
        .extract()
        .response();
  }
}
