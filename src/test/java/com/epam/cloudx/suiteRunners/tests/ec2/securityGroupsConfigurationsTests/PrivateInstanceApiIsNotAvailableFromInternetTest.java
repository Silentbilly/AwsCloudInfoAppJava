package com.epam.cloudx.suiteRunners.tests.ec2.securityGroupsConfigurationsTests;

import com.epam.cloudx.exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import com.epam.cloudx.utils.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PrivateInstanceApiIsNotAvailableFromInternetTest extends CloudxInfoBaseTest {
    @Test
    @DisplayName("API for private instance is not available from internet")
    @Tag("private")
    void getApiForPrivateInstance() {
        Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
                () -> HttpUtils.getPrivateAppInfo(ec2, privateInstanceName));
    }
}
