package com.tt.reaper.rtcp;

import org.apache.log4j.Logger;

public class RtcpPacket {
	protected static Logger logger = Logger.getLogger(RtcpPacket.class);
	public static final int HEADER_SIZE = 8;
	public static final int SENDER_INFO_SIZE = 20;
	public static final int REPORT_SIZE = 24;
	public static final int TYPE_SENDER_REPORT = 0xC8;
	public static final int TYPE_RECEIVER_REPORT = 0xC9;
	public static final int TYPE_SOURCE_DESCRIPTION = 0xCA;
	public static final int TYPE_GOODBYE = 0xCB;
	public static final int TYPE_APPLICATION_DEFINED = 0xCC;
	public static final int TYPE_EXTENDED_REPORT = 0xCF;

	protected byte[] data;
	protected int offset;

	RtcpPacket(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
	}
	
	static int probePacketType(byte[] data, int offset) {
		return (data[offset + 1] & 0xFF);
	}
	
	static int getInt(byte[] data, int offset) {
		long value;
		value = data[offset++] & 0xFF;
		value <<= 8;
		value |= data[offset++] & 0xFF;
		value <<= 8;
		value |= data[offset++] & 0xFF;
		value <<= 8;
		value |= data[offset++] & 0xFF;
		return (int)value;
	}
	
	public int getVersion() {
		return (data[offset + 0] >> 6) & 0x03;
	}
	
	public int getCount() {
		return (data[offset + 0] & 0x1F);
	}
	
	public int getPacketType() {
		return (data[offset + 1] & 0xFF);
	}
	
	public int getLength()
	{
		return (((data[offset + 2] << 8) | data[offset + 3]) + 1) * 4;
	}
}
