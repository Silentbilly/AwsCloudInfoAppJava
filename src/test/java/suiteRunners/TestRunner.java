package suiteRunners;
import org.junit.platform.suite.api.*;

@SelectPackages({"com.epam.cloudx.tests.privateInstanceTests", "com.epam.cloudx.tests.publicInstanceTests"})
@IncludeTags({"public", "private"})
@Suite
public class TestRunner {

}
