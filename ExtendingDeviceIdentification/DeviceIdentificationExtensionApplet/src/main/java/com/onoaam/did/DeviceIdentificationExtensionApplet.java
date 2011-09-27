package com.onoaam.did;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * @author Paul Codding
 * 
 *         Sample Java Applet used to retrieve the user's computer's MAC
 *         address, as well as a handful of System properties to form a
 *         fingerprint. This applet uses the java.util.prefs.Preferences API to
 *         store/retrieve the rotating 'cookie equivalent' value.
 * 
 *         This Applet uses MAC address access examples from:
 *         http://www.mkyong.com/java/how-to-get-mac-address-in-java/
 */
public class DeviceIdentificationExtensionApplet extends Applet {
	private static final long serialVersionUID = -3858195491918850286L;

	public static final String PLUG_IN_NAME = "OnOAAMApplet";
	public static final String ROTATING_COOKIE_NAME = "rotatingCookie";
	public static final String APPLET_PARAMETER_JSESSIONID = "jsessionid";

	// Fingerprint Data
	private String[] javaSystemPropertyNames = new String[] { "java.vendor",
			"os.name", "java.vm.specification.vendor", "java.runtime.version",
			"browser.version", "os.version", "java.vm.version",
			"java.vm.vendor" };
	private Map<String, String> fingerprintData;

	// Message to be displayed by the Applet
	private String appletMessage;

	// HTTP Connection used to POST the fingerprint
	private HttpURLConnection httpConnection;

	// Server URL to post the fingerprint to
	private String fpPostUrl = "http://www.onoaam.com/flashFingerprint.do";

	/*
	 * Initialize the Applet by collecting all of the information and posting it
	 * to the server.
	 */
	public void init() {
		super.init();
		// Initialize the HashMap that will hold the fingerprint data contents
		fingerprintData = new HashMap<String, String>();

		// Obtain the MAC Address and add it to the fingerprint data
		String macAddress = getMacAddress();
		if (macAddress != null)
			fingerprintData.put("device.macAddress", macAddress);

		// Obtain the Java System property data to be added to the fingerprint
		// data
		Properties properties = System.getProperties();
		for (int propertyNameIndex = 0; propertyNameIndex < javaSystemPropertyNames.length; propertyNameIndex++) {
			String key = javaSystemPropertyNames[propertyNameIndex];
			fingerprintData.put(key, properties.getProperty(key));
		}

		// Obtain the rotating 'cookie equivalent' value
		String rotatingCookieValue = getRotatingCookieValue();

		// Construct the final fingerprint with the rotating cookie appended
		String fpValue = "fp=" + constructFingerprint() + "&v=undefined"
				+ "&client=vfc&action=fp";

		// Post the fingerprint and retrieve the rotating cookie value
		String newCookieValue = postFingeprintAndRetrieveCookie(fpValue);

		// Set the rotating cookie value
		setRotatingCookieValue(newCookieValue);

		// Call OAAM Server's advancePage() JavaScript function
		callAdvancePage();
	}

	/**
	 * @return
	 */
	private String getRotatingCookieValue() {
		Preferences preferences = Preferences
				.userNodeForPackage(DeviceIdentificationExtensionApplet.class);
		String rotatingCookieValue = preferences.get(ROTATING_COOKIE_NAME,
				"undefined");
		return rotatingCookieValue;
	}

	/**
	 * 
	 */
	private void setRotatingCookieValue(String newRotatingCookieValue) {
		// avoid setting the cookie if no new cookie is given
		if (newRotatingCookieValue != null
				& newRotatingCookieValue.length() > 0) {
			Preferences preferences = Preferences
					.userNodeForPackage(DeviceIdentificationExtensionApplet.class);
			preferences.put(ROTATING_COOKIE_NAME, newRotatingCookieValue);
		}
	}

	/**
	 * Constructs the list of data/values that are collected to uniquely
	 * identify a client device. to be used as the value for the fp URL
	 * parameter.
	 * 
	 * @return The custom fingerprint value as a {@code String}
	 */
	private String constructFingerprint() {
		StringBuffer fingerprint = new StringBuffer();
		for (String key : fingerprintData.keySet())
			fingerprint.append(key + "=" + fingerprintData.get(key) + "&");
		try {
			return URLEncoder.encode(fingerprint.toString(), "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return new String();
		}
	}

	/**
	 * Obtain the MAC address of a user's computer if accessible.
	 * 
	 * @return The MAC address of the user's currently active network interface
	 *         as a {@code String}
	 */
	private String getMacAddress() {
		String macAddress = new String();
		try {
			InetAddress currentIp = null;
			currentIp = InetAddress.getLocalHost();
			NetworkInterface network = NetworkInterface
					.getByInetAddress(currentIp);
			byte[] mac = network.getHardwareAddress();
			if (mac != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++) {
					sb.append(String.format("%02X%s", mac[i],
							(i < mac.length - 1) ? ":" : ""));
				}
				macAddress = sb.toString();
				appletMessage = "MAC Address: " + macAddress;
			} else {
				appletMessage = "MAC Address not accessible";
				System.err.println(appletMessage);
			}
		} catch (Exception e) {
			appletMessage = "Error: " + e.getMessage();
		}
		return macAddress;
	}

	private String postFingeprintAndRetrieveCookie(String fpValue) {
		String newCookieValue = new String();
		StringBuffer response = null;

		try {
			URL serverAddress = new URL(fpPostUrl);

			// Setup the connection
			httpConnection = (HttpURLConnection) serverAddress.openConnection();
			httpConnection.setRequestMethod("POST");
			httpConnection.setReadTimeout(10000); // 10 seconds
			httpConnection.setUseCaches(false);
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);

			// Setup the POST data
			httpConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			httpConnection.setRequestProperty("Content-Length",
					"" + Integer.toString(fpValue.getBytes().length));
			httpConnection.setRequestProperty("Content-Language", "en-US");

			// Add the JSESSIONID cookie to the request
			String jSessionIdValue = retrieveJsessionId();
			if (jSessionIdValue != null && jSessionIdValue.length() > 0)
				httpConnection.setRequestProperty("Cookie", "JSESSIONID="
						+ jSessionIdValue);

			// Send request
			DataOutputStream writer = new DataOutputStream(
					httpConnection.getOutputStream());
			writer.writeBytes(fpValue);
			writer.flush();
			writer.close();

			// Get Response
			InputStream is = httpConnection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line;
			response = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (response != null) {
			String cleanResponse = response.toString().trim()
					.replaceAll("\r$", "");
			// The response will be in the form of &v={cookie value}
			String[] responsePieces = cleanResponse.split("=");
			if (responsePieces.length > 1)
				newCookieValue = responsePieces[1];
		} else
			newCookieValue = "undefined";
		return newCookieValue;
	}

	private void callAdvancePage() {
		try {
			// Call the OAAM Server's advancePage() JavaScript function
			getAppletContext()
					.showDocument(new URL("javascript:advancePage()"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private String retrieveJsessionId() {
		String jSessionId = getParameter("jsessionid");
		return jSessionId;
	}

	/*
	 * Paint the message variable on the screen (Web browser).
	 * 
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		this.setBackground(Color.WHITE);
		this.setSize(225, 28);
		g.drawString(appletMessage, 5, 20);
	}
}