package suiteRunners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.epam.cloudx.tests.vpc"})
@IncludeTags({"vpc"})
@Suite
public class VpcTestRunner {

}
