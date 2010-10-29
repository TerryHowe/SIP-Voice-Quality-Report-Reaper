package com.tt.reaper.message;

import javax.sip.message.Request;

public class CancelMessage extends RequestMessage {
	public CancelMessage(Request request) {
		super(Message.CANCEL, request);
	}
}
