package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.message.TestMessageFactory;
import com.tt.reaper.message.TestRtpPacket;
import com.tt.reaper.sip.CollectorStack;
import com.tt.reaper.sip.MockCollector;
import com.tt.reaper.vq.Metrics;

public class TestStateTerminating {
	CallContext context;
	Date remoteEndTime;

	private String getExpectedData(double packetLoss) {
		return
			"VQSessionReport : CallTerm\r\n" +
			"LocalMetrics:\r\n" +
			"SessionDesc:PT=0 PD=pcmu SR=8000\r\n" +
			"Timestamps:START=" + Metrics.formatDate(context.startTime) + " STOP=" + Metrics.formatDate(context.endTime) + "\r\n" +
			"CallID:2020202\r\n" +
			"FromID:from@example.com\r\n" +
			"ToID:to@example.com\r\n" +
			"OrigID:from@example.com\r\n" +
			"LocalAddr:from@example.com\r\n" +
			"RemoteAddr:to@example.com\r\n" +
			"PacketLoss:NLR=" + packetLoss + "\r\n" + 
			"Delay:IAJ=0\r\n";
	}
	
	@Before
	public void setUp()
	{
		Reaper.instance.init();
		MockCollector.instance.init();
		context = new CallContext();
		context.callId = "2020202";
		context.from = "from@example.com";
		context.to = "to@example.com";
		context.audioTo.add(new AudioData());
		context.audioTo.get(0).payloadType = "0";
		context.audioTo.get(0).payloadDescription = "pcmu";
		context.audioTo.get(0).sampleRate = "8000";
		context.audioFrom.add(new AudioData());
		context.audioFrom.get(0).payloadType = "0";
		context.audioFrom.get(0).payloadDescription = "pcmu";
		context.audioFrom.get(0).sampleRate = "8000";
	}
	
	@Test
	public void testInvite()
	{
		assertEquals(StateTerminating.instance, StateTerminating.instance.process(context, TestMessageFactory.createInviteMessage("127.0.0.3", 9090)));
	}
	
	@Test
	public void testSuccess()
	{
		remoteEndTime = context.endTime;
		assertEquals(StateTerminated.instance, StateTerminating.instance.process(context, TestMessageFactory.createSuccessMessage()));
		assertEquals(getExpectedData(0.0), CollectorStack.instance.lastSendData);
		try { Thread.sleep(750); } catch (Exception e) {}
		verifyMessage(0.0);
	}

	@Test
	public void testError()
	{
		remoteEndTime = context.endTime;
		assertEquals(StateTerminated.instance, StateTerminating.instance.process(context, TestMessageFactory.createErrorMessage()));
		assertEquals(getExpectedData(0.0),  CollectorStack.instance.lastSendData);
		try { Thread.sleep(750); } catch (Exception e) {}
		verifyMessage(0.0);
	}
	
	@Test
	public void testPacketLoss()
	{
		remoteEndTime = context.endTime;
		AudioData data = context.audioTo.get(0);
		data.receive(1, 10, 11);
		data.receive(2, 10, 11);
		data.receive(1, 10, 11);
		data.receive(1, 10, 11);
		data.receive(3, 10, 11);
		data.receive(4, 10, 11);
		data.receive(6, 10, 11);
		data.receive(7, 10, 11);
		data.receive(8, 10, 11);
		data.receive(9, 10, 11);
		
		assertEquals(StateTerminated.instance, StateTerminating.instance.process(context, TestMessageFactory.createSuccessMessage()));
		
		assertEquals(getExpectedData(32.0), CollectorStack.instance.lastSendData);
	}
	
	private void verifyMessage(double packetLoss) {
//		System.out.println("--------------------------");
//		System.out.println(MockCollector.instance.message);
//		System.out.println("--------------------------");
		assertTrue(MockCollector.instance.message.contains("PUBLISH sip:collector@127.0.0.3:5060;transport=udp SIP/2.0"));
		assertTrue(MockCollector.instance.message.contains("CSeq: 1 PUBLISH"));
		assertTrue(MockCollector.instance.message.contains("From: \"reaper\" <sip:reaper@127.0.0.2:5060>;tag=ReaperV1.0"));
		assertTrue(MockCollector.instance.message.contains("To: \"collector\" <sip:collector@127.0.0.3:5060>"));
		assertTrue(MockCollector.instance.message.contains("Max-Forwards: 70"));
		assertTrue(MockCollector.instance.message.contains("Contact: \"reaper\" <sip:reaper@127.0.0.2:5060>"));
		assertTrue(MockCollector.instance.message.contains("Content-Type: application/vq-rtcpxr"));
		assertTrue(MockCollector.instance.message.contains(getExpectedData(packetLoss)));
	}
	
	@Test
	public void testAck()
	{
		assertEquals(StateTerminating.instance, StateTerminating.instance.process(context, TestMessageFactory.createAckMessage()));
	}
	
	@Test
	public void testBye()
	{
		assertEquals(StateTerminating.instance, StateTerminating.instance.process(context, TestMessageFactory.createByeMessage()));
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
		
		assertEquals(StateTerminating.instance, StateTerminating.instance.process(context, TestRtpPacket.create(2)));
		
		assertEquals(2, data.getFirst());
		assertEquals(2, data.getLast());
		assertEquals(1, data.getPacketCount());
	}
}
