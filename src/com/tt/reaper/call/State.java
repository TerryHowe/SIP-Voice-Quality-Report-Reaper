package com.tt.reaper.call;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tt.reaper.message.Message;
import com.tt.reaper.message.RtpPacket;
import com.tt.reaper.rtcp.DataPacket;
import com.tt.reaper.rtcp.RtcpExtendedReport;
import com.tt.reaper.rtcp.RtcpPacket;
import com.tt.reaper.rtcp.RtcpReceiverReport;
import com.tt.reaper.rtcp.RtcpSenderReport;
import com.tt.reaper.rtcp.VoipMetricsExtendedReportBlock;
import com.tt.reaper.sip.CollectorStack;
import com.tt.reaper.vq.LocalMetrics;
import com.tt.reaper.vq.VQIntervalReport;

public abstract class State {
	protected static Logger logger = Logger.getLogger(State.class);
	
	protected State()
	{
	}
	
	public void processRtpPacket(CallContext context, RtpPacket packet) {
		AudioData data;
		data = context.getAudio(packet.getSource());
		if (data == null)
			return;
		data.receive(packet.getSequenceNumber(), packet.getTimeStamp(), packet.getArrival());
	}
	
	public void processDataPacket(CallContext context, DataPacket packet) {
		Iterator<RtcpPacket> it = packet.getIterator();
		while (it.hasNext()) {
			RtcpPacket rtcp = it.next();
			switch (rtcp.getPacketType()) {
			case RtcpPacket.TYPE_SOURCE_DESCRIPTION:
				break;
			case RtcpPacket.TYPE_SENDER_REPORT:
				RtcpSenderReport sender = (RtcpSenderReport)rtcp;
				for (int j=0; j<sender.getCount(); j++) {
					LocalMetrics metrics = context.createMetrics(packet);
					if (metrics == null)
						break;
					metrics.setMetrics(sender.getReport(j));
					CollectorStack.instance.sendMessage(new VQIntervalReport(metrics).toString());
				}
				break;
			case RtcpPacket.TYPE_RECEIVER_REPORT:
				RtcpReceiverReport receiver = (RtcpReceiverReport)rtcp;
				for (int j=0; j<receiver.getCount(); j++) {
					LocalMetrics metrics = context.createMetrics(packet);
					if (metrics == null)
						break;
					metrics.setMetrics(receiver.getReport(j));
					CollectorStack.instance.sendMessage(new VQIntervalReport(metrics).toString());
				}
				break;
			case RtcpPacket.TYPE_GOODBYE:
				// Unregister
				break;
			case RtcpPacket.TYPE_EXTENDED_REPORT:
				RtcpExtendedReport extendedReport = (RtcpExtendedReport)rtcp;
				Iterator<VoipMetricsExtendedReportBlock> eit = extendedReport.getIterator();
				while (eit.hasNext()) {
					LocalMetrics metrics = context.createMetrics(packet);
					if (metrics == null)
						break;
					metrics.setMetrics(eit.next());
					CollectorStack.instance.sendMessage(new VQIntervalReport(metrics).toString());
				}
				break;
			case RtcpPacket.TYPE_APPLICATION_DEFINED:
				break;
			}
		}
	}
	
	public String toString()
	{
		String name = getClass().getName();
		return name.substring(name.lastIndexOf('.')+1);
	}
	
	abstract State process(CallContext context, Message message);
}
