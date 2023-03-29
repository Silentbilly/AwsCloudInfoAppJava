package com.epam.cloudx.tests.privateInstanceTests;

import com.epam.cloudx.Exceptions.ServiceUnavailableFromPublicException;
import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PrivateInstanceNotAvailableFromInternetTest extends BaseTest {
    @Test
    @DisplayName("Private instance is not available from internet")
    @Tag("private")
    public void privateInstanceIsNotAvailableFromInternet() {
        Assertions.assertThrows(ServiceUnavailableFromPublicException.class,
                () -> AwsUtils.getPublicIpAddressByName(privateInstanceName, ec2));
    }
}
