package com.tt.reaper.message;


public class RtpPacket extends Message {
	private String sourceMacAddress;
	private String sourceIPAddress;
	private int sourcePort;
	private String destinationMacAddress;
	private String destinationIPAddress;
	private int destinationPort;
	private int sequenceNumber;
	private int timeStamp;
	private int arrival;
	
	public RtpPacket(String value) {
		super(Message.RTP_PACKET);
        String[] fields = value.split(";");
        if (fields.length < 9) {
        	logger.error("Failed to parse content");
        	return;
        }
    	sourceMacAddress = fields[0];
    	sourceIPAddress = fields[1];
    	sourcePort = Integer.parseInt(fields[2]);
    	destinationMacAddress = fields[3];
    	destinationIPAddress = fields[4];
    	destinationPort = Integer.parseInt(fields[5]);
    	sequenceNumber = Integer.parseInt(fields[6]) & 0xFFFF;
    	timeStamp = Integer.parseInt(fields[7]);
    	arrival = Integer.parseInt(fields[8]);
	}

	public String getSource() {
		return sourceIPAddress + ":" + sourcePort;
	}
	
	public String getDestination() {
		return destinationIPAddress + ":" + destinationPort;
	}
	
	public String toString() {
		return "RtpPacket(" + getSource() + "," + getDestination() + ")";
	}

	public String getSourceMac() {
		return sourceMacAddress;
	}

	public String getDestinationMac() {
		return destinationMacAddress;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public int getArrival() {
		return arrival;
	}
}
