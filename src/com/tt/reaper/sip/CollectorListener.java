package com.tt.reaper.sip;

import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipListener;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;

import org.apache.log4j.Logger;

import com.tt.reaper.CollectorManager;
import com.tt.reaper.message.MessageFactory;
import com.tt.reaper.message.SipMessage;

class CollectorListener implements SipListener {
	private static Logger logger = Logger.getLogger(CollectorListener.class);
	private MessageFactory factory = new MessageFactory();
	
	CollectorListener() {
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
		SipMessage message = factory.create(evt.getRequest());
		CollectorManager.instance.send(message);
	}

	@Override
	public void processResponse(ResponseEvent evt) {
		SipMessage message = factory.create(evt.getResponse());
		CollectorManager.instance.send(message);
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
