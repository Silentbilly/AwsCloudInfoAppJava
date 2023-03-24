package suiteRunners;
import com.epam.cloudx.tests.deploymentValidation.GetEc2InstancesPrivateTest;
import com.epam.cloudx.tests.deploymentValidation.GetEc2InstancesPublicTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({GetEc2InstancesPublicTest.class, GetEc2InstancesPrivateTest.class})
public class DeploymentValidation {

}
