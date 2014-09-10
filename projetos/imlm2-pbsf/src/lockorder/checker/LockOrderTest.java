package lockorder.checker;

import java.io.File;
import java.util.Collection;

import org.checkerframework.framework.test.ParameterizedCheckerTest;
import org.junit.runners.Parameterized.Parameters;

public class LockOrderTest extends ParameterizedCheckerTest {

    public LockOrderTest(File testFile) {
        super(testFile, LockOrderChecker.class, "lockorder.checker", "-Anomsgtext");
        //Use below to see more detailed error msgs.
//        super(testFile, LockOrderChecker.class, "lockorder.checker", "-AprintErrorStack");
    }

    @Parameters
    public static Collection<Object[]> data() {
        return testFiles("lockorder");
    }
}
