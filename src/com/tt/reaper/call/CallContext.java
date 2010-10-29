package com.tt.reaper.call;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.tt.reaper.message.Message;
import com.tt.reaper.rtcp.DataPacket;
import com.tt.reaper.vq.LocalMetrics;

public class CallContext {
	protected static Logger logger = Logger.getLogger(CallContext.class);
	public State state = StateInvited.instance;
	public Date startTime;
	public Date endTime;
	public String from;
	public String to;
	public String callId;
	public String fromMac;
	public String toMac;
	public LocalMetrics localMetrics;
	public ArrayList<AudioData> audioFrom = new ArrayList<AudioData>();
	public ArrayList<AudioData> audioTo = new ArrayList<AudioData>();
	
	public CallContext()
	{
		startTime = new Date();
	}

	public boolean process(Message message) {
		State previous = state;
		state = state.process(this, message);
		if (previous != state)
			logger.debug(callId + ": Going to " + state);
		if (state == StateTerminated.instance)
			return false;
		return true;
	}

	public void unregister() {
		Iterator<AudioData> it;
		it = audioFrom.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			CallManager.instance.unregister(data.getIpRtpPort());
			CallManager.instance.unregister(data.getIpRtcpPort());
		}
		it = audioTo.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			CallManager.instance.unregister(data.getIpRtpPort());
			CallManager.instance.unregister(data.getIpRtcpPort());
		}
		return;
	}
	
	public AudioData getAudio(String source)
	{
		Iterator<AudioData> it;
		it = audioTo.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(source))
				return data;
		}
		it = audioFrom.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(source))
				return data;
		}
		return null;
	}
	
	public AudioData getFromAudio(String source, String destination)
	{
		Iterator<AudioData> it;
		it = audioFrom.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(source))
				return data;
		}
		it = audioTo.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(destination))
				return data;
		}
		return null;
	}
	
	public AudioData getToAudio(String source, String destination)
	{
		Iterator<AudioData> it;
		it = audioTo.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(source))
				return data;
		}
		it = audioFrom.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			if (data.equals(destination))
				return data;
		}
		return null;
	}
	
	public void setAudioFrom(ArrayList<AudioData> list) {
		if (list == null)
			return;
		Iterator<AudioData> it;
		it = list.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			data.from = true;
			CallManager.instance.register(this, data.getIpRtpPort());
			CallManager.instance.register(this, data.getIpRtcpPort());
			audioFrom.add(data);
		}
	}

	public void setAudioTo(ArrayList<AudioData> list) {
		if (list == null)
			return;
		Iterator<AudioData> it;
		it = list.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			data.from = false;
			CallManager.instance.register(this, data.getIpRtpPort());
			CallManager.instance.register(this, data.getIpRtcpPort());
			audioTo.add(data);
		}
	}
	
	LocalMetrics createMetrics(DataPacket packet)
	{
		AudioData data = getAudio(packet.getSource());
		if (data == null) {
			logger.warn("Unexpected data packet: " + packet);
			return null;
		}
		
		if (data.from){ 
			setFromMac(packet.getSourceMac());
			setToMac(packet.getDestinationMac());
		}
		else {
			setFromMac(packet.getDestinationMac());
			setToMac(packet.getSourceMac());
		}
		localMetrics = new LocalMetrics(this, data);
		return localMetrics;
	}
	
	public void setFromMac(String mac) {
		fromMac = mac;
	}

	public void setToMac(String mac) {
		toMac = mac;
	}
	
	public String toString()
	{
		String response = callId + " START:" + startTime + " FROM:" + from + " TO:" + to + "\n";
		Iterator<AudioData> it;
		it = audioTo.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			response += "        TO: " + data.toString();
		}
		it = audioFrom.iterator();
		while (it.hasNext()) {
			AudioData data = it.next();
			response += "        FROM: " + data.toString();
		}
		response += "\n";
		return response;
	}
}
