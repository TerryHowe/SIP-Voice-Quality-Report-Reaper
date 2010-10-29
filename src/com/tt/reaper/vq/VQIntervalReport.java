package com.tt.reaper.vq;

public class VQIntervalReport extends VQReportEvent {
	private static final String HEADER = "VQIntervalReport\r\n";

	public VQIntervalReport(LocalMetrics localMetrics) {
		super(HEADER, localMetrics);
	}
}
