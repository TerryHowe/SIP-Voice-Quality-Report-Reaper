package com.tt.reaper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestNics {
	@Before
	public void setUp()
	{
		ReaperLogger.init();
	}
	
	@Test
	public void testNics()
	{
		assertEquals("127.0.0.1", Nics.getIp("unknown"));
		assertEquals("127.0.0.2", Nics.getIp("lo"));
		assertEquals("192.168.1.101", Nics.getIp("eth0"));
	}
}
