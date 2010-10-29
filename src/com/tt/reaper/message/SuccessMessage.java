package com.tt.reaper.message;

import javax.sip.message.Response;

public class SuccessMessage extends ResponseMessage {
	private SuccessMessage(Response response)
	{
		super(Message.SUCCESS, response);
	}

	static SipMessage create(Response response)
	{
		int status = response.getStatusCode();
		if ((status >= 200) && (status < 300)) {
			return new SuccessMessage(response);
		}
		return null;
	}
	
	public Response getResponse()
	{
		return response;
	}
}
