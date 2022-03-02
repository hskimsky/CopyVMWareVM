package com.tistory.hskimsky.copyvmware;

import com.beust.jcommander.JCommander;
import com.tistory.hskimsky.jcommander.CloneSpec;
import com.tistory.hskimsky.jcommander.CloneSpecArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Haneul Kim
 * @version 0.1
 */
@Slf4j
public class Main {

  public static final String VM_CONF_DIR = "E:\\vm\\conf";
  public static final String ENCODING = "UTF-8";

  private final boolean force;
  private final List<CloneSpec> cloneSpecs;
  private final ExecutorService executor;

  public static void main(String[] args) throws IOException, InterruptedException {
    if (args.length == 0) {
      args = new String[]{
        "--force"
        , "--yaml", "F:\\projects\\CopyVMWareVM\\src\\main\\resources\\example.yaml"
      };
    }
    CloneSpecArgs csArgs = new CloneSpecArgs();
    JCommander.newBuilder()
      .addObject(csArgs)
      .build()
      .parse(args);

    Main main = new Main(csArgs.isForce(), csArgs.getCloneSpecs());
    main.start();
  }

  public Main(boolean force, List<CloneSpec> cloneSpecs) {
    this.force = force;
    this.cloneSpecs = cloneSpecs;
    this.executor = Executors.newFixedThreadPool(this.cloneSpecs.size() * 2);
  }

  public void start() throws IOException, InterruptedException {
    List<Runnable> startThreads = new ArrayList<>(this.cloneSpecs.size() * 2);
    for (CloneSpec cloneSpec : cloneSpecs) {
      File sourceDir = cloneSpec.getSourceDir();
      if (!sourceDir.exists()) {
        throw new FileNotFoundException("Source path '" + sourceDir + "' does not exists.");
      }

      File targetDir = cloneSpec.getTargetDir();
      File vmConfDir = new File(VM_CONF_DIR, cloneSpec.getDisplayName());
      if (targetDir.exists() && this.force) {
        FileUtils.deleteDirectory(targetDir);
        log.warn("{} is deleted!!", targetDir);
        FileUtils.deleteDirectory(vmConfDir);
        log.warn("{} is deleted!!", vmConfDir);
      }
      targetDir.mkdirs();
      vmConfDir.mkdirs();

      File ipFile = new File(vmConfDir, "IPADDR");
      File hostnameFile = new File(vmConfDir, "HOSTNAME");
      IOUtils.write(cloneSpec.getIp(), new FileOutputStream(ipFile), ENCODING);
      IOUtils.write(cloneSpec.getHostname(), new FileOutputStream(hostnameFile), ENCODING);

      CopyNMoveThread copyNMoveThread = new CopyNMoveThread(cloneSpec);
      CheckThread checkThread = new CheckThread(cloneSpec.getSourceDirSize(), targetDir);

      startThreads.add(copyNMoveThread);
      startThreads.add(checkThread);
    }
    long startTime = System.nanoTime();
    startThreads.forEach(this.executor::execute);
    this.executor.shutdown();
    this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

    log.info("All VMs copy end.");
    long endTime = System.nanoTime();
    log.info("All VMs copy elapsed = {} (ms)", (endTime - startTime) / 1000000);
  }
}
