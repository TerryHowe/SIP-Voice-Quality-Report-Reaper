package com.tt.reaper.sip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.tt.reaper.Reaper;
import com.tt.reaper.call.CallContext;
import com.tt.reaper.call.CallManager;
import com.tt.reaper.filter.MockPacketFilter;
import com.tt.reaper.rtcp.TestRtcpPacket;

public class TestReaperListener {
	private static final String callId = "12013223@200.57.7.195";
	private static final String fromRtcpAddy = "200.57.7.196";
	private static final int fromRtcpPort = 40377;
	private static final String toRtcpAddy = "200.57.7.204";
	private static final int toRtcpPort = 8001;
	
	@Test
	public void testIt()
	{
		Reaper.instance.init();
		MockPacketFilter packetFilter = new MockPacketFilter();
		MockCollector.instance.init();
		packetFilter.sendSip("data/invite.data");
		try { Thread.sleep(750); } catch (Exception e) {}
		
		CallContext context = CallManager.instance.getContextCallId(callId);
		assertTrue("ReaperStack.init() failed perhaps.", context != null);
		assertEquals("StateInvited", context.state.toString());
		assertEquals("\"francisco@bestel.com\" <sip:francisco@bestel.com:55060>", context.to);
		assertEquals("<sip:200.57.7.195:55061;user=phone>", context.from);
		assertEquals(callId, context.callId);
		assertEquals(context, CallManager.instance.getContextRtcp(fromRtcpAddy, fromRtcpPort));
		packetFilter.sendSip("data/100.data");
		packetFilter.sendSip("data/180.data");
		
		packetFilter.sendSip("data/200.data");
		try { Thread.sleep(750); } catch (Exception e) {}
		assertEquals(context, CallManager.instance.getContextRtcp(toRtcpAddy, toRtcpPort));
		
		assertEquals("StateInvited", context.state.toString());
		packetFilter.sendSip("data/ack.data");
		try { Thread.sleep(750); } catch (Exception e) {}
		assertEquals("StateConnected", context.state.toString());
		
		packetFilter.sendRtcp(TestRtcpPacket.XRFILE, fromRtcpAddy, fromRtcpPort, toRtcpAddy, toRtcpPort);
		try { Thread.sleep(750); } catch (Exception e) {}
		assertEquals("StateConnected", context.state.toString());
		assertTrue(MockCollector.instance.message.contains("QualityEst:RCQ=96 MOSLQ=4.4 MOSCQ=4.4"));
		
		packetFilter.sendSip("data/bye.data");
		try { Thread.sleep(750); } catch (Exception e) {}
		assertEquals("StateTerminating", context.state.toString());
		
		packetFilter.sendSip("data/bye200.data");
		try { Thread.sleep(750); } catch (Exception e) {}
		assertEquals("StateTerminated", context.state.toString());
	}
}
