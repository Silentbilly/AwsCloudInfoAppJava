package com.epam.cloudx.suiteRunners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.epam.cloudx.suiteRunners.tests.vpc"})
@IncludeTags({"vpc"})
@Suite
public class VpcTestRunner {

}
