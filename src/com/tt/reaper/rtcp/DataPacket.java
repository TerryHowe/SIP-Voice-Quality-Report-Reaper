package com.tt.reaper.rtcp;

import java.util.ArrayList;
import java.util.Iterator;

import com.tt.reaper.message.Message;

public class DataPacket extends Message {
	private String sourceMacAddress;
	private String sourceIPAddress;
	private int sourcePort;
	private String destinationMacAddress;
	private String destinationIPAddress;
	private int destinationPort;
	private ArrayList<RtcpPacket> list = new ArrayList<RtcpPacket>();
	
	public DataPacket(byte[] content) {
		super(Message.DATA_PACKET);
		if (content == null)
			return;
        String header = new String(content);
        String[] fields = header.split(";");
        if (fields.length < 6) {
        	logger.error("Failed to parse content");
        	return;
        }
    	sourceMacAddress = fields[0];
    	sourceIPAddress = fields[1];
    	sourcePort = Integer.parseInt(fields[2]);
    	destinationMacAddress = fields[3];
    	destinationIPAddress = fields[4];
    	destinationPort = Integer.parseInt(fields[5]);
        
    	String encodedData = fields[6];
        byte[] rtcpPacket = new byte[encodedData.length()/2];
        int index = 0;
        for (int j=0; j<rtcpPacket.length; j++)
        	rtcpPacket[j] = (byte) Integer.parseInt("" + encodedData.charAt(index++) + encodedData.charAt(index++), 16);

        int offset = 0;
        while (offset < rtcpPacket.length) {
        	RtcpPacket packet = null;
        	switch (RtcpPacket.probePacketType(rtcpPacket, offset)) {
        	case RtcpPacket.TYPE_SENDER_REPORT:
        		packet = new RtcpSenderReport(rtcpPacket, offset);
        		break;
        	case RtcpPacket.TYPE_RECEIVER_REPORT:
        		packet = new RtcpReceiverReport(rtcpPacket, offset);
        		break;
        	case RtcpPacket.TYPE_SOURCE_DESCRIPTION:
        		packet = new RtcpSourceDescription(rtcpPacket, offset);
        		break;
        	case RtcpPacket.TYPE_GOODBYE:
        	case RtcpPacket.TYPE_APPLICATION_DEFINED:
        		packet = new RtcpPacket(rtcpPacket, offset);
        		break;
        	case RtcpPacket.TYPE_EXTENDED_REPORT:
        		packet = new RtcpExtendedReport(rtcpPacket, offset);
        		break;
        	default:
        		logger.error("Unexpected packet type: " + RtcpPacket.probePacketType(rtcpPacket, offset));
        		++offset;
        	}
        	if (packet == null)
        		break;
        	list.add(packet);
        	offset += packet.getLength();
        }
	}

	public Iterator<RtcpPacket> getIterator() {
		return list.iterator();
	}

	public String getSource() {
		return sourceIPAddress + ":" + sourcePort;
	}
	
	public String getDestination() {
		return destinationIPAddress + ":" + destinationPort;
	}
	
	public String toString() {
		return "DataPacket(" + getSource() + "," + getDestination() + ")";
	}

	public String getSourceMac() {
		return sourceMacAddress;
	}

	public String getDestinationMac() {
		return destinationMacAddress;
	}
}
