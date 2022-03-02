package com.tistory.hskimsky.copyvmware;

import com.tistory.hskimsky.jcommander.CloneSpec;
import com.tistory.hskimsky.util.NativeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author Haneul Kim
 * @version 0.1
 */
@Slf4j
public class CopyNMoveThread implements Runnable {

  private final CloneSpec cloneSpec;
  private final File sourceDir;
  private final File targetDir;

  public CopyNMoveThread(CloneSpec cloneSpec) {
    this.cloneSpec = cloneSpec;
    this.sourceDir = new File(this.cloneSpec.getTemplatePath());
    this.targetDir = new File(this.cloneSpec.getPath(), this.cloneSpec.getDisplayName());
  }

  @Override
  public void run() {
    Thread.currentThread().setName(this.cloneSpec.getDisplayName());
    try {
      copyVM();
      rename();
      updateContents();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void copyVM() throws IOException {
    String displayName = this.targetDir.getName();
    log.info("{} copy start!!", displayName);
    long startTime = System.nanoTime();
    // except log files
    FileUtils.copyDirectory(this.sourceDir, this.targetDir, pathname -> !pathname.getName().endsWith(".log"));
    long endTime = System.nanoTime();
    long elapsedMillis = (endTime - startTime) / 1000000;
    log.info("{} copy elapsed = {} (ms)", displayName, elapsedMillis);
    log.info("{} copy end!!!!", displayName);

    createSuccessFile();
  }

  private void createSuccessFile() throws IOException {
    new File(this.targetDir, "_SUCCESS").createNewFile();
  }

  private void rename() {
    String sourceVMName = this.sourceDir.getName();
    String targetVMName = this.targetDir.getName();
    Arrays.stream(Objects.requireNonNull(this.targetDir.listFiles())).forEach(file -> {
      String originalName = file.getName();

      String targetName = StringUtils.replace(originalName, sourceVMName, targetVMName);
      file.renameTo(new File(file.getParent() + NativeUtils.FILE_SEPARATOR + targetName));
    });
  }

  private void updateContents() throws IOException {
    String targetVMName = this.targetDir.getName();
    File[] updateTargetLists = this.targetDir.listFiles((dir, name) ->
      (String.format("%s.vmdk", targetVMName)).equals(name) || name.endsWith(".vmx") || name.endsWith(".vmxf"));
    int numvcpus = this.cloneSpec.getNumvcpus();
    int coresPerSocket = this.cloneSpec.getCoresPerSocket();
    int memsize = this.cloneSpec.getMemsize();
    String description = this.cloneSpec.getDescription().replaceAll("\n", "|0D|0A");
    String sourceVMName = this.sourceDir.getName();
    Arrays.stream(Objects.requireNonNull(updateTargetLists)).forEach(file -> {
      try {
        String sourceContent = IOUtils.toString(new FileInputStream(file), Main.ENCODING);
        String tempContent1 = StringUtils.replace(sourceContent, sourceVMName, targetVMName);
        String tempContent2 = tempContent1.replaceAll("numvcpus = \"\\d+\"", "numvcpus = \"" + numvcpus + "\"");
        String tempContent3 = tempContent2.replaceAll("coresPerSocket = \"\\d+\"", "coresPerSocket = \"" + coresPerSocket + "\"");
        String tempContent4 = tempContent3.replaceAll("memsize = \"\\d+\"", "memsize = \"" + memsize + "\"");
        String tempContent5 = tempContent4.replaceAll("annotation = \".*\"", "annotation = \"" + description + "\"");
        String finalContent = StringUtils.replace(tempContent5, Main.VM_CONF_DIR, Main.VM_CONF_DIR + NativeUtils.FILE_SEPARATOR + targetVMName);
        IOUtils.write(finalContent, new FileOutputStream(file), Main.ENCODING);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
