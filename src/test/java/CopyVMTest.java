import com.tistory.hskimsky.copyvmware.Main;
import com.tistory.hskimsky.util.NativeUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    @Test
    public void spaceDirRead() throws IOException {
        File file = new File("/Users/cloudine/Documents/Virtual Machines.localized/template.vmwarevm/template.vmdk");
        int bufferSize = 4 * 1024;

        BufferedReader br = new BufferedReader(new FileReader(file), bufferSize);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println("line = " + line);
        }

        br.close();
    }

    @Test
    public void fusionVMName() {
        String vmName = "template.vmwarevm";
        System.out.println(vmName.substring(0, vmName.length() - Main.FUSION_VM_NAME_POSTFIX.length()));
    }
}