package cn.com.navia.sdk.utils;

import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class NetUtil {
	private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

	public static final int HTTP_OK = 200;

	public static final int TIMEOUT_MILLS = 5000;

	private static CloseableHttpResponse httpRequest(String uri, NameValuePair... args) throws IOException,
			URISyntaxException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder uriBuilder = new URIBuilder(URI.create(uri));
		if (args != null) {
			uriBuilder.setParameters(args);
		}
		URI httpUri = uriBuilder.build();
		HttpUriRequest request = new HttpGet(httpUri);
		request.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_MILLS);
		request.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_MILLS);
		CloseableHttpResponse execute = httpClient.execute(request);
		logger.info("httpRequest:{}", httpUri, execute.getStatusLine().toString());
		return execute;
	}

	public static String httpGET(String uri, NameValuePair... args) throws IOException, URISyntaxException {
		CloseableHttpResponse httpRequest = httpRequest(uri, args);
		return getContent(httpRequest, HTTP_OK);
	}

	private static byte[] getBytes(CloseableHttpResponse resp, int statusCode) throws IOException {
		byte[] retval = null;
		int _statusCode = resp.getStatusLine().getStatusCode();
		if (_statusCode == statusCode) {
			HttpEntity httpEntity = resp.getEntity();
			// long contentLength = httpEntity.getContentLength();
			// if (contentLength > 1) {
			retval = EntityUtils.toByteArray(httpEntity);
			// }
		}
		HttpClientUtils.closeQuietly(resp);
		return retval;
	}

	private static String getContentType(CloseableHttpResponse resp) {
		HttpEntity httpEntity = resp.getEntity();
		return httpEntity.getContentType().getValue();
	}

	public static String getContent(CloseableHttpResponse resp, int statusCode) throws IOException {
		String content = null;
		String contentType = getContentType(resp);
		byte[] bytes = getBytes(resp, statusCode);
		if (bytes != null && contentType != null && contentType.trim().length() > 0) {
			if (contentType.toLowerCase(Locale.CANADA).contains("text")) {
				String[] splits = contentType.split(";");
                if(splits.length > 1){
                    String[] hCharset = splits[1].split("=");
                    if (hCharset.length > 1 && hCharset[1]  != null) {
                        content = new String(bytes, hCharset[1]);
                    }
                }
            }
		}

		return content;
	}

	public static File downloadFile(String uri, File specsDir, NameValuePair... args) throws IOException,
			URISyntaxException {
		File f = null;
		CloseableHttpResponse response = httpRequest(uri, args);

		String fileName = getFileName(response);
		if (TextUtils.isEmpty(fileName)) {
			return f;
		}

		Header conn = response.getFirstHeader("Connection");

		boolean was_conn = (conn != null && conn.getValue().equalsIgnoreCase("keep-alive"));

		if (was_conn) {
			if (!specsDir.exists()) {
				specsDir.mkdirs();
			}

			if (specsDir.exists()) {
				f = new File(specsDir, fileName);
				logger.info("open:{}", f.getAbsoluteFile());
				if (!f.exists()) {
					f.createNewFile();
				}
				HttpEntity httpEntity = response.getEntity();
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
				BufferedInputStream bis = new BufferedInputStream(httpEntity.getContent());
				try {
					while (true) {
						int oneByte = bis.read();
						if (oneByte == -1) {
							break;
						}
						bos.write(oneByte);
					}
				} catch (IOException e) {
					throw e;
				} finally {
					bis.close();
					bos.close();
				}
			}
		}
		return f;
	}

	private static String getFileName(CloseableHttpResponse response) {
		String n = null;
		Header cdHeader = response.getLastHeader("Content-Disposition");
		if (cdHeader != null) {
			n = cdHeader.getValue().split(";")[1].split("\"")[1];
		}
		return n;
	}

}
