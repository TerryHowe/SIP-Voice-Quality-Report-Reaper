package com.tt.reaper.call;

import com.tt.reaper.message.Message;
import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.message.SipMessage;
import com.tt.reaper.rtcp.DataPacket;

public class StateConnected extends State {
	public static final StateConnected instance = new StateConnected();
	
	private StateConnected()
	{
	}
	
	State process(CallContext context, Message message)
	{
		switch (message.getType())
		{
		case Message.RTP_PACKET:
			processRtpPacket(context, (RtpPacket)message);
			break;
		case Message.DATA_PACKET:
			processDataPacket(context, (DataPacket)message);
			break;
		case Message.SUCCESS:
		case Message.FAILURE:
			break;
		case Message.INVITE:
		case Message.ACK:
			context.setAudioFrom(((SipMessage)message).getAudioData());
			logger.warn("Unexpected message in connected: " + message);
			break;
		case Message.BYE:
			return StateTerminating.instance;
		}
		return this;
	}
}