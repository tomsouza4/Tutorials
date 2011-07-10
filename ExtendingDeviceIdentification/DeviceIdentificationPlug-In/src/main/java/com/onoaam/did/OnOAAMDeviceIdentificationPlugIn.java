package com.onoaam.did;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.bharosa.common.logger.Logger;
import com.bharosa.uio.processor.device.DeviceIdentificationProcessorBase;
import com.bharosa.uio.util.UIOContext;
import com.bharosa.vcrypt.common.util.VCryptServletUtil;

/**
 * @author Paul Codding
 * 
 */
public class OnOAAMDeviceIdentificationPlugIn extends
		DeviceIdentificationProcessorBase {
	static Logger logger = Logger
			.getLogger(OnOAAMDeviceIdentificationPlugIn.class);
	public static final String PLUG_IN_NAME = "OnOAAMApplet";
	public static final int CLIENT_TYPE = 101;
	public static final int FINGERPRINT_TYPE = 101;
	public static final String FINGERPRINT_DELIMITER = "#^#";

	public static String[] fingerprintDataNames = new String[] { "java.vendor",
			"os.name", "java.vm.specification.vendor", "java.runtime.version",
			"browser.version", "os.version", "java.vm.version",
			"java.vm.vendor", "device.macAddress" };
	private String fingerprint;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getClientDataMap()
	 */
	public Map<String, Object> getClientDataMap() {
		Map<String, Object> submittedFingerPrintData = new HashMap<String, Object>();
		if (fingerprint != null) {
			String[] fingerprintPieces = fingerprint
					.split(FINGERPRINT_DELIMITER);
			for (int fingerprintDataNamesIndex = 0; fingerprintDataNamesIndex < fingerprintDataNames.length; fingerprintDataNamesIndex++) {
				String key = fingerprintDataNames[fingerprintDataNamesIndex];
				String value = null;
				for (int fingerprintPiecesIndex = 0; fingerprintPiecesIndex < fingerprintPieces.length; fingerprintPiecesIndex++) {
					if (fingerprintPieces[fingerprintPiecesIndex].equals(key))
						try {
							value = fingerprintPieces[fingerprintPiecesIndex + 1];
						} catch (ArrayIndexOutOfBoundsException e) {
							logger.error(e);
						}
				}
				submittedFingerPrintData.put(key, value);
			}
		} else
			logger.error("Fingerprint not set, was getFingerPrint() called?");

		return submittedFingerPrintData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getClientType()
	 */
	public int getClientType() {
		return CLIENT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getDigitalCookie()
	 */
	public String getDigitalCookie() {
		HttpServletRequest request = UIOContext.getCurrentInstance()
				.getRequest();
		String submittedDigitalCookie = request.getParameter("v");
		return submittedDigitalCookie;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getFingerPrint()
	 */
	public String getFingerPrint() {
		// Obtain the HttpServletRequest
		HttpServletRequest request = UIOContext.getCurrentInstance()
				.getRequest();

		// Obtain the fingerprint submitted by the applet
		String submittedFingerprint = request.getParameter("fp");

		if (submittedFingerprint != null) {
			/*
			 * Call VCryptServletUtil.getFlashFingerPrint(String client, String
			 * fpStr) to construct the fingerprint
			 * 
			 * Note: The 'vfc' is used for the client because it is the value
			 * that is expected by the method.
			 */
			fingerprint = VCryptServletUtil.getFlashFingerPrint("vfc",
					submittedFingerprint);
			if (logger.isDebugEnabled())
				logger.debug("Fingerprint: " + fingerprint);
		} else
			logger.error("Could not find fp value in the request");

		return fingerprint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getFingerPrintType()
	 * 
	 * This method returns the value that is defined as the
	 * vcrypt.fingerprint.type.enum.OnOAAMApplet=<integer> in the Environment
	 * Properties set in the OAAM Admin console
	 */
	public int getFingerPrintType() {
		return FINGERPRINT_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bharosa.uio.processor.device.DeviceIdentificationProcessorIntf#
	 * getPluginHTML()
	 */
	public String getPluginHTML() {
		String pluginHtml = "<applet alt=\"Java is disabled\" name=\"DeviceIdentificationExtensionApplet\" width=\"225\" height=\"28\" code=\"com.onoaam.did.DeviceIdentificationExtensionApplet\" codebase=\"applet\" archive=\"OnOAAMDeviceExtensionApplet.jar\"></applet>";
		return pluginHtml;
	}
}
