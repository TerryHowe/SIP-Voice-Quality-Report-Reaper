package com.tt.reaper.sip;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;

import org.apache.log4j.Logger;

import com.tt.reaper.Configuration;
import com.tt.reaper.message.RequestMessage;

public class ReaperStack {
	private static final String STACK_NAME = "ReaperStack";
	private static Logger logger = Logger.getLogger(ReaperStack.class);
	public static ReaperStack instance = new ReaperStack();
	private SipProvider reaperProvider;
	private static boolean initialized = false;
	
	private ReaperStack()
	{
	}
	
	public synchronized boolean init()
	{
		if (initialized == true)
			return true;
		initialized = true;
		logger.info("Starting the reaper stack...");
		try {
			SipStack sipStack;
			Configuration configuration;
			configuration = new Configuration();
			configuration.setStackName(STACK_NAME);
			SipFactory.getInstance().setPathName("gov.nist");
			sipStack = SipFactory.getInstance().createSipStack(configuration);
			RequestMessage.initFactory(SipFactory.getInstance(), configuration);
			ListeningPoint reaperTcp = sipStack.createListeningPoint("127.0.0.1", configuration.getReadPort(), "tcp");
			reaperProvider = sipStack.createSipProvider(reaperTcp);
			reaperProvider.addSipListener(new ReaperListener());
			reaperProvider.setAutomaticDialogSupportEnabled(false);
			logger.info("Reaper SIP stack initialized successfully");
		}
		catch (Exception e)
		{
			logger.error("Error initializing stack: ", e);
			return false;
		}
		return true;
	}
}
