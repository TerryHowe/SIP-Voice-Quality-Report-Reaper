package com.tt.reaper.rtcp;

import java.util.ArrayList;
import java.util.Iterator;

public class RtcpExtendedReport extends RtcpPacket {
	private static final int HEADER_LENGTH = 8;
	private ArrayList<VoipMetricsExtendedReportBlock> list = new ArrayList<VoipMetricsExtendedReportBlock>();
	
	public RtcpExtendedReport(byte[] data, int offset) {
		super(data, offset);
		int start = offset + HEADER_LENGTH;
		int end = offset + getLength();
		while (start < end) {
			ExtendedReportBlock block = null;
			switch (ExtendedReportBlock.probeBlockType(data, start)) {
			case ExtendedReportBlock.VOIP_METRICS:
				block = new VoipMetricsExtendedReportBlock(data, start);
				list.add((VoipMetricsExtendedReportBlock) block);
				break;
			default:
				logger.info("Ignoring RTCP-XR: " + ExtendedReportBlock.probeBlockType(data, start));
				block = new ExtendedReportBlock(data, start);
				break;
			}
			start += block.getLength();
		}
	}

	public int getSSRC()
	{
		return getInt(data, offset + 4);
	}
	
	public Iterator<VoipMetricsExtendedReportBlock> getIterator() {
		return list.iterator();
	}
}