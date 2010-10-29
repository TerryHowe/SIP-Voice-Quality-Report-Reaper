package com.tt.reaper.vq;

import com.tt.reaper.call.AudioData;
import com.tt.reaper.call.CallContext;

public class RemoteMetrics extends Metrics {
	private static final String NAME = "RemoteMetrics";
	
	RemoteMetrics()
	{
		super(NAME);
	}

	public RemoteMetrics(CallContext context, AudioData data) {
		super(NAME, context, data);
	}
}
