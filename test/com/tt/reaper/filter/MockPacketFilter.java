package com.tt.reaper.filter;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.tt.reaper.Configuration;
import com.tt.reaper.message.NotifyMessage;
import com.tt.reaper.rtcp.TestRtcpPacket;

public class MockPacketFilter {
	Socket socket;
	InetAddress addy;
	OutputStream stream;
	
	public MockPacketFilter() {
		try {
			Configuration configuration = new Configuration();
			addy = InetAddress.getByName("127.0.0.1");
			socket = new Socket(addy, configuration.getReadPort());
			stream = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendSip(String fileName) {
		try {
			byte[] buffer = new byte[2048];
			FileInputStream in = new FileInputStream(fileName);
			int length = in.read(buffer);
			in.close();
			stream.write(buffer, 0, length);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendRtcp(String fileName, String fromAddy, int fromPort, String toAddy, int toPort) {
		try {
			NotifyMessage notify = TestRtcpPacket.createNotifyDataMessage(fileName, fromAddy, fromPort, toAddy, toPort);
			String buffer = notify.getRequest().toString();
			stream.write(buffer.getBytes(), 0, buffer.length());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
