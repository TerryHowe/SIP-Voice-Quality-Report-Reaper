package com.tt.reaper.message;

import javax.sip.message.Response;

public class ErrorMessage extends ResponseMessage {
	private ErrorMessage(Response response)
	{
		super(Message.FAILURE, response);
	}
	
	static SipMessage create(Response response)
	{
		if (response.getStatusCode() >= 300) {
			return new ErrorMessage(response);
		}
		return null;
	}
}
