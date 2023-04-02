package suiteRunners;
import org.junit.platform.suite.api.*;

@SelectPackages({"com.epam.cloudx.tests.ec2"})
@IncludeTags({"public", "private", "configuration"})
@Suite
public class Ec2TestRunner {

}
