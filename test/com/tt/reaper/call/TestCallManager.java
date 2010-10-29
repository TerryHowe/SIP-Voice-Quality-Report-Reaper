package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.message.DataRequest;
import com.tt.reaper.message.DataResponse;
import com.tt.reaper.message.InviteMessage;
import com.tt.reaper.message.MessageQueue;
import com.tt.reaper.message.NotifyMessage;
import com.tt.reaper.message.TestMessageFactory;
import com.tt.reaper.message.TestRtpPacket;
import com.tt.reaper.rtcp.DataPacket;
import com.tt.reaper.rtcp.TestRtcpPacket;

public class TestCallManager {
	private static final String RTP_IP_ADDY = "127.0.0.2";
	private static final int RTP_PORT = 2222;
	private static final String RTCP_IP_PORT = RTP_IP_ADDY + ":" + (RTP_PORT + 1);

	private CallManager sot = CallManager.instance;
	CallContext one = new CallContext();
	CallContext two = new CallContext();
	InviteMessage message;
	String callId;
	
	@Before
	public void setUp()
	{
		Reaper.instance.init();
		message = TestMessageFactory.createInviteMessage(RTP_IP_ADDY, RTP_PORT);
		callId = message.getCallId();
		sot.clearMaps();
	}
	
	@Test
	public void testCallIdMap()
	{
		assertEquals(null, sot.getContextCallId(callId));
		sot.process(message);
		CallContext context = sot.getContextCallId(callId);
		assertTrue(context.getFromAudio(RTCP_IP_PORT, RTCP_IP_PORT) != null);
		assertEquals(context, sot.getContextRtcp(RTCP_IP_PORT));
	}
	
	@Test
	public void testRegister()
	{
		sot.register(one, RTCP_IP_PORT);
		assertEquals(one, sot.getContextRtcp(RTCP_IP_PORT));
	}
	
	@Test
	public void testUnregister()
	{
		sot.process(message);
		CallContext context = sot.getContextCallId(callId);
		context.unregister();
		assertEquals(null, sot.getContextRtcp(RTCP_IP_PORT));
	}
	
	@Test
	public void testDoubleRegister()
	{
		sot.register(one, RTCP_IP_PORT);
		sot.register(two, RTCP_IP_PORT);
		assertEquals(two, sot.getContextRtcp(RTCP_IP_PORT));
	}
	
	@Test
	public void testClearMaps()
	{
		sot.process(message);
		sot.clearMaps();
		assertEquals(null, sot.getContextRtcp(RTCP_IP_PORT));
		assertEquals(null, sot.getContextCallId(callId));
	}
	
	@Test
	public void testDataPacketSource()
	{
		message = TestMessageFactory.createInviteMessage(TestRtcpPacket.SOURCE_IP, (TestRtcpPacket.SOURCE_PORT - 1));
		callId = message.getCallId();
		sot.process(message);
		String rtcpString = TestRtcpPacket.SOURCE_IP + ":" + TestRtcpPacket.SOURCE_PORT;
		CallContext context = sot.getContextCallId(callId);
		assertTrue(context.getFromAudio(rtcpString, rtcpString) != null);
		assertEquals(context, sot.getContextRtcp(rtcpString));
		DataPacket packet = TestRtcpPacket.create(TestRtcpPacket.XRFILE);
		
		sot.process(packet);
	}
	
	@Test
	public void testNotifyMessage()
	{
		message = TestMessageFactory.createInviteMessage(TestRtpPacket.SOURCE_IP, Integer.parseInt(TestRtpPacket.SOURCE_PORT));
		callId = message.getCallId();
		sot.process(message);
		String rtpString = TestRtpPacket.SOURCE_IP + ":" + TestRtpPacket.SOURCE_PORT;
		CallContext context = sot.getContextCallId(callId);
		assertTrue(context.getFromAudio(rtpString, rtpString) != null);
		assertEquals(context, sot.getContextRtcp(rtpString));
		NotifyMessage message = new NotifyMessage(TestRtpPacket.create(1, 2) + TestRtpPacket.create(2, 3) + TestRtpPacket.create(3, 4));
		
		sot.process(message);
		
		AudioData audio = context.getFromAudio(rtpString, rtpString);
		audio.close();
		assertEquals(true, audio.from);
		assertEquals(TestRtpPacket.SOURCE_IP, audio.ipAddy);
		assertEquals("pcma", audio.payloadDescription);
		assertEquals("8", audio.payloadType);
		assertEquals(Integer.parseInt(TestRtpPacket.SOURCE_PORT), audio.rtpPort);
		assertEquals("8000", audio.sampleRate);
		assertEquals(rtpString, audio.getIpRtpPort());
		assertEquals(0.0, audio.getDiscardRate(), 0.001);
		assertEquals(0, audio.getDuplicates());
		assertEquals(1, audio.getFirst());
		assertEquals(3, audio.getLast());
		assertEquals(0.0, audio.getLossRate(), 0.001);
		assertEquals(3, audio.getPacketCount());
		assertEquals(0, audio.getPacketLoss());
		assertEquals(0, audio.getJitter());
	}
	
	@Test
	public void testDataResponse()
	{
		sot.process(message);
		MessageQueue queue = new MessageQueue();
		sot.process(new DataRequest(queue));
		DataResponse response = (DataResponse)queue.getBlocking();
		assertTrue(response.data.contains("FROM:\"reaper\" <sip:reaper@127.0.0.2:5060> TO:\"collector\" <sip:collector@127.0.0.3:5060>"));
	}
}
