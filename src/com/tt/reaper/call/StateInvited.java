package com.tt.reaper.call;

import com.tt.reaper.message.InviteMessage;
import com.tt.reaper.message.Message;
import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.message.SipMessage;

class StateInvited extends State {
	public static final StateInvited instance = new StateInvited();
	
	private StateInvited()
	{
	}
	
	State process(CallContext context, Message message)
	{
		switch (message.getType())
		{
		case Message.INVITE:
			InviteMessage invite = (InviteMessage)message;
			context.from = invite.getFrom();
			context.to = invite.getTo();
			context.callId = invite.getCallId();
			context.setAudioFrom(invite.getAudioData());
			logger.debug(context.callId + ": Going to " + this);
			break;
		case Message.PROVISIONAL:
		case Message.SUCCESS:
			context.setAudioTo(((SipMessage)message).getAudioData());
			break;
		case Message.FAILURE:
			return StateTerminated.instance;
		case Message.ACK:
			context.setAudioFrom(((SipMessage)message).getAudioData());
			return StateConnected.instance;
		case Message.CANCEL:
		case Message.BYE:
			return StateTerminated.instance;
		case Message.RTP_PACKET:
			processRtpPacket(context, (RtpPacket)message);
			break;
		}
		return this;
	}
}
