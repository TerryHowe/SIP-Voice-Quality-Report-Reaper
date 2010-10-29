package com.tt.reaper.call;

import com.tt.reaper.message.RtpPacket;

public class RtpPacketFactory {
	String[] lines;
	int current;
	
	void init(byte[] data)
	{
		lines = null;
		if (data == null)
			return;
		lines = new String(data).split("\n");
		current = 0;
	}
	
	RtpPacket getNext()
	{
		if (lines == null)
			return null;
		if (current >= lines.length)
			return null;
		return new RtpPacket(lines[current++]);
	}
}
