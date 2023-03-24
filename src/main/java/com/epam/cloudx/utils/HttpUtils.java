package com.epam.cloudx.utils;

import static io.restassured.RestAssured.given;

import com.amazonaws.services.ec2.AmazonEC2;
import com.epam.cloudx.config.Config;
import lombok.experimental.UtilityClass;
import io.restassured.response.Response;

@UtilityClass
public class HttpUtils {
  private final String HTTP = "http://";
  public static Response getAppInfo(String instanceName, AmazonEC2 ec2) {
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(HTTP + AwsUtils.getPublicIpAddressByName(instanceName, ec2))
        .when()
        .get()
        .then()
        .log().all()
        .extract()
        .response();
  }
}
