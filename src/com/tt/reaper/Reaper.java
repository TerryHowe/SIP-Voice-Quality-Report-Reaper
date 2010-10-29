package com.tt.reaper;

import com.tt.reaper.call.CallManager;
import com.tt.reaper.filter.FilterExecute;
import com.tt.reaper.http.WebServer;
import com.tt.reaper.sip.CollectorStack;
import com.tt.reaper.sip.ReaperStack;

public class Reaper {
	private static boolean initialized = false;
	public static Reaper instance = new Reaper();
	
	private Reaper() {
	}

	public synchronized void init() {
		if (initialized == true)
			return;
		initialized = true;
		ReaperLogger.init();
		CollectorManager.instance.init();
		CallManager.instance.init();
		ReaperStack.instance.init();
		CollectorStack.instance.init();
		FilterExecute.instance.init();
		WebServer.instance.init();
	}
	
	public static void main(String[] args) {
		Reaper.instance.init();
	}
}
