package com.tt.reaper.message;

import javax.sip.message.Response;

public class ProvisionalMessage  extends ResponseMessage {
	private ProvisionalMessage(Response response)
	{
		super(SipMessage.PROVISIONAL, response);
	}
	
	static SipMessage create(Response response)
	{
		if (response.getStatusCode() < 200) {
			return new ProvisionalMessage(response);
		}
		return null;
	}
}