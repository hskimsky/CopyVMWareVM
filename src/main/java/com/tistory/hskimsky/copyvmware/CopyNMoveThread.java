package com.tistory.hskimsky.copyvmware;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class CopyNMoveThread implements Runnable {

  private boolean isMac;

  private File source;

  private File target;

  private String encoding;

  private long sourceSize;

  private String targetVMName;

  public CopyNMoveThread(boolean isMac, File source, File target, String encoding) {
    this.isMac = isMac;
    this.source = source;
    this.target = target;
    this.encoding = encoding;

    this.sourceSize = FileUtils.sizeOfDirectory(source);

    this.targetVMName = this.target.getName() + " VM";
  }

  @Override
  public void run() {
    Thread.currentThread().setName(this.targetVMName);
    try {
      copyVM();
      rename();
      updateContents();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void copyVM() throws IOException {
    System.out.println(this.targetVMName + " copy start!!");
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
    System.out.printf("%s copy elapsed = %d (ms)\n", this.targetVMName, elapsedMillis);
    System.out.println(this.targetVMName + " copy end!!");

    createSuccessFile();
  }

  private void createSuccessFile() throws IOException {
    new File(this.target, "_SUCCESS").createNewFile();
  }

  private void rename() {
        /*File[] targetLists = this.target.listFiles();
        for (File file : targetLists) {
            String originalName = file.getName();

            String sourceVMName = getVMName(this.source.getName());
            String targetVMName = getVMName(this.target.getName());

            String targetName = StringUtils.replace(originalName, sourceVMName, targetVMName);
            file.renameTo(new File(file.getParent() + System.getProperty("file.separator") + targetName));
        }*/
    Arrays.stream(this.target.listFiles()).forEach(file -> {
      String originalName = file.getName();

      String sourceVMName = getVMName(this.source.getName());
      String targetVMName = getVMName(this.target.getName());

      String targetName = StringUtils.replace(originalName, sourceVMName, targetVMName);
      file.renameTo(new File(file.getParent() + System.getProperty("file.separator") + targetName));
    });
  }

  /**
   * remove postfix(.vmwarevm) in VMWare Fusion
   * return real vm name
   */
  private String getVMName(String name) {
    return this.isMac ? name.substring(0, name.length() - Main.FUSION_VM_NAME_POSTFIX.length()) : name;
  }

  private void updateContents() throws IOException {
    final String targetVMName = getVMName(this.target.getName());
    File[] updateTargetLists = this.target.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return (String.format("%s.vmdk", targetVMName)).equals(name) || name.endsWith(".vmx") || name.endsWith(".vmxf");
      }
    });
        /*for (File file : updateTargetLists) {
            String content = IOUtils.toString(new FileInputStream(file), this.encoding);
            String sourceVMName = getVMName(this.source.getName());
            String updatedContent = StringUtils.replace(content, sourceVMName, targetVMName);
            IOUtils.write(updatedContent, new FileOutputStream(file), this.encoding);
        }*/
    Arrays.stream(updateTargetLists).forEach(file -> {
      try {
        String content = IOUtils.toString(new FileInputStream(file), this.encoding);
        String sourceVMName = getVMName(this.source.getName());
        String updatedContent = StringUtils.replace(content, sourceVMName, targetVMName);
        IOUtils.write(updatedContent, new FileOutputStream(file), this.encoding);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public long getSourceSize() {
    return sourceSize;
  }
}