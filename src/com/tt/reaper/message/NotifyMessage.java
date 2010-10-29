package com.tt.reaper.message;

import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Request;

public class NotifyMessage  extends RequestMessage {
	public NotifyMessage(Request request) {
		super(Message.NOTIFY, request);
	}

	public NotifyMessage(String data)
	{
		super(SipMessage.NOTIFY, Request.NOTIFY, getNewCallId());
		init(data);
	}

	private boolean init(String data)
	{
		if (super.init() == false)
			return false;
		try {
			ContentTypeHeader contentTypeHeader;
			contentTypeHeader = headerFactory
					.createContentTypeHeader("application", "text/plain");
			request.setContent(data, contentTypeHeader);
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error adding content: ", e);
		}
		return false;
	}
}
