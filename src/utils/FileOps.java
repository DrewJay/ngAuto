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
	 * @param {Class} cls - calee class
	 * @return {URI} - Jar URI
	 * @throws URISyntaxException
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
	 * @param {URI} where - path where to search
	 * @param {String} fileName - name of file
	 * @return {URI} - Jar URI
	 * @throws ZipException
	 * @throws IOException
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
	 * @param {ZipFile} zipFile - zip file reference
	 * @param {String} fileName - name of file
	 * @return {URI} - Jar Uri
	 * @throws IOException
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
	 * @param {Closeable} stream - close reading stream
	 * @return {void}
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
