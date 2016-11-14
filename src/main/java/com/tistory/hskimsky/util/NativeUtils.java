package com.tistory.hskimsky.util;

/**
 * description.
 *
 * @author Haneul, Kim
 */
public class NativeUtils {

    private static String os = System.getProperty("os.name").toLowerCase();

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
        return (os.indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (os.indexOf("mac") >= 0);
    }

    private static boolean isUnix() {
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0);
    }

    private static boolean isSolaris() {
        return (os.indexOf("sunos") >= 0);
    }
}
