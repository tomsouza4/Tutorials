package com.onoaam.did;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.bharosa.uio.util.UIOContext;

public class OnOAAMDeviceIdentificationPlugInTest {
	OnOAAMDeviceIdentificationPlugIn plugIn = new OnOAAMDeviceIdentificationPlugIn();

	@Before
	public void setUp() throws Exception {
		// Setup Mock request
		MockHttpServletRequest request = new MockHttpServletRequest();
		// Setup fake fingerprint
		String[] fpDataNames = OnOAAMDeviceIdentificationPlugIn.fingerprintDataNames;
		StringBuffer mockFpParameter = new StringBuffer();
		for (int fpDataNamesIndex = 0; fpDataNamesIndex < fpDataNames.length; fpDataNamesIndex++)
			mockFpParameter.append(fpDataNames[fpDataNamesIndex] + "="
					+ Integer.toString(fpDataNamesIndex) + "&");
		request.addParameter("fp", mockFpParameter.toString());
		request.addParameter("v", "mockDigitalCookie");
		request.addParameter("client", "OnOAAMApplet");
		UIOContext.setCurrentInstance(new UIOContext(request,
				new MockHttpServletResponse()));
		plugIn.getFingerPrint();
	}

	@Test
	public void testGetClientDataMap() {
		//System.out.println(plugIn.getClientDataMap().keySet().size());
		assertTrue(plugIn.getClientDataMap().keySet() != null
				&& plugIn.getClientDataMap().keySet().size() == 9);
	}

	@Test
	public void testGetClientType() {
		//System.out.println(plugIn.getClientType());
		assertTrue(plugIn.getClientType() == 101);
	}

	@Test
	public void testGetDigitalCookie() {
		String digitalCookie = plugIn.getDigitalCookie();
		//System.out.println(digitalCookie);
		assertTrue(digitalCookie != null);
	}

	@Test
	public void testGetFingerPrint() {
		String fingerprint = plugIn.getFingerPrint();
		//System.out.println("Fingerprint: " + fingerprint);
		assertTrue(fingerprint != null && fingerprint.contains("java.vendor#^#"));
	}

	@Test
	public void testGetFingerPrintType() {
		//System.out.println(plugIn.getFingerPrintType();
		assertTrue(plugIn.getFingerPrintType() == 101);
	}

	@Test
	public void testGetPluginHTML() {
		System.out.println(plugIn.getPluginHTML());
		assertTrue(plugIn.getPluginHTML().contains("applet"));
	}

}
