package ca.softwareengineering.jcampfire.http;

import static ca.softwareengineering.jcampfire.impl.Constants.USER_AGENT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleClient {

	Logger log = Logger.getLogger(this.getClass().getName());

	List<String> _cookies = new ArrayList<String>();

	public SimpleClient() {
	}

	private void storeResponseCookies(Map<String, List<String>> fields) {
		for (Entry<String, List<String>> e : fields.entrySet()) {
			String key = e.getKey();
			List<String> val = e.getValue();
			log.log(Level.INFO, "   HDR>" + key + ":" + val);

			if ("Set-Cookie".equals(key)) {
				log.log(Level.INFO, "Got cookie " + val);
				// Ignore metadata
				_cookies.add(val.get(0));
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

	public String doRequest(Request r) throws IOException {
		log.log(Level.INFO, "doRequest: " + r.toString());
		URL url = new URL(r.address);
		URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", USER_AGENT);
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = bufReader.readLine()) != null) {
			sb.append(line);
		}
		bufReader.close();
		storeResponseCookies(conn.getHeaderFields());
		return sb.toString();
	}

	public static class Request {
		String address;

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		@Override
		public String toString() {
			return String.format("Addr:'%s'", address);
		}
	}
}
