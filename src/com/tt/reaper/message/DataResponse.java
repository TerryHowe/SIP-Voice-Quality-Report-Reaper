package com.tt.reaper.message;

public class DataResponse extends Message {
	public String data;
	
	public DataResponse(String data) {
		super(Message.DATA_REQUEST);
		this.data = data;
	}
}
