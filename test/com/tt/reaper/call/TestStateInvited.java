package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.message.InviteMessage;
import com.tt.reaper.message.TestMessageFactory;
import com.tt.reaper.message.TestRtpPacket;

public class TestStateInvited {
	CallContext context;
	
	@Before
	public void setUp()
	{
		Reaper.instance.init();
		context = new CallContext();
	}
	
	@Test
	public void testInvite()
	{
		InviteMessage message = TestMessageFactory.createInviteMessage("127.0.0.3", 9090);
		assertEquals(StateInvited.instance, StateInvited.instance.process(context, message));
		assertEquals(message.getCallId(), context.callId);
		assertEquals(message.getFrom(), context.from);
		assertEquals(message.getTo(), context.to);
		assertTrue(context.getFromAudio("127.0.0.3:9091", "127.0.0.3:404") != null);
		assertTrue(context.getToAudio("127.0.0.3:9091", "127.0.0.3:404") == null);
		assertTrue(context.getFromAudio("127.0.0.3:404", "127.0.0.3:9091") == null);
		assertTrue(context.getToAudio("127.0.0.3:404", "127.0.0.3:9091") != null);
		assertTrue(context.getFromAudio("127.0.0.3:9090", "127.0.0.3:404") != null);
		assertTrue(context.getToAudio("127.0.0.3:404", "127.0.0.3:9090") != null);
	}
	
	@Test
	public void testSuccess()
	{
		assertEquals(StateInvited.instance, StateInvited.instance.process(context, TestMessageFactory.createSuccessMessage("127.0.0.4", 7070)));
		assertTrue(context.getFromAudio("127.0.0.4:7071", "127.0.0.4:404") == null);
		assertTrue(context.getToAudio("127.0.0.4:7071", "127.0.0.4:404") != null);
		assertTrue(context.getFromAudio("127.0.0.4:404", "127.0.0.4:7071") != null);
		assertTrue(context.getToAudio("127.0.0.4:404", "127.0.0.4:7071") == null);
		assertTrue(context.getFromAudio("127.0.0.4:404", "127.0.0.4:7070") != null);
		assertTrue(context.getToAudio("127.0.0.4:7070", "127.0.0.4:404") != null);
	}
	
	@Test
	public void testProvisional()
	{
		assertEquals(StateInvited.instance, StateInvited.instance.process(context, TestMessageFactory.createProvisionalMessage()));
	}
	
	@Test
	public void testError()
	{
		assertEquals(StateTerminated.instance, StateInvited.instance.process(context, TestMessageFactory.createErrorMessage()));
	}
	
	@Test
	public void testAck()
	{
		assertEquals(StateConnected.instance, StateInvited.instance.process(context, TestMessageFactory.createAckMessage()));
	}
	
	@Test
	public void testBye()
	{
		assertEquals(StateTerminated.instance, StateInvited.instance.process(context, TestMessageFactory.createByeMessage()));
	}
	
	@Test
	public void testCancel()
	{
		assertEquals(StateTerminated.instance, StateInvited.instance.process(context, TestMessageFactory.createCancelMessage()));
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
		
		assertEquals(StateInvited.instance, StateInvited.instance.process(context, TestRtpPacket.create(2)));
		
		assertEquals(2, data.getFirst());
		assertEquals(2, data.getLast());
		assertEquals(1, data.getPacketCount());
	}
}
