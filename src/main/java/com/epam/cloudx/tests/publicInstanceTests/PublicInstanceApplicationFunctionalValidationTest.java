package com.epam.cloudx.tests.publicInstanceTests;

import com.epam.cloudx.objects.AppInfo;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

public class PublicInstanceApplicationFunctionalValidationTest extends BaseTest {

  @Test
  @DisplayName("Public instance application functional validation")
  @Tag("public")
  public void getApiForPublicInstance() {
    var file = new File("src/main/resources/data/json/appInfoPublic.json");
    var response = HttpUtils.getPublicAppInfo(publicInstanceName, ec2);
    var actualResponse = JsonUtils.readJsonAsObject(response, AppInfo.class);
    var expectedResponse = JsonUtils.readJsonFileAsObject(file, AppInfo.class);
    Assertions.assertEquals(actualResponse, expectedResponse);
  }
}
