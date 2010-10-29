package com.tt.reaper.vq;

public class VQSessionReport extends VQReportEvent {
	private static final String HEADER = "VQSessionReport : CallTerm\r\n";

	public VQSessionReport(LocalMetrics localMetrics) {
		super(HEADER, localMetrics);
	}
}
