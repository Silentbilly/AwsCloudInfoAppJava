package com.epam.cloudx.suiteRunners;
import org.junit.platform.suite.api.*;

@SelectPackages({"com.epam.cloudx.suiteRunners.tests.ec2"})
@IncludeTags({"public", "private", "configuration"})
@Suite
public class Ec2TestRunner {

}
