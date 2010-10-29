package com.tt.reaper.message;

import javax.sip.message.Request;
import javax.sip.message.Response;

public class MessageFactory {
	public SipMessage create(Request request)
	{
		String eventName = request.getMethod();
		
		if (Request.ACK.equals(eventName))
		{
			return new AckMessage(request);
		}
		if (Request.BYE.equals(eventName))
		{
			return new ByeMessage(request);
		}
		if (Request.CANCEL.equals(eventName))
		{
			return new CancelMessage(request);
		}
		if (Request.INFO.equals(eventName))
		{
			return null;
		}
		if (Request.INVITE.equals(eventName))
		{
			return new InviteMessage(request);
		}
		if (Request.MESSAGE.equals(eventName))
		{
			return null;
		}
		if (Request.NOTIFY.equals(eventName))
		{
			return new NotifyMessage(request);
		}
		if (Request.OPTIONS.equals(eventName))
		{
			return null;
		}
		if (Request.PRACK.equals(eventName))
		{
			return null;
		}
		if (Request.PUBLISH.equals(eventName))
		{
			return new PublishMessage(request);
		}
		if (Request.REFER.equals(eventName))
		{
			return null;
		}
		if (Request.REGISTER.equals(eventName))
		{
			return null;
		}
		if (Request.SUBSCRIBE.equals(eventName))
		{
			return null;
		}
		if (Request.UPDATE.equals(eventName))
		{
			return null;
		}
		
		return null;
	}
	
	public SipMessage create(Response response)
	{
		SipMessage message;
		if ((message = SuccessMessage.create(response)) != null)
			return message;
		if ((message = ProvisionalMessage.create(response)) != null)
			return message;
		if ((message = ErrorMessage.create(response)) != null)
			return message;
		return null;
	}
}
