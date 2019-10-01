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
	private static String apiEndpoint = "/api/set/ngAutoStream";
	private static Dialog dialog = new Dialog("options", "ngAuto", "Stream url copied to clipboard.");


	/**
	 * Main execution part. Chained calls attempt to launch ngrok to stream your current localhost
	 * on remote address with various configurations. Process management, parsing, validations and
	 * HTTP requests are all performed here.
	 * 
	 * @param args
	 * @throws URISyntaxException
	 * @throws ZipException
	 * @throws IOException
	 */

	public static void main(String[] args) throws URISyntaxException, ZipException, IOException {

		port = (args.length == 0) ? port : args[0];

		ProcessOps.killByName("ngrok", "exe");
		Generals.sleep(1);
		String crawlData = Generals.getHTML("http://localhost:4040/inspect/http");

		if (crawlData != null) {
			System.out.println("ngAuto is already running.");
			return;
		}

		final URI uri = FileOps.getJarURI(Execute.class);
		final URI exe = FileOps.getFile(uri, "ngrok.exe");

		Process ngProc = ProcessOps.startProcess(exe.toString().replaceAll("file:/", ""), "http " + port + " -host-header='localhost:" + port + "'");
		ngProc.isAlive();

		Generals.sleep(5);

		crawlData = Generals.getHTML("http://localhost:4040/inspect/http");

		try {
			Pattern pattern = Pattern.compile("\"URL.*?io");
			Matcher match = pattern.matcher(crawlData);
			match.find();

			String finalUrl = match.group(0).substring(9).replaceAll("https", "http");
			
			String encoded = URLEncoder.encode(finalUrl, "UTF-8");
			Generals.getHTML(apiRoot + apiEndpoint + encoded);
			
			Generals.clipboard(finalUrl);
			System.out.println("Streaming http://localhost:" + port + " on " + finalUrl + ".");
			
			dialog.setAction(0, finalUrl);
			dialog.show();
			
		} catch (Exception ex) {
			System.out.println("Sorry, parsing went wrong...");
		}
	}
}
