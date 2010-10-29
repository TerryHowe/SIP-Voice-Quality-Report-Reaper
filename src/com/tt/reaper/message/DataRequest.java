package com.tt.reaper.message;

public class DataRequest extends Message {
	public MessageQueue queue;
	
	public DataRequest(MessageQueue queue) {
		super(Message.DATA_REQUEST);
		this.queue = queue;
	}
}