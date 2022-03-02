package com.tistory.hskimsky.copyvmware;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Haneul Kim
 * @version 0.1
 */
@Slf4j
public class CheckThread implements Runnable {

  private final long sourceSize;
  private final File target;
  private final String targetVMName;

  public CheckThread(long sourceSize, File target) {
    this.sourceSize = sourceSize;
    this.target = target;
    this.targetVMName = this.target.getName() + " VM";
  }

  @Override
  public void run() {
    long targetSize = 0;
    File successFile = new File(this.target, "_SUCCESS");
    while (!successFile.exists()) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      targetSize = FileUtils.sizeOfDirectory(this.target);
      double progress = ((double) targetSize / (double) this.sourceSize) * 100;
      log.info("{} {} / {} = {}",
        this.targetVMName, String.format("%,d", targetSize), String.format("%,d", this.sourceSize), String.format("%.2f%%", progress));
    }
    successFile.delete();
  }
}
