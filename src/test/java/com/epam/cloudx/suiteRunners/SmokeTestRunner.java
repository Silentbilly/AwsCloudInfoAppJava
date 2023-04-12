package com.epam.cloudx.suiteRunners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.epam.cloudx.tests"})
@IncludeTags({"smoke", "s3"})
@Suite
public class SmokeTestRunner {

}
