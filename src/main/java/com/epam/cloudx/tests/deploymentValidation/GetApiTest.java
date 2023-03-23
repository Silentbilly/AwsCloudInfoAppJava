package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.objects.AppInfo;
import com.epam.cloudx.utils.HttpUtils;
import com.epam.cloudx.utils.JsonUtils;
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetApiTest {
  @Test
  public void getApiForPublicInstance() {
    var file = new File("src/main/resources/data/appInfoPublic.json");
    var response = HttpUtils.getAppInfo();
    var actualResponse = JsonUtils.readJsonAsObject(response, AppInfo.class);
    var expectedResponse = JsonUtils.readJsonFileAsObject(file, AppInfo.class);
    Assertions.assertEquals(actualResponse, expectedResponse);
  }
}
