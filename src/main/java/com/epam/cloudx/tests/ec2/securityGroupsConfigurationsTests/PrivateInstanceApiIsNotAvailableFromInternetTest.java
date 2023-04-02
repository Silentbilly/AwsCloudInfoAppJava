package com.epam.cloudx.tests.ec2.securityGroupsConfigurationsTests;

import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PrivateInstanceApiIsNotAvailableFromInternetTest extends BaseTest {
    @Test
    @DisplayName("API for private instance is not available from internet")
    @Tag("private")
    public void getApiForPrivateInstance() {
        Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
                () -> HttpUtils.getPrivateAppInfo(ec2, privateInstanceName));
    }
}
