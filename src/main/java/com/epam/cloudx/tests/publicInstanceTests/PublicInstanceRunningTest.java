package com.epam.cloudx.tests.publicInstanceTests;

import com.epam.cloudx.tests.BaseTest;
import com.epam.cloudx.tests.PublicInstanceTest;
import com.epam.cloudx.utils.AwsUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class PublicInstanceRunningTest extends BaseTest {
    @Test
    @DisplayName("Public instance is available from internet")
    @Tag("public")
    public void publicInstanceIsAvailableFromInternet() {
        boolean isPublicIpExisting = !AwsUtils.getPublicIpAddressByName(publicInstanceName, ec2).isEmpty();
        Assertions.assertTrue(isPublicIpExisting, "Public IP is empty");
    }
}
