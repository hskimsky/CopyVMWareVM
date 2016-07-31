package com.tistory.hskimsky.copyvmware;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class CopyNMoveThread extends Thread {

    private File source;

    private File target;

    private String encoding;

    private long sourceSize;

    public CopyNMoveThread(File source, File target, String encoding) {
        this.source = source;
        this.target = target;
        this.encoding = encoding;

        this.sourceSize = FileUtils.sizeOfDirectory(source);
    }

    @Override
    public void run() {
        try {
            copyVM();
            rename();
            updateContents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyVM() throws IOException {
        System.out.println(this.getName() + " copy start!!");
        long startTime = System.nanoTime();
        // except log files
        FileUtils.copyDirectory(this.source, this.target, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.getName().endsWith(".log");
            }
        });
        long endTime = System.nanoTime();
        long elapsedMillis = (endTime - startTime) / 1000000;
        System.out.printf("%s copy elapsed = %d (ms)\n", this.getName(), elapsedMillis);
        System.out.println(this.getName() + " copy end!!");

        createSuccessFile();
    }

    private void createSuccessFile() throws IOException {
        new File(this.target, "_SUCCESS").createNewFile();
    }

    private void rename() {
        File[] targetLists = this.target.listFiles();
        for (File file : targetLists) {
            String originalName = file.getName();
            String targetName = StringUtils.replace(originalName, this.source.getName(), this.target.getName());
            file.renameTo(new File(file.getParent() + System.getProperty("file.separator") + targetName));
        }
    }

    private void updateContents() throws IOException {
        final String targetVMName = this.target.getName();
        File[] targetLists = this.target.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (String.format("%s.vmdk", targetVMName)).equals(name) || name.endsWith(".vmx") || name.endsWith(".vmxf");
            }
        });
        for (File file : targetLists) {
            String content = IOUtils.toString(new FileInputStream(file), this.encoding);
            String updatedContent = StringUtils.replace(content, this.source.getName(), this.target.getName());
            IOUtils.write(updatedContent, new FileOutputStream(file), this.encoding);
        }
    }

    public long getSourceSize() {
        return sourceSize;
    }
}