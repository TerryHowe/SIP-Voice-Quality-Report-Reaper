package com.tt.reaper.rtcp;


public class RtcpSenderReport extends RtcpPacket {
	
	public RtcpSenderReport(byte[] data, int offset) {
		super(data, offset);
	}
	
	public int getSSRC()
	{
		return getInt(data, offset + 4);
	}

	public long getTimeStamp() {
		int start = offset + 8;
		long value;
		value = data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		value <<= 8;
		value |= data[start++] & 0xFF;
		return value;
	}
	
	public int getRTPTimeStamp()
	{
		return getInt(data, offset + 16);
	}
	
	public int getSenderPacketCount()
	{
		return getInt(data, offset + 20);
	}
	
	public int getSenderOctetCount()
	{
		return getInt(data, offset + 24);
	}
	
	public ReportBlock getReport(int number)
	{
		return new ReportBlock(data, ((offset + HEADER_SIZE + SENDER_INFO_SIZE) + (number * REPORT_SIZE)));
	}
}
