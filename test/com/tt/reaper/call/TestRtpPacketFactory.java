package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.message.TestRtpPacket;

public class TestRtpPacketFactory {
	
	@Test
	public void testFactory()	{
		RtpPacketFactory factory = new RtpPacketFactory();
		factory.init(null);
		assertEquals(null, factory.getNext());
	}
	
	@Test
	public void testFactoryOne()
	{
		RtpPacketFactory factory = new RtpPacketFactory();
		factory.init(TestRtpPacket.create(1, 2).getBytes());
		RtpPacket packet = factory.getNext();
		assertEquals(1, packet.getSequenceNumber());
		assertEquals(2, packet.getTimeStamp());
	}
}
