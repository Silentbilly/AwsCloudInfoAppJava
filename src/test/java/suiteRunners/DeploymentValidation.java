package suiteRunners;
import org.junit.platform.suite.api.*;

@SelectPackages({"com.epam.cloudx.tests.privateInstanceTests", "com.epam.cloudx.tests.publicInstanceTests",
        "com.epam.cloudx.tests.deploymentValidation"})
//@IncludeTags("public")
@Suite
public class DeploymentValidation {

}
