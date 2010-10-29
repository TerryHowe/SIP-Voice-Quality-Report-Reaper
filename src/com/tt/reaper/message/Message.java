package com.tt.reaper.message;

import org.apache.log4j.Logger;

public abstract class Message {
	protected static Logger logger = Logger.getLogger(Message.class);
	public static final int PROVISIONAL = 180;
	public static final int SUCCESS = 200;
	public static final int FAILURE = 400;
	public static final int ACK = 1;
	public static final int BYE = 2;
	public static final int INVITE = 3;
	public static final int PUBLISH = 4;
	public static final int NOTIFY = 5;
	public static final int CANCEL = 6;
	public static final int DATA_RESPONSE = 996;
	public static final int DATA_REQUEST = 997;
	public static final int RTP_PACKET = 998;
	public static final int DATA_PACKET = 999;
	
	private int type;
	
	protected Message(int type)
	{
		this.type = type;
	}
	
	public final int getType()
	{
		return type;
	}
}