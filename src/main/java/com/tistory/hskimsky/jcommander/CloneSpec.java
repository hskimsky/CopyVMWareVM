package com.tistory.hskimsky.jcommander;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

/**
 * yaml 파일을 변환할 class
 *
 * @author haneul.kim
 * @version 1.0
 * @since 2021-05-28
 */
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

  public String getTemplatePath() {
    return templatePath;
  }

  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getNumvcpus() {
    return numvcpus;
  }

  public void setNumvcpus(int numvcpus) {
    this.numvcpus = numvcpus;
  }

  public int getCoresPerSocket() {
    return coresPerSocket;
  }

  public void setCoresPerSocket(int coresPerSocket) {
    this.coresPerSocket = coresPerSocket;
  }

  public int getMemsize() {
    return memsize;
  }

  public void setMemsize(int memsize) {
    this.memsize = memsize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CloneSpec cloneSpec = (CloneSpec) o;
    return numvcpus == cloneSpec.numvcpus &&
      coresPerSocket == cloneSpec.coresPerSocket &&
      memsize == cloneSpec.memsize &&
      templatePath.equals(cloneSpec.templatePath) &&
      displayName.equals(cloneSpec.displayName) &&
      hostname.equals(cloneSpec.hostname) &&
      path.equals(cloneSpec.path) &&
      Objects.equals(description, cloneSpec.description) &&
      ip.equals(cloneSpec.ip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templatePath, displayName, hostname, path, description, ip, numvcpus, coresPerSocket, memsize);
  }

  @Override
  public String toString() {
    return "CloneSpec{" +
      "templatePath='" + templatePath + '\'' +
      ", displayName='" + displayName + '\'' +
      ", hostname='" + hostname + '\'' +
      ", path='" + path + '\'' +
      ", description='" + description + '\'' +
      ", ip='" + ip + '\'' +
      ", numvcpus=" + numvcpus +
      ", coresPerSocket=" + coresPerSocket +
      ", memsize=" + memsize +
      '}';
  }
}
