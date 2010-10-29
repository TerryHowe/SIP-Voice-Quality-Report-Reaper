package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tt.reaper.ReaperLogger;
import com.tt.reaper.message.TestMessageFactory;

public class TestCallContext {
	
	@Test
	public void testIt()
	{
		ReaperLogger.init();
		CallContext context = new CallContext();
		assertEquals(StateInvited.instance, context.state);
		assertEquals(true, context.process(TestMessageFactory.createAckMessage()));
		assertEquals(StateConnected.instance, context.state);
		assertEquals(true, context.process(TestMessageFactory.createByeMessage()));
		assertEquals(StateTerminating.instance, context.state);
		assertEquals(false, context.process(TestMessageFactory.createSuccessMessage()));
		assertEquals(StateTerminated.instance, context.state);
	}
}
