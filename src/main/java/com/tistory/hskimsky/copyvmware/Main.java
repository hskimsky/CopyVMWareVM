package com.tistory.hskimsky.copyvmware;

import com.tistory.hskimsky.core.AbstractJob;
import com.tistory.hskimsky.util.NativeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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

    private List<String> targetVMNames;

    private String encoding;

    private ExecutorService executor;

    private Map<String, String> params;

    public static void main(String[] args) throws Exception {
        int result = new Main().run(args);
        System.exit(result);
    }

    public int run(String[] args) throws Exception {
//        addOption("sourcePath", "sp", "원본 VM 경로", "H:\\vm\\Linux");
        addOption("sourcePath", "sp", "원본 VM 경로", "/Users/cloudine/Documents/Virtual Machines.localized");
//        addOption("sourceVMName", "sv", "원본 VM 이름", "centos_6.7_template");
        addOption("sourceVMName", "sv", "원본 VM 이름", "template");
//        addOption("targetPath", "tp", "타겟 VM 경로", "H:\\vm\\Linux");
        addOption("targetPath", "tp", "타겟 VM 경로", "/Users/cloudine/Documents/Virtual Machines.localized");
        addOption("targetVMNames", "tvs", "타겟 VM 이름들 (comma separated)", true);
        addOption("encoding", "e", "file encoding", "UTF-8");
        this.params = parseArguments(args);

        if (params == null || params.size() == 0) {
            return APP_FAIL;
        }

        this.isMac = NativeUtils.getOS() == NativeUtils.OS.MAC;
        this.sourcePath = this.params.get(keyFor("sourcePath"));
        this.sourceVMName = this.params.get(keyFor("sourceVMName")) + (this.isMac ? FUSION_VM_NAME_POSTFIX : "");
        this.targetPath = this.params.get(keyFor("targetPath"));
        this.targetVMNames = Arrays.asList(this.params.get(keyFor("targetVMNames")).split(","));
        this.encoding = this.params.get(keyFor("encoding"));

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
        for (String targetVMName : this.targetVMNames) {
            File target = new File(this.targetPath, targetVMName + (this.isMac ? FUSION_VM_NAME_POSTFIX : ""));
            if (target.exists()) {
                System.err.println("Target path '" + target + "' already exists.");
                System.err.print("Did you overwrite? ");

                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine().toLowerCase();
                switch (input) {
                    case "y":
                    case "yes":
                        target.delete();
                        System.err.println(target + " is deleted!!");
                        break;
                    default:
                        throw new IllegalArgumentException("Target path '" + target + "' already exists.");
                }
            }
            target.mkdirs();

            CopyNMoveThread copyNMoveThread = new CopyNMoveThread(this.isMac, source, target, this.encoding);
            CheckThread checkThread = new CheckThread(copyNMoveThread.getSourceSize(), target);

            startThreads.add(copyNMoveThread);
            startThreads.add(checkThread);

        }
        long startTime = System.nanoTime();
        /*for (Runnable thread : startThreads) {
            this.executor.execute(thread);
        }*/
        startThreads.forEach(this.executor::execute);
        this.executor.shutdown();
        this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        System.out.println("All VMs copy end.");
        long endTime = System.nanoTime();
        System.out.printf("%s copy elapsed = %d (ms)\n", "All VMs", (endTime - startTime) / 1000000);
    }
}