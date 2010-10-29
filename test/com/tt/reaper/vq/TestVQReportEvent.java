package com.tt.reaper.vq;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tt.reaper.vq.VQReportEvent;

public class TestVQReportEvent {
	
	@Test
	public void testNoMetrics()
	{
		VQReportEvent sot;
		sot = new VQSessionReport(new LocalMetrics());
		assertEquals("VQSessionReport : CallTerm\r\nLocalMetrics:\r\n", sot.toString());
		sot = new VQIntervalReport(new LocalMetrics());
		assertEquals("VQIntervalReport\r\nLocalMetrics:\r\n", sot.toString());
	}
}
