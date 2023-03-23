package com.epam.cloudx.tests.deploymentValidation;

import com.epam.cloudx.utils.AwsUtils;
import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GetEc2Instances extends BaseTest {

    @Test
    public void checkIsInstanceRunning() throws Exception {
        String expectedState = "running";

        String publicInstanceName = "cloudxinfo/PublicInstance/Instance";
        String actualState = AwsUtils.getInstanceStateByName(publicInstanceName, ec2);
        Assertions.assertEquals(actualState, expectedState,
            String.format("Actual instance state is %s.", actualState));
    }
}