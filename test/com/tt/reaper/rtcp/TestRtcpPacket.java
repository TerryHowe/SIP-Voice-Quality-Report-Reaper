package com.tt.reaper.rtcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.util.Iterator;

import org.junit.Test;

import com.tt.reaper.message.NotifyMessage;

public class TestRtcpPacket {
	public static final String XRFILE = "data/rtcpxr.data";
	public static final String RECEIVER_FILE = "data/rtcp_receiver_report.data";
	public static final String SENDER_FILE = "data/rtcp_sender_report.data";
	public static final String SOURCE_MAC = "00:00:00:00:00:00";
	public static final String SOURCE_IP = "10.0.100.1";
	public static final int SOURCE_PORT = 9091;
	public static final String DESTINATION_MAC = "01:01:01:01:01:01";
	public static final String DESTINATION_IP = "100.1.1.1";
	public static final int DESTINATION_PORT = 8081;
	private static final int BUFFER_SIZE = 1024;
	private static byte[] buffer = new byte[BUFFER_SIZE];
    
    public static DataPacket create(String fileName)
    {
    	NotifyMessage message = createDatagram(fileName);
		assertTrue(message != null);
		DataPacket dataPacket = new DataPacket(message.getRequest().getRawContent());
		return dataPacket;
    }
    
