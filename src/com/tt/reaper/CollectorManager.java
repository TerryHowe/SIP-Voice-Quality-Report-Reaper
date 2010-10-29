package com.tt.reaper;

import org.apache.log4j.Logger;

import com.tt.reaper.message.Message;
import com.tt.reaper.message.MessageQueue;
import com.tt.reaper.message.RequestMessage;
import com.tt.reaper.message.ResponseMessage;
import com.tt.reaper.sip.CollectorStack;

public class CollectorManager extends Thread {
	private static Logger logger = Logger.getLogger(CollectorManager.class);
	public static CollectorManager instance = new CollectorManager();
	private MessageQueue queue = new MessageQueue();
	private boolean initialized = false;
	
	private CollectorManager() {
		super("CollectorManager");
	}
	
	public synchronized void init() {
		if (initialized == true)
			return;
		initialized = true;
		start();
	}
	
	public void run()
	{
		Message message;
		while ((message = queue.getBlocking()) != null) {
			process(message);
		}
	}
	
	void process(Message message) {
		logger.info("processRequest: " + message);
		if (message instanceof RequestMessage) {
			RequestMessage request = (RequestMessage)message;
			CollectorStack.instance.sendResponse(new ResponseMessage(request.getRequest()));
		}
	}

	public void send(Message message) {
		queue.add(message);
	}
}
