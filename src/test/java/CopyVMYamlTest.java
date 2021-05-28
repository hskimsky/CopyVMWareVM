import com.beust.jcommander.JCommander;
import com.tistory.hskimsky.copyvmware.Main;
import com.tistory.hskimsky.jcommander.CloneSpec;
import com.tistory.hskimsky.jcommander.CloneSpecArgs;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @author hskimsky
 * @version 1.0
 * @since 2021-05-28
 */
public class CopyVMYamlTest {

  private static final Logger logger = LoggerFactory.getLogger(CopyVMYamlTest.class);

  @Test
  public void parseTest() {
    String[] args = {"--force", "--yaml", "F:\\projects\\CopyVMWareVM\\src\\main\\resources\\example.yaml"};
    CloneSpecArgs csArgs = new CloneSpecArgs();
    JCommander.newBuilder()
      .addObject(csArgs)
      .build()
      .parse(args);

    Assert.assertTrue(csArgs.isForce());

    List<CloneSpec> cloneSpecs = csArgs.getCloneSpecs();
    Assert.assertEquals(2, cloneSpecs.size());

    CloneSpec cloneSpec0 = cloneSpecs.get(0);
    Assert.assertEquals("E:\\vm\\linux\\template79large", cloneSpec0.getTemplatePath());
    Assert.assertEquals("hdm1.sky.local", cloneSpec0.getHostname());
    Assert.assertEquals("192.168.181.211", cloneSpec0.getIp());
    Assert.assertEquals("F:\\vm\\linux\\hdp", cloneSpec0.getPath());
    Assert.assertEquals(2, cloneSpec0.getCoresPerSocket());
    Assert.assertEquals(4, cloneSpec0.getNumvcpus());
    Assert.assertEquals(8192, cloneSpec0.getMemsize());

    CloneSpec cloneSpec1 = cloneSpecs.get(1);
    Assert.assertEquals("E:\\vm\\linux\\template79", cloneSpec1.getTemplatePath());
    Assert.assertEquals("hdw1.sky.local", cloneSpec1.getHostname());
    Assert.assertEquals("192.168.181.212", cloneSpec1.getIp());
    Assert.assertEquals("F:\\vm\\linux\\hdp", cloneSpec1.getPath());
    Assert.assertEquals(2, cloneSpec1.getCoresPerSocket());
    Assert.assertEquals(2, cloneSpec1.getNumvcpus());
    Assert.assertEquals(4096, cloneSpec1.getMemsize());
  }

  @Test
  public void mainTest() throws IOException, InterruptedException {
    String[] args = {"--force", "--yaml", "F:\\projects\\CopyVMWareVM\\src\\main\\resources\\example.yaml"};
    CloneSpecArgs csArgs = new CloneSpecArgs();
    JCommander.newBuilder()
      .addObject(csArgs)
      .build()
      .parse(args);

    Main main = new Main(csArgs.isForce(), csArgs.getCloneSpecs());
    main.start();
  }
}
