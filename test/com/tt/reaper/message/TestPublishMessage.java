package com.tt.reaper.message;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tt.reaper.ReaperLogger;

public class TestPublishMessage {
	
	@Test
	public void testInit()
	{
		ReaperLogger.init();
		PublishMessage sot = new PublishMessage("data");
		assertTrue(sot.getStatus());
		String strMessage = sot.getRequest().toString();
		assertTrue(strMessage.contains("PUBLISH sip:collector@127.0.0.3:5060;transport=udp SIP/2.0"));
		assertTrue(strMessage.contains("Call-ID: "));
		assertTrue(strMessage.contains("CSeq: 1 PUBLISH"));
		assertTrue(strMessage.contains("From: \"reaper\" <sip:reaper@127.0.0.2:5060>;tag=ReaperV1.0"));
		assertTrue(strMessage.contains("To: \"collector\" <sip:collector@127.0.0.3:5060>"));
		assertTrue(strMessage.contains("Via: SIP/2.0/UDP 127.0.0.2:5060"));
		assertTrue(strMessage.contains("Max-Forwards: 70"));
		assertTrue(strMessage.contains("Contact: \"reaper\" <sip:reaper@127.0.0.2:5060>"));
		assertTrue(strMessage.contains("Content-Type: application/vq-rtcpxr"));
		assertTrue(strMessage.contains("Content-Length: 4"));
		assertTrue(strMessage.contains("data"));
	}
}
