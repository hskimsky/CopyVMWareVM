package com.tistory.hskimsky.util;

/**
 * @author Haneul, Kim
 */
public class NativeUtils {

  private static final String os = System.getProperty("os.name").toLowerCase();

  public static final String FILE_SEPARATOR = System.getProperty("file.separator");

  public enum OS {
    WINDOWS, MAC, UNIX, SOLARIS
  }

  public static OS getOS() {
    if (isWindows()) {
      return OS.WINDOWS;
    } else if (isMac()) {
      return OS.MAC;
    } else if (isUnix()) {
      return OS.UNIX;
    } else if (isSolaris()) {
      return OS.SOLARIS;
    } else {
      return null;
    }
  }

  private static boolean isWindows() {
    return os.contains("win");
  }

  private static boolean isMac() {
    return os.contains("mac");
  }

  private static boolean isUnix() {
    return os.contains("nix") || os.contains("nux") || os.indexOf("aix") > 0;
  }

  private static boolean isSolaris() {
    return os.contains("sunos");
  }
}
