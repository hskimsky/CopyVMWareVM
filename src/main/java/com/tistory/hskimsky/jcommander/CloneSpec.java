package com.tistory.hskimsky.jcommander;

import lombok.Data;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * yaml 파일을 변환할 class
 *
 * @author haneul.kim
 * @version 1.0
 * @since 2021-05-28
 */
@Data
public class CloneSpec {

  private String templatePath;
  private String displayName;
  private String hostname;
  private String path;
  private String description;
  private String ip;
  private int numvcpus;
  private int coresPerSocket;
  private int memsize;

  public void validate() {
    if (coresPerSocket != 0 && numvcpus % coresPerSocket != 0) {
      throw new IllegalArgumentException("numvcpus % coresPerSocket must 0. current value is " + numvcpus % coresPerSocket);
    }
  }

  public File getSourceDir() {
    return new File(templatePath);
  }

  public long getSourceDirSize() {
    return FileUtils.sizeOfDirectory(this.getSourceDir());
  }

  public File getTargetDir() {
    return new File(path, displayName);
  }
}
