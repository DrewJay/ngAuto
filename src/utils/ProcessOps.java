package utils;

import java.io.IOException;

public class ProcessOps {

	/**
	 * Create process by executing exe file with parameters.
	 * 
	 * @param processLink cmd process link
	 * @param processParams comma separated process parameters
	 * @return process object
	 */
	public static final Process startProcess(String processLink, String processParams) {

		try {
			return Runtime.getRuntime().exec(processLink + " " + processParams);
		} catch (IOException ioex) {
			return null;
		}
	}

	/**
	 * Kill process by name.
	 * 
	 * @param name name of process
	 * @param extension process extension
	 * @return success of operation
	 */
	public static final boolean killByName(String name, String extension) {

		try {
			Runtime.getRuntime().exec("taskkill /F /IM "+ name + "." + extension);
			return true;
		} catch(IOException exeption) {
			return false;
		}
	}
}
