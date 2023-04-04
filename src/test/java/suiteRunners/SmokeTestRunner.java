package suiteRunners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.epam.cloudx.tests"})
@IncludeTags("smoke")
@Suite
public class SmokeTestRunner {

}
