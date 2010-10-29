package com.tt.reaper.message;

import javax.sip.message.Request;

public class InviteMessage extends RequestMessage {
	public InviteMessage()
	{
		super(Message.INVITE, Request.INVITE, getNewCallId());
		status = init();
	}
	
	public InviteMessage(Request request) {
		super(Message.INVITE, request);
	}
}
