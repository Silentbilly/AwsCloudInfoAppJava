package com.epam.cloudx.utils;

import static io.restassured.RestAssured.given;

import com.epam.cloudx.config.Config;
import lombok.experimental.UtilityClass;
import io.restassured.response.Response;

@UtilityClass
public class HttpUtils {
  public static Response getAppInfo() {
    return given().relaxedHTTPSValidation()
        .log().all()
        .baseUri(Config.getInstance().getHomeUrl())
        .when()
        .get()
        .then()
        .log().all()
        .extract()
        .response();
  }
}
