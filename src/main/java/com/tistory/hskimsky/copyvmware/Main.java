package com.tistory.hskimsky.copyvmware;

import com.tistory.hskimsky.core.AbstractJob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class Main extends AbstractJob {

    private String sourcePath;

    private String sourceVMName;

    private String targetPath;

    private List<String> targetVMNames;

    private String encoding;

    private Map<String, String> params;

    public static void main(String[] args) throws Exception {
        int result = new Main().run(args);
        System.exit(result);
    }

    public int run(String[] args) throws Exception {
        addOption("sourcePath", "sp", "원본 VM 경로", "H:\\vm\\Linux");
        addOption("sourceVMName", "sv", "원본 VM 이름", "centos_6.7_template");
        addOption("targetPath", "tp", "타겟 VM 경로", "H:\\vm\\Linux");
        addOption("targetVMNames", "tvs", "타겟 VM 이름들 (comma separated)", true);
        addOption("encoding", "e", "file encoding", "UTF-8");
        params = parseArguments(args);

        if (params == null || params.size() == 0) {
            return APP_FAIL;
        }

        this.sourcePath = params.get(keyFor("sourcePath"));
        this.sourceVMName = params.get(keyFor("sourceVMName"));
        this.targetPath = params.get(keyFor("targetPath"));
        this.targetVMNames = Arrays.asList(params.get(keyFor("targetVMNames")).split(","));
        this.encoding = params.get(keyFor("encoding"));

        execute();

        return APP_SUCCESS;
    }

    private void execute() throws IOException, InterruptedException {
        File source = new File(this.sourcePath, this.sourceVMName);
        if (!source.exists()) {
            throw new IllegalArgumentException("Source path '" + source + "' does not exists.");
        }

        List<Thread> threads = new ArrayList<>();
        long startTime = System.nanoTime();
        for (String targetVMName : this.targetVMNames) {
            File target = new File(this.targetPath, targetVMName);
            if (target.exists()) {
                throw new IllegalArgumentException("Target path '" + target + "' already exists.");
            }
            target.mkdirs();

            CopyNMoveThread copyNMoveThread = new CopyNMoveThread(source, target, this.encoding);
            copyNMoveThread.setName(targetVMName + " VM");
            copyNMoveThread.start();

            CheckThread checkThread = new CheckThread(copyNMoveThread.getSourceSize(), target);
            checkThread.setName(targetVMName + " VM");
            checkThread.start();

            threads.add(copyNMoveThread);
            threads.add(checkThread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("All VMs copy end.");
        long endTime = System.nanoTime();
        System.out.printf("%s copy elapsed = %d (ms)\n", "All VMs", (endTime - startTime) / 1000000);
    }
}