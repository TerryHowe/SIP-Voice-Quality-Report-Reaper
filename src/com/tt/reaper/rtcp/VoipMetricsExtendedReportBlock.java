package com.tt.reaper.rtcp;

public class VoipMetricsExtendedReportBlock extends ExtendedReportBlock {

	public VoipMetricsExtendedReportBlock(byte[] data, int offset) {
		super(data, offset);
	}
	
	public double getLossRate()
	{
		return ((double)(data[offset + 8] & 0xFF)) / 256.0;
	}
	
	public double getDiscardRate()
	{
		return ((double)(data[offset + 9] & 0xFF)) / 256.0;
	}
	
	public double getBurstDensity()
	{
		return ((double)(data[offset + 10] & 0xFF)) / 256.0;
	}
	
	public double getGapDensity()
	{
		return ((double)(data[offset + 11] & 0xFF)) / 256.0;
	}
	
	public int getBurstDuration()
	{
		return ((RtcpPacket.getInt(data, offset + 12) >> 16) & 0xFFFF);
	}
	
	public int getGapDuration()
	{
		return (RtcpPacket.getInt(data, offset + 12) & 0xFFFF);
	}
	
	public int getRoundTripDelay()
	{
		return ((RtcpPacket.getInt(data, offset + 16) >> 16) & 0xFFFF);
	}
	
	public int getEndSystemDelay()
	{
		return (RtcpPacket.getInt(data, offset + 16) & 0xFFFF);
	}
	
	public int getSignalLevel()
	{
		return data[offset + 20];
	}
	
	public int getNoiseLevel()
	{
		return data[offset + 21];
	}
	
	public int getResidualEchoReturnLoss()
	{
		return data[offset + 22] & 0xFF;
	}
	
	public int getGmin()
	{
		return data[offset + 23] & 0xFF;
	}
	
	public int getRFactor()
	{
		return data[offset + 24] & 0xFF;
	}
	
	public int getExtRFactor()
	{
		return data[offset + 25] & 0xFF;
	}
	
	public double getMOSLQ()
	{
		return ((double)(data[offset + 26] & 0xFF)) / 10.0;
	}
	
	public double getMOSCQ()
	{
		return ((double)(data[offset + 27] & 0xFF)) / 10.0;
	}
	
	private int getRxConfig()
	{
		return data[offset + 28] & 0xFF;
	}
	
	public int getPacketLossConcealment()
	{
		return (getRxConfig() >> 6) & 0x03;
	}
	
	public int getJitterBufferAdaptive()
	{
		return (getRxConfig() >> 4) & 0x03;
	}
	
	public int getJitterBufferRate()
	{
		return getRxConfig() & 0x0F;
	}
	
	public int getJitterBufferNominal()
	{
		return (RtcpPacket.getInt(data, offset + 28) & 0xFFFF);
	}
	
	public int getJitterBufferMaximum()
	{
		return ((RtcpPacket.getInt(data, offset + 32) >> 16) & 0xFFFF);
	}
	
	public int getJitterBufferAbsMaximum()
	{
		return (RtcpPacket.getInt(data, offset + 32) & 0xFFFF);
	}
}
