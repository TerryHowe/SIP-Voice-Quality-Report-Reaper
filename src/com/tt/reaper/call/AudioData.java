package com.tt.reaper.call;


public class AudioData extends RtpStream {
	public String ipAddy;
	public int rtpPort;
	public String payloadType;
	public String payloadDescription;
	public String sampleRate;
	public boolean from;
	
	public AudioData() {
		ipAddy = "127.0.0.1";
		rtpPort = 404;
		payloadType = "0";
		payloadDescription = "unknown";
		sampleRate = "0";
		from = true;
	}
	
	public boolean equals(String value) {
		if (value.equals(getIpRtpPort()))
			return true;
		return value.equals(getIpRtcpPort());
	}
	
	public String getIpRtpPort() {
		return ipAddy + ":" + rtpPort;
	}
	
	public String getIpRtcpPort() {
		return ipAddy + ":" + (rtpPort + 1);
	}
	
	public String toString() {
		return ipAddy + ":" + rtpPort + " " + payloadType + " " + payloadDescription + " " + sampleRate + "\n" +
		       "            first=" +  getFirst() + " last=" + getLast() + " count=" + getPacketCount() + " lost=" + getPacketLoss() + " dup=" + getDuplicates() + "\n";
	}
}
