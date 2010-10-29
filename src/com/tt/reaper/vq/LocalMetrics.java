package com.tt.reaper.vq;

import com.tt.reaper.call.AudioData;
import com.tt.reaper.call.CallContext;

public class LocalMetrics extends Metrics {
	private static final String NAME = "LocalMetrics";
	
	LocalMetrics()
	{
		super(NAME);
	}

	public LocalMetrics(CallContext context, AudioData data) {
		super(NAME, context, data);
	}
}
