package com.tt.reaper.message;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.tt.reaper.ReaperLogger;

public class TestRtpPacket {
	public static final String SOURCE_MAC = "1:1:1:1:1:1";
	public static final String SOURCE_IP = "10.0.0.1";
	public static final String SOURCE_PORT = "9090";
	public static final String DESTINATION_MAC = "2:2:2:2:2:2";
	public static final String DESTINATION_IP = "10.0.0.2";
	public static final String DESTINATION_PORT = "8080";
	
	public static RtpPacket create(int sequenceNumber)
	{
		return new RtpPacket(create(sequenceNumber, 3));
	}
	
	public static String create(int sequenceNumber, int timeStamp) {
		return SOURCE_MAC + ";" + SOURCE_IP + ";" + SOURCE_PORT + ";" +
		    DESTINATION_MAC + ";" + DESTINATION_IP + ";" + DESTINATION_PORT + ";" +
		    sequenceNumber + ";" + timeStamp + ";" + (timeStamp + 1) + ";\n";
	}
	
	@Before
	public void setUp()
	{
		ReaperLogger.init();
	}
	
	@Test
	public void testPacket()
	{
		RtpPacket packet = new RtpPacket(create(1, 2));
		assertEquals(SOURCE_MAC, packet.getSourceMac());
		assertEquals(SOURCE_IP + ":" + SOURCE_PORT, packet.getSource());
		assertEquals(DESTINATION_MAC, packet.getDestinationMac());
		assertEquals(DESTINATION_IP + ":" + DESTINATION_PORT, packet.getDestination());
		assertEquals(1, packet.getSequenceNumber());
		assertEquals(2, packet.getTimeStamp());
		assertEquals(3, packet.getArrival());
	}
}
