package com.tt.reaper.call;

import com.tt.reaper.message.Message;

public class StateTerminated extends State {
	public static final StateTerminated instance = new StateTerminated();
	
	private StateTerminated()
	{
	}
	
	State process(CallContext context, Message message)
	{
		logger.warn("Unexpected message in terminated: " + message);
		return this;
	}
}