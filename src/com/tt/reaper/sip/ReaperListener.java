package com.tt.reaper.sip;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.apache.log4j.Logger;

import com.tt.reaper.call.CallManager;
import com.tt.reaper.message.MessageFactory;
import com.tt.reaper.message.SipMessage;

class ReaperListener implements SipListener {
	private static Logger logger = Logger.getLogger(ReaperListener.class);
	private MessageFactory factory = new MessageFactory();
	
	ReaperListener()
	{
	}
	
	@Override
	public void processDialogTerminated(DialogTerminatedEvent evt) {
		logger.info("processDialogTerminated");
	}

	@Override
	public void processIOException(IOExceptionEvent evt) {
		logger.info("processIOException");
	}

	@Override
	public void processRequest(RequestEvent evt) {
		logger.debug("ReaperListener processRequest():" + evt.getRequest().getMethod());
		SipMessage message = factory.create(evt.getRequest());
		if (message != null)
			CallManager.instance.send(message);
	}

	@Override
	public void processResponse(ResponseEvent evt) {
		logger.debug("ReaperListener processRequest():" + evt.getResponse().getStatusCode());
		SipMessage message = factory.create(evt.getResponse());
		if (message != null)
			CallManager.instance.send(message);
	}

	@Override
	public void processTimeout(TimeoutEvent evt) {
		logger.info("processTimeout");
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent evt) {
		logger.info("processTransactionTerminated");
	}
}