	public static NotifyMessage createNotifyDataMessage(String fileName, String fromAddy, int fromPort, String toAddy, int toPort) {
    	try {
    		String header = SOURCE_MAC + ";" + fromAddy + ";" + fromPort + ";" + DESTINATION_MAC + ";" + toAddy + ";" + toPort + ";";
    		System.arraycopy(header.getBytes(), 0, buffer, 0, header.length());
    		FileInputStream input = new FileInputStream(fileName);
    		byte[] data = new byte[BUFFER_SIZE];
    		int length = input.read(data, 0, BUFFER_SIZE);
    		input.close();
    		int index = header.length();
    		byte[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    		for (int j=0; j<length; j++) {
    			buffer[index++] = hex[(data[j] >> 4) & 0x0F];
    			buffer[index++] = hex[data[j] & 0x0F];
    		}
    		buffer[index++] = '\n';
    		return new NotifyMessage(new String(buffer, 0, index));
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return null;
	}
	
    public static NotifyMessage createDatagram(String fileName)
    {
    	return createNotifyDataMessage(fileName, SOURCE_IP, SOURCE_PORT, DESTINATION_IP, DESTINATION_PORT);
    }
    
	@Test
	public void testXR()
	{
		Iterator<RtcpPacket> it = create(XRFILE).getIterator();
		
		RtcpSenderReport senderReport = (RtcpSenderReport)it.next();
		assertEquals(2, senderReport.getVersion());
		assertEquals(0, senderReport.getCount());
		assertEquals(RtcpPacket.TYPE_SENDER_REPORT, senderReport.getPacketType());
		assertEquals(28, senderReport.getLength());
		assertEquals(0x302867b9, senderReport.getSSRC());
		assertEquals(13599, senderReport.getSenderPacketCount());
		assertEquals(2175840, senderReport.getSenderOctetCount());
		
		RtcpSourceDescription sourceDescription = (RtcpSourceDescription) it.next();
		assertEquals(2, sourceDescription.getVersion());
		assertEquals(1, sourceDescription.getCount());
		assertEquals(RtcpPacket.TYPE_SOURCE_DESCRIPTION, sourceDescription.getPacketType());
		assertEquals(20, sourceDescription.getLength());
		
		RtcpExtendedReport extendedReport = (RtcpExtendedReport) it.next();
		assertEquals(2, extendedReport.getVersion());
		assertEquals(0, extendedReport.getCount());
		assertEquals(RtcpPacket.TYPE_EXTENDED_REPORT, extendedReport.getPacketType());
		assertEquals(44, extendedReport.getLength());
		assertEquals(0x302867b9, extendedReport.getSSRC());
		
		Iterator<VoipMetricsExtendedReportBlock> eit = extendedReport.getIterator();
		VoipMetricsExtendedReportBlock reportBlock = eit.next();
		assertEquals(ExtendedReportBlock.VOIP_METRICS, reportBlock.getBlockType());
		assertEquals(0, reportBlock.getTypeSpecific());
		assertEquals(32, reportBlock.getBlockLength());
		assertTrue(0.0 == reportBlock.getLossRate());
		assertTrue(0.0 == reportBlock.getDiscardRate());
		assertTrue(0.0 == reportBlock.getBurstDensity());
		assertTrue(0.0 == reportBlock.getGapDensity());
		assertEquals(0, reportBlock.getBurstDuration());
		assertEquals(0, reportBlock.getGapDuration());
		assertEquals(0, reportBlock.getRoundTripDelay());
		assertEquals(124, reportBlock.getEndSystemDelay());
		assertEquals(-85, reportBlock.getSignalLevel());
		assertEquals(-72, reportBlock.getNoiseLevel());
		assertEquals(75, reportBlock.getResidualEchoReturnLoss());
		assertEquals(16, reportBlock.getGmin());
		assertEquals(96, reportBlock.getRFactor());
		assertEquals(127, reportBlock.getExtRFactor());
		assertTrue(4.4 == reportBlock.getMOSLQ());
		assertTrue(4.4 == reportBlock.getMOSCQ());
		assertEquals(0, reportBlock.getPacketLossConcealment());
		assertEquals(3, reportBlock.getJitterBufferAdaptive());
		assertEquals(0, reportBlock.getJitterBufferRate());
		assertEquals(20, reportBlock.getJitterBufferNominal());
		assertEquals(100, reportBlock.getJitterBufferMaximum());
		assertEquals(300, reportBlock.getJitterBufferAbsMaximum());

		assertEquals(false, eit.hasNext());
		
		assertEquals(false, it.hasNext());
	}
	
	@Test
	public void testReceiverReport()
	{
		Iterator<RtcpPacket> it = create(RECEIVER_FILE).getIterator();
		
		RtcpReceiverReport receiverReport = (RtcpReceiverReport)it.next();
		assertEquals(2, receiverReport.getVersion());
		assertEquals(1, receiverReport.getCount());
		assertEquals(RtcpPacket.TYPE_RECEIVER_REPORT, receiverReport.getPacketType());
		assertEquals(32, receiverReport.getLength());
		assertEquals(0xD2BD4E3E, receiverReport.getSSRC());
		ReportBlock block = receiverReport.getReport(0);
		assertEquals(0x58f33dea, block.getSSRC());
		assertEquals(0, block.getFractionLost());
		assertEquals(0, block.getCumulativeLost());
		assertEquals(11732, block.getExtendedHighestSequenceReceived());
		assertEquals(1890, block.getJitter());
		assertEquals(0x86defef9, block.getLastReport());
		assertEquals(1, block.getDelaySinceLastReport());
	}
	
	@Test
	public void testSenderReport()
	{
		Iterator<RtcpPacket> it = create(SENDER_FILE).getIterator();
		
		RtcpSenderReport senderReport = (RtcpSenderReport)it.next();
		assertEquals(2, senderReport.getVersion());
		assertEquals(1, senderReport.getCount());
		assertEquals(RtcpPacket.TYPE_SENDER_REPORT, senderReport.getPacketType());
		assertEquals(52, senderReport.getLength());
		assertEquals(0xD2BD4E3E, senderReport.getSSRC());
		assertEquals(0xc59286defef9db23L, senderReport.getTimeStamp());
		assertEquals(49760, senderReport.getRTPTimeStamp());
		assertEquals(158, senderReport.getSenderPacketCount());
		assertEquals(25280, senderReport.getSenderOctetCount());
		ReportBlock block = senderReport.getReport(0);
		assertEquals(0xd2bd4e3e, block.getSSRC());
		assertEquals(0, block.getFractionLost());
		assertEquals(0, block.getCumulativeLost());
		assertEquals(10354846, block.getExtendedHighestSequenceReceived());
		assertEquals(0, block.getJitter());
		assertEquals(0x86defef9, block.getLastReport());
		assertEquals(1, block.getDelaySinceLastReport());
	}
}
