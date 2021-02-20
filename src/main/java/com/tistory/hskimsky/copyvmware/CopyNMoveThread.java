package com.tistory.hskimsky.copyvmware;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Haneul Kim
 * @version 0.1
 */
public class CopyNMoveThread implements Runnable {

  private final boolean isMac;
  private final File source;
  private final File target;
  private final String encoding;
  private final long sourceSize;
  private final String targetVMName;

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
    FileUtils.copyDirectory(this.source, this.target, pathname -> !pathname.getName().endsWith(".log"));
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
    Arrays.stream(Objects.requireNonNull(this.target.listFiles())).forEach(file -> {
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
    File[] updateTargetLists = this.target.listFiles((dir, name) -> (String.format("%s.vmdk", targetVMName)).equals(name) || name.endsWith(".vmx") || name.endsWith(".vmxf"));
    Arrays.stream(Objects.requireNonNull(updateTargetLists)).forEach(file -> {
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