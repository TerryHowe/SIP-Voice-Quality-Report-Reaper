package com.tt.reaper.call;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestRtpStream {
	RtpStream stream;
	
	@Before
	public void setUp()
	{
		stream = new RtpStream();	
	}
	
	@Test
	public void testSimple()
	{
		stream.receive(22, 10, 110);
		stream.receive(23, 10, 10);
		stream.receive(25, 10, 110);
		stream.receive(25, 10, 10);
		stream.receive(25, 10, 110);
		stream.receive(26, 10, 10);
		stream.close();
		
		assertEquals(22, stream.getFirst());
		assertEquals(26, stream.getLast());
		assertEquals(2, stream.getDuplicates());
		assertEquals(1, stream.getPacketLoss());
		assertEquals(4, stream.getPacketCount());
		assertEquals(64.0, stream.getLossRate(), 0.001);
		assertEquals(128.0, stream.getDiscardRate(), 0.001);
	}
	
	@Test
	public void testJitterOnePacket()
	{
		stream.receive(22, 10, 110);
		assertEquals(6, stream.getJitter());
		assertEquals(6, stream.getMinJitter());
		assertEquals(6, stream.getMaxJitter());
		assertEquals(6, stream.getMeanJitter());
	}
	
	@Test
	public void testJitter()
	{
		for (int j=0; j<1000; j++)
		{
			if ((j % 2) == 0)
				stream.receive(22, 10, 110);
			else
				stream.receive(23, 10, 10);
		}
		
		assertEquals(12, stream.getJitter());
		assertEquals(6, stream.getMinJitter());
		assertEquals(12, stream.getMaxJitter());
		assertEquals(9, stream.getMeanJitter());
	}
	
	@Test
	public void testEdgeOne()
	{
		stream.receive(RtpStream.SIZE-1, 10, 11);
		stream.receive(RtpStream.SIZE, 10, 11);
		stream.close();
		assertEquals(0, stream.getPacketLoss());
		assertEquals(2, stream.getPacketCount());
		assertEquals(RtpStream.SIZE-1, stream.getFirst());
		assertEquals(RtpStream.SIZE, stream.getLast());
		assertEquals(0, stream.getDuplicates());
		assertEquals(0.0, stream.getLossRate(), 0.001);
		assertEquals(0.0, stream.getDiscardRate(), 0.001);
	}
	
	@Test
	public void testEdgeTwo()
	{
		stream.receive(RtpStream.SIZE, 10, 11);
		stream.close();
		assertEquals(0, stream.getPacketLoss());
		assertEquals(1, stream.getPacketCount());
		assertEquals(RtpStream.SIZE, stream.getFirst());
		assertEquals(RtpStream.SIZE, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testEdgeThree()
	{
		stream.receive(RtpStream.SIZE-1, 10, 11);
		stream.close();
		assertEquals(0, stream.getPacketLoss());
		assertEquals(1, stream.getPacketCount());
		assertEquals(RtpStream.SIZE-1, stream.getFirst());
		assertEquals(RtpStream.SIZE-1, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testBigOne()
	{
		for (int j=111; j<2000; j++)
		{
			switch (j)
			{
			case 144:
			case 202:
			case 888:
			case 998:
				continue;
			}
			stream.receive(j, 10, 11);
		}
		stream.close();
		assertEquals(4, stream.getPacketLoss());
		assertEquals(1885, stream.getPacketCount());
		assertEquals(111, stream.getFirst());
		assertEquals(1999, stream.getLast());
		assertEquals(0, stream.getDuplicates());
		assertEquals(0.543, stream.getLossRate(), 0.001);
		assertEquals(0.0, stream.getDiscardRate(), 0.001);
	}
	
	@Test
	public void testBigTwo()
	{
		for (int j=0; j<5022; j++)
		{
			switch (j)
			{
			case 144:
			case 244:
			case 344:
				continue;
			}
			stream.receive(j, 10, 11);
		}
		stream.close();
		assertEquals(3, stream.getPacketLoss());
		assertEquals(5019, stream.getPacketCount());
		assertEquals(0, stream.getFirst());
		assertEquals(5021, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testBigThree()
	{
		for (int j=0; j<430; j++)
		{
			switch (j)
			{
			case (RtpStream.SIZE+44):
			case (RtpStream.SIZE*2+44):
			case (RtpStream.SIZE*3+44):
				continue;
			}
			stream.receive(j, 10, 11);
		}
		stream.close();
		assertEquals(3, stream.getPacketLoss());
		assertEquals(427, stream.getPacketCount());
		assertEquals(0, stream.getFirst());
		assertEquals(429, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testShortOne()
	{
		stream.receive(0xFFFF, 10, 11);
		for (int j=0; j<400; j++)
		{
			switch (j)
			{
			case 144:
			case 244:
			case 344:
				continue;
			}
			stream.receive(j, 10, 11);
		}
		stream.close();
		assertEquals(3, stream.getPacketLoss());
		assertEquals(398, stream.getPacketCount());
		assertEquals(0xFFFF, stream.getFirst());
		assertEquals(399, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testShortTwo()
	{
		for (int j=0xFF00; j<=400+0xFFFF; j++)
		{
			switch (j & 0xFFFF)
			{
			case 0xFF0F:
			case 244:
			case 344:
				continue;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		stream.close();
		assertEquals(3, stream.getPacketLoss());
		assertEquals(653, stream.getPacketCount());
		assertEquals(0xFF00, stream.getFirst());
		assertEquals(399, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testShortThree()
	{
		for (int j=0xFF00; j<=(1+0xFFFF); j++)
		{
			switch (j & 0xFFFF)
			{
			case 0xFF0F:
			case 0xFFF0:
				continue;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		stream.close();
		assertEquals(2, stream.getPacketLoss());
		assertEquals(255, stream.getPacketCount());
		assertEquals(0xFF00, stream.getFirst());
		assertEquals(0, stream.getLast());
		assertEquals(0, stream.getDuplicates());
	}
	
	@Test
	public void testLong()
	{
		for (int j=0xFF00; j<=0xFFFF; j++)
		{
			switch (j & 0xFFFF)
			{
			case 0xFF0F:
			case 0xFFF0:
				continue;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		for (int j=0; j<=0xFFFF; j++)
		{
			switch (j & 0xFFFF)
			{
			case 128:
			case 129:
				continue;
			case 555:
			case 1000:
				stream.receive(j & 0xFFFF, 10, 11);
				break;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		for (int j=0; j<=10; j++)
		{
			switch (j & 0xFFFF)
			{
			case 9:
				continue;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		stream.close();
		assertEquals(5, stream.getPacketLoss());
		assertEquals(65798, stream.getPacketCount());
		assertEquals(0xFF00, stream.getFirst());
		assertEquals(10, stream.getLast());
		assertEquals(2, stream.getDuplicates());
	}
	
	@Test
	public void testSuperLong()
	{
		for (int j=4; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
		{
			switch (j & 0xFFFF)
			{
			case 128:
			case 129:
				continue;
			case 555:
			case 1000:
				stream.receive(j & 0xFFFF, 10, 11);
				break;
			}
			stream.receive(j & 0xFFFF, 10, 11);
		}
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=0xFFFF; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		for (int j=0; j<=128; j++)
			stream.receive(j & 0xFFFF, 10, 11);
		stream.close();
		assertEquals(2, stream.getPacketLoss());
		assertEquals(852091, stream.getPacketCount());
		assertEquals(4, stream.getFirst());
		assertEquals(128, stream.getLast());
		assertEquals(2, stream.getDuplicates());
		assertEquals(0, stream.getJitter());
	}
}
