package com.epam.cloudx.utils;

import static io.restassured.RestAssured.given;

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
}
