package net.pms.util;

import java.lang.reflect.Field;

import com.sun.jna.Platform;

import org.jvnet.winp.WinProcess;
import org.jvnet.winp.NotWindowsException;

import net.pms.PMS;
import net.pms.io.Gob;

public class ProcessUtil {

    /* chocolateboy 2010-02-16: cleaned up process termination to prevent orphan processes */
	public static void destroy(Process p) {
		if (p != null) {
	    if (Platform.isWindows()) {
		/*
		 * This calls TerminateProcess() on the spawned process and all its children
		 * (from leaves to root). This is the best we can do on Windows,
		 * as it doesn't provide any other way to terminate a process.
		 * Killing the process and its children ensures no orphan
		 * processes are left behind.
		 */

		try {
		    WinProcess wp = new WinProcess(p);
		    PMS.debug("Killing the Windows process: " + wp.getPid());
		    /* Modification made by ExSport due to problems with unrelated processes termination */
		    Process process = Runtime.getRuntime().exec("taskkill /PID " + wp.getPid() + " /T /F");
		    new Gob(process.getErrorStream()).start();
		    new Gob(process.getInputStream()).start();
		    int exit = process.waitFor();
		    if (exit != 0) {
		    PMS.debug("\"taskkill /PID " + wp.getPid() + " /T /F\" not successful... process was obviously already terminated");
		    } else {
		      PMS.debug("\"taskkill /PID " + wp.getPid() + " /T /F\" successful !");
		    }
		} catch (Throwable e) { /* shouldn't happen */
		    p.destroy(); /* kill the process non-recursively; shouldn't get here */
		}
	    } else {
			/* get the PID - only used for logging i.e. not directly */
			if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
				try {
					Field f = p.getClass().getDeclaredField("pid");
					f.setAccessible(true);
					int pid = f.getInt(p);
					PMS.debug("Killing the Unix process: " + pid);
				} catch (Throwable e) {
				        PMS.info("Can't determine the Unix process ID: " + e.getMessage());
				}
			}

			/*
			 * Squashed bug - send Unix processes a TERM signal rather than KILL.
			 *
			 * destroy() sends the spawned process a TERM signal.
			 * This ensures the process is given the opportunity
			 * to shutdown cleanly. *Extremely* rare cases where this doesn't
			 * happen are less of a problem than extremely common cases
			 * where kill -9 (KILL) creates orphan processes. In the former
			 * case, the stubborn processes may still be shut down when PMS
			 * exits.
			 */

			p.destroy();
	    }
    }
  }
	
	public static String getShortFileNameIfWideChars(String name) {
		if (Platform.isWindows()) {
			return PMS.get().getRegistry().getShortPathNameW(name);
		}
		return name;
	}

}
