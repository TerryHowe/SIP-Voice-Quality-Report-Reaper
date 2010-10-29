package com.tt.reaper.message;

import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Request;

public class PublishMessage extends RequestMessage {
	
	public PublishMessage(String data)
	{
		super(SipMessage.PUBLISH, Request.PUBLISH, getNewCallId());
		status = init(data);
	}
	
	public PublishMessage(Request request) {
		super(SipMessage.PUBLISH, request);
		status = true;
	}

	private boolean init(String data)
	{
		if (super.init() == false)
			return false;
		try {
			ContentTypeHeader contentTypeHeader;
			contentTypeHeader = headerFactory
					.createContentTypeHeader("application", "vq-rtcpxr");
			request.setContent(data, contentTypeHeader);
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error adding content: ", e);
		}
		return false;
	}

	public boolean getStatus() {
		return status;
	}
}
