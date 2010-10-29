package com.tt.reaper.sip;

import javax.sip.ListeningPoint;
import javax.sip.ServerTransaction;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.header.CallIdHeader;

import org.apache.log4j.Logger;

import com.tt.reaper.Configuration;
import com.tt.reaper.message.PublishMessage;
import com.tt.reaper.message.RequestMessage;
import com.tt.reaper.message.ResponseMessage;

public class CollectorStack {
	private static final String STACK_NAME = "CollectorStack";
	private static Logger logger = Logger.getLogger(CollectorStack.class);
	public static CollectorStack instance = new CollectorStack();
	private SipProvider collectorProvider;
	private static boolean initialized = false;
	public String lastSendData;
	
	private CollectorStack()
	{
	}
	
	public synchronized boolean init()
	{
		if (initialized == true)
			return true;
		initialized = true;
		logger.info("Starting the collector stack...");
		try {
			SipStack sipStack;
			Configuration configuration;
			configuration = new Configuration();
			configuration.setStackName(STACK_NAME);
			SipFactory.getInstance().setPathName("gov.nist");
			sipStack = SipFactory.getInstance().createSipStack(configuration);
			RequestMessage.initFactory(SipFactory.getInstance(), configuration);
			ListeningPoint reaperUdp = sipStack.createListeningPoint(configuration.getWriteInterface(), configuration.getWritePort(), "udp");
			collectorProvider = sipStack.createSipProvider(reaperUdp);
			collectorProvider.addSipListener(new CollectorListener());
			collectorProvider.setAutomaticDialogSupportEnabled(false);
			logger.info("Collector SIP stack initialized successfully");
		}
		catch (Exception e)
		{
			logger.error("Error initializing stack: ", e);
			return false;
		}
		return true;
	}
	
	public boolean sendResponse(ResponseMessage response) {
		try {
			ServerTransaction st = collectorProvider.getNewServerTransaction(response.getRequest());
			st.sendResponse(response.getResponse());
		}
		catch (Exception e) {
			logger.error("Error sending response: ", e);
		}
		return true;
	}
	
	public boolean sendMessage(String data) {
		lastSendData = data;
		try {
			PublishMessage publish = new PublishMessage(data);
			collectorProvider.sendRequest(publish.getRequest());
		}
		catch (Exception e)
		{
			logger.error("Error sending message: ", e);
			return false;
		}
		return true;
	}
	
	public CallIdHeader getNewCallId() {
		logger.info("Get new call id: " + collectorProvider);
		return collectorProvider.getNewCallId();
	}
}
