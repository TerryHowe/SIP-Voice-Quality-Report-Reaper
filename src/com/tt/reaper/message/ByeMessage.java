package com.tt.reaper.message;

import javax.sip.message.Request;

public class ByeMessage extends RequestMessage {
	public ByeMessage(Request request) {
		super(Message.BYE, request);
	}
}
