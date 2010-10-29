package com.tt.reaper.vq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.rtcp.RtcpExtendedReport;
import com.tt.reaper.rtcp.RtcpPacket;
import com.tt.reaper.rtcp.RtcpSenderReport;
import com.tt.reaper.rtcp.RtcpSourceDescription;
import com.tt.reaper.rtcp.TestRtcpPacket;
import com.tt.reaper.rtcp.VoipMetricsExtendedReportBlock;

public class TestMetrics {
	
	@Before
	public void setUp()
	{
		Reaper.instance.init();
	}
	
	@Test
	public void testLocalMetrics()
	{
		LocalMetrics sot = new LocalMetrics();
		assertEquals("LocalMetrics:\r\n", sot.toString());
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		Date startTime = calendar.getTime();
		startTime.setTime(0);
		Date endTime = calendar.getTime();
		endTime.setTime(1000*60*60);
		sot.setSessionDescription("8", "pcma", "8000");
		sot.setTimeStamps(startTime, endTime);
		sot.setCallID("6dg37f1890463");
		sot.setFromID("Alice <sip:alice@example.org>");
		sot.setToID("Bill <sip:bill@elpmaxe.org>");
		sot.setLocalAddr("IP=11.1.1.150 PORT=5002 SSRC=0x2468abcd");
		sot.setJitterBuffer(3, 2, 40, 80, 120);
		sot.setPacketLoss(5.01, 2.04);
		sot.setBurstGapLoss(0.0, 0, 2.0, 500, 16);
		sot.setDelay(200, 140, 200, 2, 10);
		sot.setSignal(-18, -50, 55);
		sot.setQualityEst(88, 85, 90, 4.1, 4.0, true);
		String bigMessage = "LocalMetrics:\r\n" +
							"SessionDesc:PT=8 PD=pcma SR=8000\r\n" +
							"Timestamps:START=1970-01-01T00:00:00.000000Z STOP=1970-01-01T01:00:00.000000Z\r\n" +
							"CallID:6dg37f1890463\r\n" +
							"FromID:Alice <sip:alice@example.org>\r\n" +
							"ToID:Bill <sip:bill@elpmaxe.org>\r\n" +
							"LocalAddr:IP=11.1.1.150 PORT=5002 SSRC=0x2468abcd\r\n" +
							"JitterBuffer:JBA=3 JBR=2 JBN=40 JBM=80 JBX=120\r\n" +
							"PacketLoss:NLR=5.0 JDR=2.0\r\n" +
							"BurstGapLoss:BLD=0.0 BD=0 GLD=2.0 GD=500 GMIN=16\r\n" +
							"Delay:RTD=200 ESD=140 SOWD=200 IAJ=2 MAJ=10\r\n" +
							"Signal:SL=-18 NL=-50 RERL=55\r\n" +
							"QualityEst:RLQ=88 RCQ=85 EXTRI=90 MOSLQ=4.1 MOSCQ=4.0 QoEEstAlg=P.564\r\n";
		assertEquals(bigMessage, sot.toString());
	}
	
	@Test
	public void testRemoteMetrics()
	{
		RemoteMetrics sot = new RemoteMetrics();
		assertEquals("RemoteMetrics:\r\n", sot.toString());
	}
	
	@Test
	public void testSetVoipMetrics()
	{
		RemoteMetrics sot = new RemoteMetrics();
		sot.setSessionDescription("0", "pcmu", "8000");
		sot.setCallID("6dg37f1890463");
		sot.setFromID("Alice <sip:alice@example.org>");
		sot.setToID("Bill <sip:bill@elpmaxe.org>");
		sot.setLocalAddr("IP=11.1.1.150 PORT=5002 SSRC=0x2468abcd");
		sot.setLocalMac("01:02:03:04:05:06");
		Iterator<RtcpPacket> it = TestRtcpPacket.create(TestRtcpPacket.XRFILE).getIterator();
		assertTrue(it.next() instanceof RtcpSenderReport);
		assertTrue(it.next() instanceof RtcpSourceDescription);
		RtcpExtendedReport report = (RtcpExtendedReport)it.next();
		Iterator<VoipMetricsExtendedReportBlock> eit = report.getIterator();
		VoipMetricsExtendedReportBlock reportBlock = eit.next();
		sot.setMetrics(reportBlock);	
		String bigMessage = "RemoteMetrics:\r\n" +
							"SessionDesc:PT=0 PD=pcmu SR=8000\r\n" +
							"CallID:6dg37f1890463\r\n" +
							"FromID:Alice <sip:alice@example.org>\r\n" +
							"ToID:Bill <sip:bill@elpmaxe.org>\r\n" +
							"LocalAddr:IP=11.1.1.150 PORT=5002 SSRC=0x2468abcd\r\n" +
							"LocalMAC:01:02:03:04:05:06\r\n" +
							"JitterBuffer:JBA=3 JBR=0 JBN=20 JBM=100 JBX=300\r\n" +
							"PacketLoss:NLR=0.0 JDR=0.0\r\n" +
							"BurstGapLoss:BLD=0.0 BD=0 GLD=0.0 GD=0 GMIN=16\r\n" +
							"Delay:RTD=0 ESD=124\r\n" +
							"Signal:SL=-85 NL=-72 RERL=75\r\n" +
							"QualityEst:RCQ=96 MOSLQ=4.4 MOSCQ=4.4\r\n";
		assertEquals(bigMessage, sot.toString());
	}
}
