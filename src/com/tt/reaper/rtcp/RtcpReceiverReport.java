package com.tt.reaper.rtcp;

public class RtcpReceiverReport extends RtcpPacket {
	
	public RtcpReceiverReport(byte[] data, int offset) {
		super(data, offset);
	}

	public int getSSRC()
	{
		return getInt(data, offset + 4);
	}
	
	public ReportBlock getReport(int number)
	{
		return new ReportBlock(data, ((offset + HEADER_SIZE) + (number * REPORT_SIZE)));
	}
}
