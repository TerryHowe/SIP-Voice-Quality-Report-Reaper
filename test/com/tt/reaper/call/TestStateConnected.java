package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.message.TestMessageFactory;
import com.tt.reaper.message.TestRtpPacket;
import com.tt.reaper.rtcp.TestRtcpPacket;
import com.tt.reaper.sip.CollectorStack;
import com.tt.reaper.vq.Metrics;

public class TestStateConnected {
	CallContext context;
	
	@Before
	public void setUp()
	{
		Reaper.instance.init();
		context = new CallContext();
		context.callId = "2020202";
		context.from = "from@example.com";
		context.to = "to@example.com";
	}
	
	@Test
	public void testInvite()
	{
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestMessageFactory.createInviteMessage("127.0.0.3", 9090)));
	}
	
	@Test
	public void testSuccess()
	{
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestMessageFactory.createSuccessMessage()));
	}
	
	@Test
	public void testError()
	{
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestMessageFactory.createErrorMessage()));
	}
	
	@Test
	public void testAck()
	{
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestMessageFactory.createAckMessage()));
	}
	
	@Test
	public void testBye()
	{
		assertEquals(StateTerminating.instance, StateConnected.instance.process(context, TestMessageFactory.createByeMessage()));
	}
	
	@Test
	public void testDataPacket()
	{
		CollectorStack.instance.lastSendData = null;
		AudioData data = new AudioData();
		data.ipAddy = TestRtcpPacket.SOURCE_IP;
		data.rtpPort = TestRtcpPacket.SOURCE_PORT - 1;
		data.payloadType = "8";
		data.payloadDescription = "pcma";
		data.sampleRate = "8000";
		ArrayList<AudioData> list = new ArrayList<AudioData>();
		list.add(data);
		context.setAudioFrom(list);
		
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtcpPacket.create(TestRtcpPacket.XRFILE)));
		
		String expectedMessage = 
			"VQIntervalReport\r\n" +
			"LocalMetrics:\r\n" +
			"SessionDesc:PT=8 PD=pcma SR=8000\r\n" +
			"Timestamps:START=" + Metrics.formatDate(context.startTime) + " STOP=" + Metrics.formatDate(context.endTime) + "\r\n" +
			"CallID:2020202\r\n" +
			"FromID:from@example.com\r\n" +
			"ToID:to@example.com\r\n" +
			"OrigID:from@example.com\r\n" +
			"LocalAddr:from@example.com\r\n" +
			"LocalMAC:00:00:00:00:00:00\r\n" +
			"RemoteAddr:to@example.com\r\n" +
			"RemoteMAC:01:01:01:01:01:01\r\n" +
			"JitterBuffer:JBA=3 JBR=0 JBN=20 JBM=100 JBX=300\r\n" +
			"PacketLoss:NLR=0.0 JDR=0.0\r\n" +
			"BurstGapLoss:BLD=0.0 BD=0 GLD=0.0 GD=0 GMIN=16\r\n" +
			"Delay:RTD=0 ESD=124\r\n" +
			"Signal:SL=-85 NL=-72 RERL=75\r\n" +
			"QualityEst:RCQ=96 MOSLQ=4.4 MOSCQ=4.4\r\n";

		assertEquals(expectedMessage, CollectorStack.instance.lastSendData);
	}
	
	@Test
	public void testRtpPacket()
	{
		AudioData data = new AudioData();
		data.ipAddy = TestRtpPacket.SOURCE_IP;
		data.rtpPort = Integer.valueOf(TestRtpPacket.SOURCE_PORT);
		data.payloadType = "8";
		data.payloadDescription = "pcma";
		data.sampleRate = "8000";
		ArrayList<AudioData> list = new ArrayList<AudioData>();
		list.add(data);
		context.setAudioFrom(list);
		assertEquals(0, data.getLast());
		assertEquals(0, data.getFirst());
		assertEquals(0, data.getPacketCount());
		
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(2)));
		
		assertEquals(2, data.getLast());
		assertEquals(2, data.getFirst());
		assertEquals(1, data.getPacketCount());
	}
	
	@Test
	public void testRtpPackets()
	{
		AudioData data = new AudioData();
		data.ipAddy = TestRtpPacket.SOURCE_IP;
		data.rtpPort = Integer.valueOf(TestRtpPacket.SOURCE_PORT);
		data.payloadType = "8";
		data.payloadDescription = "pcma";
		data.sampleRate = "8000";
		ArrayList<AudioData> list = new ArrayList<AudioData>();
		list.add(data);
		context.setAudioFrom(list);
		assertEquals(0, data.getLast());
		assertEquals(0, data.getFirst());
		assertEquals(0, data.getPacketCount());
		
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(2)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(3)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(2)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(4)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(5)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(7)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(8)));
		assertEquals(StateConnected.instance, StateConnected.instance.process(context, TestRtpPacket.create(9)));
		
		
		assertEquals(2, data.getFirst());
		assertEquals(9, data.getLast());
		assertEquals(7, data.getPacketCount());
		assertEquals(1, data.getDuplicates());
	}
}
