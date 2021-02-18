import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import utils.ProcessOps;
import utils.FileOps;
import utils.Generals;
import components.Dialog;

/**
 * Ngrok automatizer.
 * 
 * @author Andrej Noskaj.
 */
public class Execute {
	private static String port = "1234";
	private static String apiRoot = "https://jsonstream.herokuapp.com";
	private static String inspectUrl = "http://localhost:4040/inspect/http";
	private static String apiEndpoint = "/api/set/ngAutoStream";
	private static Dialog dialog = new Dialog("options", "ngAuto", "Stream url copied to clipboard.");

	/**
	 * Main execution part. Chained calls attempt to launch ngrok to stream your current localhost
	 * on remote address with various configurations. Process management, parsing, validations and
	 * HTTP requests are all performed here.
	 * 
	 * @param args Program arguments.
	 * @throws URISyntaxException If URI parsing went wrong.
	 * @throws ZipException If operations over Zip went wrong.
	 * @throws IOException If stream error occurs.
	 */
	public static void main(String[] args) throws URISyntaxException, ZipException, IOException {
		port = (args.length == 0) ? port : args[0];

		// Validate process and make sure no other is running.
		ProcessOps.killByName("ngrok", "exe");
		Generals.sleep(1);
		String crawlData = Generals.getHTML(inspectUrl);

		if (crawlData != null) {
			System.out.println("ngAuto is already running.");
			return;
		}

		// Get access to ngrok.exe and forge process.
		final URI uri = FileOps.getJarURI(Execute.class);
		final URI exe = FileOps.getFile(uri, "ngrok.exe");

		Process ngProc = ProcessOps.startProcess(exe.toString().replaceAll("file:/", ""), "http " + port + " -host-header='localhost:" + port + "'");
		ngProc.isAlive();

		Generals.sleep(5);

		// This is where parsing phase begins.
		crawlData = Generals.getHTML(inspectUrl);

		try {
			// Sniff generated urls from local information page.
			Pattern pattern = Pattern.compile("\"URL.*?io");
			Matcher match = pattern.matcher(crawlData);
			match.find();

			String finalUrl = match.group(0).substring(9).replaceAll("https", "http");
			
			// Stream results to public API.
			String encoded = URLEncoder.encode(finalUrl, "UTF-8");
			Generals.getHTML(apiRoot + apiEndpoint + encoded);
			
			// Copy url to clipboard and aknowledge success.
			Generals.clipboard(finalUrl);
			System.out.println("Streaming http://localhost:" + port + " on " + finalUrl + ".");
			
			dialog.setAction(0, finalUrl);
			dialog.show();
			
		} catch (Exception ParsingException) {
			System.out.println("Sorry, parsing went wrong...");
		}
	}
}
