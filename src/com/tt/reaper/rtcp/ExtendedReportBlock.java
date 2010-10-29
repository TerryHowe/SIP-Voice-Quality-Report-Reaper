package com.tt.reaper.rtcp;

public class ExtendedReportBlock {
	public static final int LOSS_RLE = 1;
	public static final int DUPLICATE_RLE = 2;
	public static final int PACKET_RECEIPT_TIMES = 3;
	public static final int RECEIVER_REFERENCE_TIME = 4;
	public static final int DLRR = 5;
	public static final int STATISTICS_SUMMARY = 6;
	public static final int VOIP_METRICS = 7;
	public static final int HEADER_LENGTH = 4;

	protected byte [] data;
	protected int offset;
	
	public ExtendedReportBlock(byte[] data, int offset) {
		this.data = data;
		this.offset = offset;
	}
	
	static int probeBlockType(byte[] data, int offset) {
		return (data[offset + 0] & 0xFF);
	}
	
	public int getBlockType()
	{
		return data[offset + 0] & 0xFF;
	}
	
	public int getTypeSpecific()
	{
		return data[offset + 1] & 0xFF;
	}
	
	public int getBlockLength()
	{
		return (RtcpPacket.getInt(data, offset) & 0xFFFF) * 4;
	}

	public int getLength() {
		return getBlockLength() + HEADER_LENGTH;
	}
}
