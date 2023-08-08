package com.epam.cloudx.suiteRunners.tests.ec2.securityGroupsConfigurationsTests;

import com.epam.cloudx.exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.suiteRunners.tests.CloudxInfoBaseTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class PrivateInstanceNotAvailableFromInternetTest extends CloudxInfoBaseTest {
    @Test
    @DisplayName("Private instance is not available from internet")
    @Tag("private")
    void privateInstanceIsNotAvailableFromInternet() {
        Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
                () -> AwsUtils.getPublicIpAddressByName(ec2, privateInstanceName));
    }
}
