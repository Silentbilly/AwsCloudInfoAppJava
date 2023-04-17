package com.epam.cloudx.suiteRunners.tests.ec2.securityGroupsConfigurationsTests;

import com.epam.cloudx.objects.cloudxinfo.AppInfo;
import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;

public class PublicInstanceApplicationFunctionalValidationTest extends CloudxInfoBaseTest {

  @Test
  @DisplayName("Public instance application functional validation")
  @Tag("public")
  public void getApiForPublicInstance() {
    var file = new File("src/main/resources/data/json/appInfoPublic.json");
    var response = HttpUtils.getPublicAppInfo(ec2, publicInstanceName);
    var actualResponse = JsonUtils.readJsonAsObject(response, AppInfo.class);
    var expectedResponse = JsonUtils.readJsonFileAsObject(file, AppInfo.class);
    Assertions.assertEquals(actualResponse, expectedResponse);
  }
}
