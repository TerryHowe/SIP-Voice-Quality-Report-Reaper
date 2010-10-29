package com.tt.reaper.sip;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.tt.reaper.Configuration;

public class MockCollector extends Thread {
	public static final MockCollector instance = new MockCollector();
	DatagramSocket socket;
	byte[] buffer = new byte[2048];
	Configuration configuration = new Configuration();
	int port;
	InetAddress addy;
	public String message;
	
	private MockCollector() {
		try {
			port = configuration.getWritePort();
			addy = InetAddress.getByName(configuration.getWriteInterface());
			socket = new DatagramSocket(configuration.getCollectorPort(), InetAddress.getByName(configuration.getCollectorHost()));
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
	}
	
	public void run() {
		int count = 0;
		DatagramPacket packet= new DatagramPacket(new byte[2048], 0, 2048, addy, port);
		while (count++ < 100) {
			try {
				socket.receive(packet);
				message = new String(packet.getData(), 0, packet.getLength());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendResponse(String fileName) {
		try {
			FileInputStream in = new FileInputStream(fileName);
			int length = in.read(buffer);
			in.close();
			DatagramPacket packet;
			packet = new DatagramPacket(buffer, 0, length, addy, port);
			socket.send(packet);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
