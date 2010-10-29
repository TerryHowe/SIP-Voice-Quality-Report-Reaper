package com.tt.reaper;

import org.apache.log4j.PropertyConfigurator;

public class ReaperLogger {
	private static boolean initialized = false;
	private static final String FILE_NAME = "config/log4j.properties";
	
	public static synchronized void init() {
		if (initialized == true)
			return;
		initialized = true;
		PropertyConfigurator.configure(FILE_NAME);
	}
}