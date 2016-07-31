import com.tistory.hskimsky.copyvmware.Main;
import org.junit.Test;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class CopyVMTest {

    @Test
    public void runCopy() throws Exception {
        String[] args = {"--sourceVMName", "test", "--targetVMNames", "test1,test2"};
        new Main().run(args);
    }
}