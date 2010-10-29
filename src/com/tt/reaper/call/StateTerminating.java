package com.tt.reaper.call;

import java.util.Iterator;

import com.tt.reaper.message.Message;
import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.sip.CollectorStack;
import com.tt.reaper.vq.LocalMetrics;
import com.tt.reaper.vq.VQSessionReport;

public class StateTerminating extends State {
	public static final StateTerminating instance = new StateTerminating();
	
	private StateTerminating()
	{
	}
	
	State process(CallContext context, Message message)
	{
		switch (message.getType())
		{
		case Message.SUCCESS:
		case Message.FAILURE:
			Iterator<AudioData> it;
			it = context.audioFrom.iterator();
			while (it.hasNext()) {
				AudioData data = it.next();
				data.close();
				LocalMetrics metrics = new LocalMetrics(context, data);
				metrics.setPacketLoss(data.getLossRate());
				metrics.setDelay(data.getJitter());
				VQSessionReport report = new VQSessionReport(metrics);
				CollectorStack.instance.sendMessage(report.toString());				
			}
			it = context.audioTo.iterator();
			while (it.hasNext()) {
				AudioData data = it.next();
				data.close();
				LocalMetrics metrics = new LocalMetrics(context, data);
				metrics.setPacketLoss(data.getLossRate());
				metrics.setDelay(data.getJitter());
				VQSessionReport report = new VQSessionReport(metrics);
				CollectorStack.instance.sendMessage(report.toString());				
			}
			return StateTerminated.instance;
		case Message.INVITE:
		case Message.ACK:
			logger.warn("Unexpected message in terminating: " + message);
			break;
		case Message.BYE:
			break;
		case Message.RTP_PACKET:
			processRtpPacket(context, (RtpPacket)message);
			break;
		}
		return this;
	}
}