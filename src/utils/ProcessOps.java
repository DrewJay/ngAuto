package utils;

import java.io.IOException;

public class ProcessOps {

	/**
	 * Create process by executing exe file with parameters.
	 * 
	 * @param {String} processLink - cmd process link
	 * @param {String} processParams - comma separated process parameters
	 * @return {Process} - process object
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
	 * @param {String} name - name of process
	 * @param {String} extension - process extension
	 * @return {boolean} - success of operation
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
