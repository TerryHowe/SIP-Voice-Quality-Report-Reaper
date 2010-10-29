package com.tt.reaper.message;

import javax.sip.message.Request;

public class AckMessage extends RequestMessage {
	public AckMessage(Request request) {
		super(Message.ACK, request);
	}
}