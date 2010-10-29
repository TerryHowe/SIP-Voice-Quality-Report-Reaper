package com.tt.reaper.call;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tt.reaper.message.DataRequest;
import com.tt.reaper.message.DataResponse;
import com.tt.reaper.message.InviteMessage;
import com.tt.reaper.message.Message;
import com.tt.reaper.message.MessageQueue;
import com.tt.reaper.message.NotifyMessage;
import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.message.SipMessage;

public class CallManager extends Thread {
	protected static Logger logger = Logger.getLogger(CallManager.class);
	public static CallManager instance = new CallManager();
	private HashMap<String, CallContext> callIdMap = new HashMap<String, CallContext>();
	private HashMap<String, CallContext> rtcpMap = new HashMap<String, CallContext>();
	private MessageQueue queue = new MessageQueue();
	private RtpPacketFactory factory = new RtpPacketFactory();
	private boolean initialized = false;

	private CallManager() {
		super("CallManager");
	}
	
	public synchronized void init() {
		if (initialized == true)
			return;
		initialized = true;
		start();
	}
	
	public void run()
	{
		logger.info("Call manager started");
		Message message;
		while ((message = queue.getBlocking()) != null) {
			process(message);
		}
		logger.warn("Call manager finished");
	}
	
	void process(Message message)
	{
		try {
			if (message instanceof NotifyMessage) {
				RtpPacket packet;
				factory.init(((NotifyMessage)message).getRequest().getRawContent());
				while ((packet = factory.getNext()) != null)
				{
					CallContext context;
					if ((context = rtcpMap.get(packet.getSource())) != null) {
						logger.debug("Found source: " + packet.getSource());
						if (context.process(packet) == false) {
							logger.warn("Removing rtcp map that should of been removed");
							rtcpMap.remove(packet.getSource());
						}
					}
					else if ((context = rtcpMap.get(packet.getDestination())) != null) {
						logger.debug("Found destination: " + packet.getDestination());
						if (context.process(packet) == false) {
							logger.warn("Removing rtcp map that should of been removed");
							rtcpMap.remove(packet.getDestination());
						}
					}
					else {
						logger.warn("RTCP stream not found: " + packet);
					}
				}
			}
			if (message instanceof SipMessage) {
				SipMessage sipMessage = (SipMessage)message;
				CallContext context = callIdMap.get(sipMessage.getCallId());
				if (context == null) {
					if (! (sipMessage instanceof InviteMessage))
						return;
					context = new CallContext();
					callIdMap.put(sipMessage.getCallId(), context);
					logger.info("Creating call context: " + sipMessage.getCallId());
				}
				if (context.process(sipMessage) == false) {
					callIdMap.remove(context.callId);
					context.unregister();
				}
			}
			else if (message instanceof DataRequest) {
				DataRequest request = (DataRequest)message;
				request.queue.add(new DataResponse(toString()));
			}
			else {
				logger.error("Unexpected message: " + message);
			}
		}
		catch (Exception e) {
			logger.error("Error processing packet: ", e);
		}
	}

	public CallContext getContextCallId(String callId) {
		return callIdMap.get(callId);
	}
	
	final void register(CallContext context, String ipPort)
	{
		logger.debug("Registering " + ipPort);
		rtcpMap.put(ipPort, context);
	}
	
	public final CallContext getContextRtcp(String ipPort)
	{
		return rtcpMap.get(ipPort);
	}
	
	public final CallContext getContextRtcp(String ip, int port)
	{
		return rtcpMap.get(ip + ":" + port);
	}
	
	final void unregister(String ipPort)
	{
		logger.debug("Unregistering " + ipPort);
		rtcpMap.remove(ipPort);
	}
	
	final void clearMaps()
	{
		callIdMap.clear();
		rtcpMap.clear();
	}

	public void send(Message message) {
		queue.add(message);
	}
	
	public String toString()
	{
		String response = "";
		Collection<CallContext> collection = callIdMap.values();
		Iterator<CallContext> it = collection.iterator();
		while (it.hasNext()) {
			CallContext context = it.next();
			response += context.toString();
		}
		return response;
	}
}
