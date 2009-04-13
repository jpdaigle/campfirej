package ca.softwareengineering.campfirej.http;

import static ca.softwareengineering.campfirej.impl.Constants.USER_AGENT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.softwareengineering.campfirej.CampfireException;

public class SimpleClient {

	Logger log = Logger.getLogger(this.getClass().getName());

	List<String> _cookies = new ArrayList<String>();

	public SimpleClient() {
	}

	private void storeResponseCookies(Map<String, List<String>> fields) {
		for (Entry<String, List<String>> e : fields.entrySet()) {
			String key = e.getKey();
			List<String> val = e.getValue();

			if ("Set-Cookie".equals(key)) {
				log.log(Level.FINEST, "Got cookie " + val);
				// Ignore metadata, just take first chunk
				String c1 = val.get(0).split(";")[0];
				_cookies.add(c1);
			}
		}
	}

	private String getRequestCookies() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = _cookies.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	public String doRequest(Request r) throws IOException, CampfireException {
		log.log(Level.FINE, "doRequest: " + r.toString());
		URL url = new URL(r.address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("User-Agent", USER_AGENT);
		String cookies;
		if ((cookies = getRequestCookies()).length() > 0) {
			conn.setRequestProperty("Cookie", cookies);
		}
		conn.setDoInput(true);
		if (r.rtype == RTYPE.POST) {
			conn.setDoOutput(true);
			conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			DataOutputStream os = new DataOutputStream(conn.getOutputStream());
			os.write(getFormPostBody(r.formFields).getBytes("UTF-8"));
		}
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = bufReader.readLine()) != null) {
			sb.append(line);
		}
		bufReader.close();
		storeResponseCookies(conn.getHeaderFields());

		/*
		 * The Java HttpURLConnection doesn't keep our cookies / user agent when
		 * it follows redirects, so we have to follow redirects manually.
		 * 
		 * Using r.followingRedir field to prevent infinite recursion
		 */
		int resp_code = conn.getResponseCode();
		if (!r.followingRedir
				&& (resp_code == HttpURLConnection.HTTP_MOVED_PERM || resp_code == HttpURLConnection.HTTP_MOVED_TEMP)) {
			String location = conn.getHeaderField("Location");
			if (location != null && location.length() > 0) {
				Request followReq = new Request();
				followReq.address = location;
				followReq.rtype = RTYPE.GET;
				return doRequest(followReq);
			}
		}

		if (resp_code != HttpURLConnection.HTTP_OK) {
			throw new CampfireException(String.format("Response code '%s' not 200 OK", resp_code));
		}
		return sb.toString();
	}

	public enum RTYPE {
		GET, POST
	}

	/*
	 * data is a map for formkey:formvalue
	 * 
	 * We URLEncode keys and values and separate them with '&'.
	 */
	static String getFormPostBody(Map<String, String> data) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> e : data.entrySet()) {
			try {
				String key = URLEncoder.encode(e.getKey(), "UTF-8");
				String val = URLEncoder.encode(e.getValue(), "UTF-8");
				sb.append(sb.length() == 0 ? "" : "&");
				sb.append(key).append("=").append(val);
			} catch (UnsupportedEncodingException uee) {
				// Can't happen, UTF-8 required to be supported
			}
		}
		return sb.toString();
	}

	public static class Request {
		public String address;
		public RTYPE rtype = RTYPE.GET;
		public Map<String, String> formFields = new HashMap<String, String>();
		public boolean followingRedir = false;

		public Request() {
		}

		@Override
		public String toString() {
			return String.format("Addr:'%s'", address);
		}
	}
}
