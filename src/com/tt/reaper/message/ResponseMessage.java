package com.tt.reaper.message;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPResponse;

import javax.sip.header.CallIdHeader;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

public class ResponseMessage  extends SipMessage {
	protected Response response;
	private Request request;
	
	protected ResponseMessage(int type, Response response)
	{
		super(type);
		this.response = response;
	}
	
	public ResponseMessage(Request request) {
		super(Message.SUCCESS);
		try {
			this.request = request;
			response = messageFactory.createResponse(200, request);
			ToHeader toHeader = (ToHeader)response.getHeader(ToHeader.NAME);
			toHeader.setTag("888");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResponseMessage(int type) {
		super(type);
		response = new SIPResponse();
	}

	public Response getResponse() {
		return response;
	}

	public Request getRequest() {
		return request;
	}
	
	public final String getCallId()
	{
		CallIdHeader header = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
		if (header == null)
			return "null";
		return header.getCallId();
	}
	
	public final SIPMessage getMessage() {
		return (SIPMessage)response;
	}
}
