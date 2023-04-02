package com.epam.cloudx.utils;

import static io.restassured.RestAssured.given;

import com.amazonaws.services.ec2.AmazonEC2;
import lombok.experimental.UtilityClass;
import io.restassured.response.Response;

@UtilityClass
public class HttpUtils {
  private final String HTTP = "http://";
  public static Response getPublicAppInfo(AmazonEC2 ec2, String instanceName) {
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

  public static Response getPrivateAppInfo(AmazonEC2 ec2, String instanceName) {
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
}
