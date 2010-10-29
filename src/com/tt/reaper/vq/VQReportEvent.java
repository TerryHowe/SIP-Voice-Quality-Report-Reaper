package com.tt.reaper.vq;


class VQReportEvent {
	String header;
	private LocalMetrics localMetrics;
	private RemoteMetrics remoteMetrics;
	
	protected VQReportEvent(String header, LocalMetrics localMetrics) {
		this.header = header;
		this.localMetrics = localMetrics;
	}
	
	public void setRemoteMetrics(RemoteMetrics remoteMetrics) {
		this.remoteMetrics = remoteMetrics;
	}

	public String toString()
	{
		String message = header;
		message += localMetrics;
		if (remoteMetrics != null)
			message += remoteMetrics;
		return message;
	}
}
