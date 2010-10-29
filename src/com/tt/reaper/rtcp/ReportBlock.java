package com.tt.reaper.rtcp;

public class ReportBlock {
	private byte [] data;
	private int start;
	
	public ReportBlock(byte [] data, int start)
	{
		this.data = data;
		this.start = start;
	}

	public int getSSRC() {
		return RtcpPacket.getInt(data, start + 0);
	}
	
	public int getFractionLost() {
		return data[start + 4];
	}
	
	public int getCumulativeLost() {
		return (RtcpPacket.getInt(data, start + 4) & 0xFFFFFF);
	}
	
	public int getExtendedHighestSequenceReceived() {
		return RtcpPacket.getInt(data, start + 8);
	}
	
	public int getJitter()
	{
		return RtcpPacket.getInt(data, start + 12);
	}
	
	public int getLastReport() {
		return RtcpPacket.getInt(data, start + 16);
	}
	
	public int getDelaySinceLastReport() {
		return RtcpPacket.getInt(data, start + 20);
	}
}
