package com.tt.reaper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TestConfiguration {

	@Test
	public void testConfiguration()
	{
		Configuration sot = new Configuration();
		assertEquals("0", sot.getStackTraceLevel());
		assertEquals("server.log", sot.getStackServerLog());
		assertEquals("debug.log", sot.getStackDebugLog());
		assertEquals("eth0", sot.getReadInterface());
		assertEquals(5050, sot.getReadPort());
		assertEquals("127.0.0.2", sot.getWriteInterface());
		assertEquals(5060, sot.getWritePort());
		assertEquals("reaper", sot.getWriteUsername());
		assertEquals(5060, sot.getCollectorPort());
		assertEquals("127.0.0.3", sot.getCollectorHost());
		assertEquals("collector", sot.getCollectorUsername());
		assertEquals("/opt/reaper/bin/filter.sh  -n -e  -i eth0", sot.getCommand());
		assertEquals("ReaperV1.0", sot.getSoftwareVersion());
	}
}
