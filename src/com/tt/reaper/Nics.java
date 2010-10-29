package com.tt.reaper;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

public class Nics {
	private static Logger logger = Logger.getLogger(Nics.class);

	public static String getIp(String name)
	{
		if (name.equals("lo")) {
			logger.warn("Using loopback interface");
			return "127.0.0.2";
		}
		
		String otherThanIpv4 = null;
		List<String> nicList = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces();
			while (nics.hasMoreElements()) {
				NetworkInterface ifc = nics.nextElement();
				nicList.add(ifc.getDisplayName());
				if (ifc.getDisplayName().equals(name) == false)
					continue;
				if (ifc.isUp()) {
					Enumeration<InetAddress> iface = ifc.getInetAddresses();
					while (iface.hasMoreElements()) {
						InetAddress addr = iface.nextElement();
						if (addr instanceof Inet4Address)
						{
							return addr.getHostAddress();
						}
						else
						{
							otherThanIpv4 = addr.getHostAddress();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.warn("Error getting interfaces: ", e);
		}
		if (otherThanIpv4 != null)
			return otherThanIpv4;
		logger.error("Cannot find interface <" + name + "> known nics are: " + nicList);
		return "127.0.0.1";
	}
}
