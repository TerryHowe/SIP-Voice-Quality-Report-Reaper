package com.tt.reaper;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Configuration extends Properties {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static final String FILE_NAME = "config/reaper.properties";
	public static final String STACK_NAME = "javax.sip.STACK_NAME";
	public static final String STACK_TRACE_LEVEL = "gov.nist.javax.sip.TRACE_LEVEL";
	public static final String STACK_SERVER_LOG = "gov.nist.javax.sip.SERVER_LOG";
	public static final String STACK_DEBUG_LOG = "gov.nist.javax.sip.DEBUG_LOG";
	private static final String READ_INTERFACE = "readInterface";
	private static final String READ_PORT = "readPort";
	private static final String WRITE_INTERFACE = "writeInterface";
	private static final String FROM_PORT = "fromPort";
	private static final String FROM_USERNAME = "fromUsername";
	private static final String COLLECTOR_HOST = "collectorHost";
	private static final String COLLECTOR_PORT = "collectorPort";
	private static final String COLLECTOR_USERNAME = "collectorUsername";
	private static final String COMMAND_ARGUMENTS = "commandArguments";
	private static final String SOFTWARE_VERSION = "softwareVersion";

	public Configuration()
	{
		try {
			FileInputStream in = new FileInputStream(FILE_NAME);
			load(in);
			in.close();
		}
		catch (Exception e)
		{
			logger.error("Error reading properties file");
		}
	}
	
	public void setStackName(String value) {
		setProperty(STACK_NAME, value);
	}
	
	public String getStackTraceLevel() {
		return getProperty(Configuration.STACK_TRACE_LEVEL, "0");
	}
	
	public String getStackServerLog() {
		return getProperty(Configuration.STACK_SERVER_LOG, "/dev/null");
	}
	
	public String getStackDebugLog() {
		return getProperty(Configuration.STACK_DEBUG_LOG, "/dev/null");
	}

	public String getReadInterface() {
		return getProperty(Configuration.READ_INTERFACE, "eth0");
	}
	

	public int getReadPort() {
		try {
			return Integer.parseInt(getProperty(Configuration.READ_PORT, "5050"));
		}
		catch (Exception e)
		{
			logger.error("Error reading port configuration: ", e);
		}
		return 5050;
	}

	public String getWriteInterface() {
		return Nics.getIp(getProperty(Configuration.WRITE_INTERFACE, "lo"));
	}

	public int getWritePort() {
		try {
			return Integer.parseInt(getProperty(Configuration.FROM_PORT, "5060"));
		}
		catch (Exception e)
		{
			logger.error("Error reading port configuration: ", e);
		}
		return 5060;
	}
	
	public String getWriteUsername() {
		return getProperty(Configuration.FROM_USERNAME, "reaper");
	}

	public String getCollectorHost() {
		return getProperty(Configuration.COLLECTOR_HOST, "127.0.0.3");
	}
	
	public int getCollectorPort() {
		try {
			return Integer.parseInt(getProperty(Configuration.COLLECTOR_PORT, "5060"));
		}
		catch (Exception e)
		{
			logger.error("Error reading port configuration: ", e);
		}
		return 5060;
	}

	public String getCollectorUsername() {
		return getProperty(Configuration.COLLECTOR_USERNAME, "collector");
	}

	public String getCommand() {
		return "/opt/reaper/bin/filter.sh " + getProperty(Configuration.COMMAND_ARGUMENTS, " -n -e ") + " -i " + getReadInterface();
	}

	public String getSoftwareVersion() {
		return getProperty(Configuration.SOFTWARE_VERSION, "reaperv1.0");
	}
}
