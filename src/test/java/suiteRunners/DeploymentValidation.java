package suiteRunners;
import com.epam.cloudx.tests.deploymentValidation.GetEc2InstancesTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(GetEc2InstancesTest.class)
public class DeploymentValidation {

}
