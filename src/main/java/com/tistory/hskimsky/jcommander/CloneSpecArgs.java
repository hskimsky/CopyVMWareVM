package com.tistory.hskimsky.jcommander;

import com.beust.jcommander.Parameter;
import com.tistory.hskimsky.jcommander.converter.CloneSpecConverter;

import java.util.List;
import java.util.Objects;

/**
 * arguments for xen vm clone spec
 *
 * @author haneul.kim
 * @version 1.0
 * @since 2021-02-27
 */
public class CloneSpecArgs {

  @Parameter(names = {"-f", "--force"}, description = "이미 존재하는 경우 삭제")
  private boolean force;

  @Parameter(names = {"-y", "--yml", "--yaml"}, required = true, description = "xen vm clone spec yaml file path", listConverter = CloneSpecConverter.class)
  private List<CloneSpec> cloneSpecs;

  public CloneSpecArgs() {
  }

  public boolean isForce() {
    return force;
  }

  public List<CloneSpec> getCloneSpecs() {
    return cloneSpecs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CloneSpecArgs that = (CloneSpecArgs) o;
    return force == that.force && cloneSpecs.equals(that.cloneSpecs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(force, cloneSpecs);
  }

  @Override
  public String toString() {
    return "CloneSpecArgs{" +
      "force=" + force +
      ", cloneSpecs=" + cloneSpecs +
      '}';
  }
}
