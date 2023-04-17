package com.epam.cloudx.suiteRunners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.epam.cloudx.suiteRunners.tests"})
@IncludeTags({"s3"})
@Suite
public class S3TestRunner {

}
