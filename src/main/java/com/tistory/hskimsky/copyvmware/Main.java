package com.tistory.hskimsky.copyvmware;

import com.tistory.hskimsky.core.AbstractJob;
import com.tistory.hskimsky.util.NativeUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class Main extends AbstractJob {

  public static final String FUSION_VM_NAME_POSTFIX = ".vmwarevm";

  private boolean isMac;
  private String sourcePath;
  private String sourceVMName;
  private String targetPath;
  private String autoConfPath;
  private List<String> targetVMNames;
  private List<String> targetVMIPs;
  private String targetDomain;
  private String encoding;
  private ExecutorService executor;

  public static void main(String[] args) throws Exception {
    int result = new Main().run(args);
    System.exit(result);
  }

  public int run(String[] args) throws Exception {
    // addOption("sourcePath", "sp", "원본 VM 경로", "H:\\vm\\Linux");
    addOption("sourcePath", "sp", "원본 VM 경로", "/Users/cloudine/Documents/Virtual Machines.localized");
    // addOption("sourceVMName", "sv", "원본 VM 이름", "centos_6.7_template");
    addOption("sourceVMName", "sv", "원본 VM 이름", "template67");
    // addOption("targetPath", "tp", "타겟 VM 경로", "H:\\vm\\Linux");
    addOption("targetPath", "tp", "타겟 VM 경로", "/Users/cloudine/Documents/Virtual Machines.localized");
    addOption("autoConfPath", "acp", "자동 설정 경로", "/Users/cloudine/Documents/Virtual Machines.localized/conf");
    addOption("targetVMNames", "tvs", "타겟 VM 이름들 (comma separated)", true);
    addOption("targetVMIPs", "tvi", "타겟 VM IP들 (comma separated)", true);
    addOption("targetDomain", "td", "타겟 VM domain", true);
    addOption("encoding", "e", "file encoding", "UTF-8");
    Map<String, String> params = parseArguments(args);

    if (params == null || params.size() == 0) {
      return APP_FAIL;
    }

    this.isMac = NativeUtils.getOS() == NativeUtils.OS.MAC;
    this.sourcePath = params.get(keyFor("sourcePath"));
    this.sourceVMName = params.get(keyFor("sourceVMName")) + (this.isMac ? FUSION_VM_NAME_POSTFIX : "");
    this.targetPath = params.get(keyFor("targetPath"));
    this.autoConfPath = params.get(keyFor("autoConfPath"));
    this.targetVMNames = Arrays.asList(params.get(keyFor("targetVMNames")).split(","));
    this.targetVMIPs = Arrays.asList(params.get(keyFor("targetVMIPs")).split(","));
    this.targetDomain = params.get(keyFor("targetDomain"));
    this.encoding = params.get(keyFor("encoding"));

    if (this.targetVMIPs.size() != this.targetVMNames.size()) {
      System.err.println("target vm ip count = " + this.targetVMIPs.size() + ", target vm hostname count = " + this.targetVMNames.size());
      return APP_FAIL;
    }

    int vmCount = this.targetVMNames.size();
    System.out.println("copy vm Count = " + vmCount);
    this.executor = Executors.newFixedThreadPool(vmCount * 2);

    execute();

    return APP_SUCCESS;
  }

  private void execute() throws IOException, InterruptedException {
    File source = new File(this.sourcePath, this.sourceVMName);
    if (!source.exists()) {
      throw new FileNotFoundException("Source path '" + source + "' does not exists.");
    }

    List<Runnable> startThreads = new ArrayList<>();
    // for (String targetVMName : this.targetVMNames) {
    int end = this.targetVMNames.size();
    for (int i = 0; i < end; i++) {
      String targetVMIP = this.targetVMIPs.get(i).trim();
      String targetVMName = this.targetVMNames.get(i).trim();
      File target = new File(this.targetPath, targetVMName + (this.isMac ? FUSION_VM_NAME_POSTFIX : ""));
      File vmConfDir = new File(this.autoConfPath, targetVMName + (this.isMac ? FUSION_VM_NAME_POSTFIX : ""));
      if (target.exists()) {
        System.err.println("Target path '" + target + "' already exists.");
        System.err.print("Did you overwrite? ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().toLowerCase();
        switch (input) {
          case "y":
          case "yes":
            target.delete();
            vmConfDir.delete();
            System.err.println(target + " is deleted!!");
            System.err.println(vmConfDir + " is deleted!!");
            break;
          default:
            throw new IllegalArgumentException("Target path '" + target + "' already exists.");
        }
      }
      target.mkdirs();
      vmConfDir.mkdirs();

      File ipFile = new File(vmConfDir, "IPADDR");
      File hostnameFile = new File(vmConfDir, "HOSTNAME");
      IOUtils.write(targetVMIP, new FileOutputStream(ipFile), this.encoding);
      IOUtils.write(targetVMName + "." + this.targetDomain, new FileOutputStream(hostnameFile), this.encoding);

      CopyNMoveThread copyNMoveThread = new CopyNMoveThread(this.isMac, source, target, this.autoConfPath, this.encoding);
      CheckThread checkThread = new CheckThread(copyNMoveThread.getSourceSize(), target);

      startThreads.add(copyNMoveThread);
      startThreads.add(checkThread);
    }
    long startTime = System.nanoTime();
    startThreads.forEach(this.executor::execute);
    this.executor.shutdown();
    this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

    System.out.println("All VMs copy end.");
    long endTime = System.nanoTime();
    System.out.printf("%s copy elapsed = %d (ms)\n", "All VMs", (endTime - startTime) / 1000000);
  }
}