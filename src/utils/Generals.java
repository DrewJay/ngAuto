package utils;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;

public class Generals {

	/**
	 * Ngrok creates status page on localhost:4040. I need to parse it in order
	 * to sniff the URL where our page is published.
	 * 
	 * @param targetUrl Target url we are parsing
	 * @return Stringified HTML
	 */
	public static final String getHTML(String targetUrl) {

		try {
			StringBuilder result = new StringBuilder();
			URL url = new URL(targetUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			reader.close();
			return result.toString();

		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Copy string to cliboard.
	 * 
	 * @param data Data to save
	 */
	public static final void clipboard(String data) {

		StringSelection stringSelection = new StringSelection(data);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	/**
	 * Pause main thread for a few seconds.
	 * 
	 * @param seconds Sleep length in seconds
	 */
	public static final void sleep(int seconds) {

		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException iex) {
			iex.printStackTrace();
		}
	}
	
	/**
	 * Open url in browser.
	 * 
	 * @param browser Browser code name
	 * @param url URL to open
	 */
	public static final boolean browserify(String browser, String url) {

		try {
			if(Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				Runtime runtime = Runtime.getRuntime();
				runtime.exec("/usr/bin/" + browser + " -new-window " + url);
			}

			return true;

		} catch(IOException|URISyntaxException excp) {
			return false;
		}
	}
}
