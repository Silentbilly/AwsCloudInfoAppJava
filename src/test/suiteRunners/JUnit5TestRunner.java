package suiteRunners;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(com.epam.cloudx.tests.deploymentValidation.GetEc2Instances.class)
public class JUnit5TestRunner {

}
