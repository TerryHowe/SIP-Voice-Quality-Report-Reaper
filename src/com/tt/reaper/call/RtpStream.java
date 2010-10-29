package com.tt.reaper.call;

public class RtpStream {
	static final int SIZE = 128;
	private boolean first = true;
	private int firstPacket = 0;
	private int lastPacket = 0;
	private int totalPackets = 0;
	private int packetCount = 0;
	private int packetLoss = 0;
	private int discardCount = 0;
	private int transit = 0;
	private double jitter = 0.0;
	private double minJitter = 0.0;
	private double maxJitter = 0.0;
	private double totalJitter = 0.0;
	private int[] received = new int[SIZE];
	
	public RtpStream()
	{
		for (int j=0; j<SIZE; j++)
			received[j] = 0;
	}
	
	private void open(int packetNumber, int timeStamp, int arrival)
	{
		first = false;
		firstPacket = packetNumber;
		for (int j=packetNumber; j<(packetNumber+SIZE); j++)
			received[j%SIZE] = j & 0xFFFF;
		minJitter = calculateJitter(timeStamp, arrival);
		maxJitter = minJitter;
		transit = 0;
	}
	
	private final int getNextExpected(int packetNumber)
	{
		return (packetNumber + SIZE) & 0xFFFF;
	}
	
	private final void addLost(int expected)
	{
		packetLoss += ((expected - received[expected % SIZE]) & 0xFFFF) / SIZE;
	}
	
	public void close()
	{
		int start;
		if (packetCount < SIZE)
			start = firstPacket;
		else
			start = (lastPacket - SIZE + 1) & 0xFFFF;
		if (start > lastPacket)
		{
			for (int j=start; j<=0xFFFF; j++)
			{
				if (received[j%SIZE] != getNextExpected(j))
					addLost(getNextExpected(j));
			}
			start = 0;
		}
		for (int j=start; j<=lastPacket; j++)
		{
			if (received[j%SIZE] != getNextExpected(j))
				addLost(getNextExpected(j));
		}
	}
	
	public void receive(int packetNumber, int timeStamp, int arrival)
	{
		++totalPackets;
		if (first)
			open(packetNumber, timeStamp, arrival);
		if (received[packetNumber % SIZE] != packetNumber)
		{
			if (received[packetNumber % SIZE] == getNextExpected(packetNumber))
			{
				++discardCount;
				return;
			}
			addLost(packetNumber);
		}
		++packetCount;
		received[packetNumber % SIZE] = getNextExpected(packetNumber);
		lastPacket = packetNumber;
		jitter += calculateJitter(timeStamp, arrival);
		if (jitter < minJitter)
			minJitter = jitter;
		if (jitter > maxJitter)
			maxJitter = jitter;
		totalJitter += jitter;
	}
	
	final double calculateJitter(int timeStamp, int arrival)
	{
		int xit = arrival - timeStamp;
		int d = xit - transit;
		transit = xit;
		if (d < 0)
			d = -d;
		return (1.0 / 16.0) * ((double) d - jitter);
	}
	
	final int getFirst()
	{
		return firstPacket;
	}
	
	final int getLast()
	{
		return lastPacket;
	}

	public int getPacketCount()
	{
		return packetCount;
	}
	
	public int getPacketLoss()
	{
		return packetLoss;
	}

	final int getDuplicates() {
		return discardCount;
	}

	public double getLossRate() {
		if (packetCount == 0)
			return 0.0;
		return((256.0 * (double)packetLoss) / (double)packetCount);
	}

	public double getDiscardRate() {
		if (packetCount == 0)
			return 0.0;
		return((256.0 * (double)discardCount) / (double)packetCount);
	}
	
	public int getJitter() {
		return (int)jitter;
	}
	
	public int getMinJitter() {
		return (int) minJitter;
	}
	
	public int getMaxJitter() {
		return (int) maxJitter;
	}
	
	public int getMeanJitter() {
		return (int)(totalJitter / (long)packetCount);
	}
}
