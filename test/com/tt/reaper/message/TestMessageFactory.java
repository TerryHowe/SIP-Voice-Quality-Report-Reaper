package com.tt.reaper.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;

import java.text.ParseException;

import javax.sip.message.Request;

import org.junit.Test;

public class TestMessageFactory {
	public static final String CALL_ID = "woot";
	static MessageFactory sot = new MessageFactory();
	static SIPResponse response = new SIPResponse();
	static SIPRequest request = new SIPRequest();

	public static SuccessMessage createSuccessMessage()
	{
		try {
			response.setStatusCode(200);
			response.setCallId(CALL_ID);
		} catch (ParseException e) {
		}
		return (SuccessMessage)sot.create(response);
	}

	public static SuccessMessage createSuccessMessage(String ipAddy, int port)
	{
		SuccessMessage success = createSuccessMessage();
		success.setSdp(createSdp(ipAddy, port));
		return success;
	}
	
	public static SipMessage createProvisionalMessage() {
		try {
			response.setStatusCode(180);
			response.setCallId(CALL_ID);
		} catch (ParseException e) {
		}
		return (ProvisionalMessage)sot.create(response);
	}
	
	public static ErrorMessage createErrorMessage()
	{
		try {
			response.setStatusCode(400);
			response.setCallId(CALL_ID);
		} catch (ParseException e) {
		}
		return (ErrorMessage)sot.create(response);
	}
	
	public static InviteMessage createInviteMessage(String ipAddy, int port)
	{
		InviteMessage invite = new InviteMessage();
		invite.setSdp(createSdp(ipAddy, port));
		return invite;
	}
	
	public static AckMessage createAckMessage()
	{
		request.setMethod(Request.ACK);
		return (AckMessage)sot.create(request);
	}
	
	public static ByeMessage createByeMessage()
	{
		request.setMethod(Request.BYE);
		return (ByeMessage)sot.create(request);
	}
	
	public static CancelMessage createCancelMessage()
	{
		request.setMethod(Request.CANCEL);
		return (CancelMessage)sot.create(request);
	}
	
	public static String createSdp(String ipAddy, int port) {
		return 	"v=0\n" +
				"o=francisco 13004970 13013442 IN IP4 200.57.7.204\n" +
				"s=X-Lite\n" +
				"c=IN IP4 " + ipAddy + "\n" +
				"t=0 0\n" +
				"m=audio " + port + " RTP/AVP 8 0 3 98 97 101\n" +
				"a=rtpmap:0 pcmu/8000\n" +
				"a=rtpmap:8 pcma/8000\n" +
				"a=rtpmap:3 gsm/8000\n" +
				"a=rtpmap:98 iLBC/8000\n" +
				"a=rtpmap:97 speex/8000\n" +
				"a=rtpmap:101 telephone-event/8000\n" +
				"a=fmtp:101 0-15\n";
	}
	
	@Test
	public void testRequest()
	{
		request.setMethod(Request.ACK);
		assertTrue(sot.create(request) instanceof AckMessage);

		request.setMethod(Request.BYE);
		assertTrue(sot.create(request) instanceof ByeMessage);

		request.setMethod(Request.CANCEL);
		assertTrue(sot.create(request) instanceof CancelMessage);

		request.setMethod(Request.INFO);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.INVITE);
		assertTrue(sot.create(request) instanceof InviteMessage);

		request.setMethod(Request.MESSAGE);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.NOTIFY);
		assertTrue(sot.create(request) instanceof NotifyMessage);

		request.setMethod(Request.OPTIONS);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.PRACK);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.PUBLISH);
		assertTrue(sot.create(request) instanceof PublishMessage);

		request.setMethod(Request.REFER);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.REGISTER);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.SUBSCRIBE);
		assertEquals(null, sot.create(request));

		request.setMethod(Request.UPDATE);
		assertEquals(null, sot.create(request));

		request.setMethod("bogus");
		assertEquals(null, sot.create(request));
	}
	@Test
	public void testResponse() throws Exception
	{
		SIPResponse response = new SIPResponse();

		response.setStatusCode(180);
		assertTrue(sot.create(response) instanceof ProvisionalMessage);
		response.setStatusCode(199);
		assertTrue(sot.create(response) instanceof ProvisionalMessage);
		response.setStatusCode(200);
		assertTrue(sot.create(response) instanceof SuccessMessage);
		response.setStatusCode(299);
		assertTrue(sot.create(response) instanceof SuccessMessage);
		response.setStatusCode(300);
		assertTrue(sot.create(response) instanceof ErrorMessage);
		response.setStatusCode(404);
		assertTrue(sot.create(response) instanceof ErrorMessage);
	}
}
