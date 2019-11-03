package utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class FileOps {

	/**
	 * Get URI path of JAR file.
	 * 
	 * @param cls Calee class
	 * @return Jar URI
	 * @throws URISyntaxException If URI can not be parsed
	 */
	public static URI getJarURI(Class<?> cls) throws URISyntaxException {

		final ProtectionDomain domain;
		final CodeSource source;
		final URL url;
		final URI uri;

		domain = cls.getProtectionDomain();
		source = domain.getCodeSource();
		url = source.getLocation();
		uri = url.toURI();

		return (uri);
	}

	/**
	 * Get URI path of resource in JAR file.
	 * 
	 * @param where Path where to search
	 * @param fileName Name of file
	 * @return Jar URI
	 * @throws ZipException If Zip creation went wrong
	 * @throws IOException If stream error occurs
	 */
	public static URI getFile(final URI where, final String fileName) throws ZipException, IOException {

		final File location;
		final URI fileURI;

		location = new File(where);

		if (location.isDirectory()) {
			fileURI = URI.create(where.toString() + fileName);
		} else {
			final ZipFile zipFile;

			zipFile = new ZipFile(location);

			try {
				fileURI = extract(zipFile, fileName);
			} finally {
				zipFile.close();
			}
		}

		return (fileURI);
	}

	/**
	 * Create tempfile from exe packed in JAR.
	 * 
	 * @param zipFile Zip file reference
	 * @param fileName Name of file
	 * @return Jar URI
	 * @throws IOException If stream error occurs
	 */
	private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {

		final File tempFile;
		final ZipEntry entry;
		final InputStream zipStream;
		OutputStream fileStream;

		tempFile = File.createTempFile(fileName, Long.toString(System.currentTimeMillis()));
		tempFile.deleteOnExit();
		entry = zipFile.getEntry(fileName);

		if (entry == null) {
			throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
		}

		zipStream = zipFile.getInputStream(entry);
		fileStream = null;

		try {
			final byte[] buf;
			int i;

			fileStream = new FileOutputStream(tempFile);
			buf = new byte[1024];
			i = 0;

			while ((i = zipStream.read(buf)) != -1) {
				fileStream.write(buf, 0, i);
			}
		} finally {
			close(zipStream);
			close(fileStream);
		}

		return (tempFile.toURI());
	}

	/**
	 * Close stream.
	 * 
	 * @param stream Close reading stream
	 */
	private static void close(final Closeable stream) {

		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
