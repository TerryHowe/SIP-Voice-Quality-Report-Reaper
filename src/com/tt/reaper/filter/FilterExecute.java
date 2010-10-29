package com.tt.reaper.filter;

import org.apache.log4j.Logger;

import com.tt.reaper.Configuration;

public class FilterExecute extends Thread {
	private static Logger logger = Logger.getLogger(FilterExecute.class);
	public static final FilterExecute instance = new FilterExecute();
	private String command;
	private boolean running = false;

	private FilterExecute() {
	}

	public synchronized void init() {
		command = new Configuration().getCommand();
		if (command == null) {
			logger.error("Filter command not configured");
		}
		if (running == false)
			start();
		running = true;
	}

	public void run() {
		try {
			logger.info("Running filter: " + command);
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			logger.warn("Filter exited with return value: " + p.exitValue());
		} catch (Exception e) {
			logger.error("Error running filter: ", e);
		}
	}

}
