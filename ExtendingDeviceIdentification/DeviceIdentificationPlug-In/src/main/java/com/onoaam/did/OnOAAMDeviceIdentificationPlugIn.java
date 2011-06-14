package com.onoaam.did;

import java.util.Map;

import com.bharosa.uio.processor.device.DeviceIdentificationProcessorBase;

/**
 * @author Paul Codding
 * 
 *         Sample Java Applet used to retrieve the user's computer's MAC
 *         address, as well as a handful of System properties to form a
 *         fingerprint. This applet uses the java.util.prefs.Preferences API to
 *         store/retrieve the rotating 'cookie equivalent' value.
 * 
 *         This Applet uses code examples from:
 *         http://www.mkyong.com/java/how-to-get-mac-address-in-java/
 */
public class OnOAAMDeviceIdentificationPlugIn extends
		DeviceIdentificationProcessorBase {

	@Override
	public Map<String, Object> getClientDataMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getClientType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDigitalCookie() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFingerPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFingerPrintType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getPluginHTML() {
		// TODO Auto-generated method stub
		return null;
	}

}
