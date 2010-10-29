package com.tt.reaper.http;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import com.sun.net.httpserver.HttpServer;
import com.tt.reaper.call.CallContext;

public class WebServer {
	protected static Logger logger = Logger.getLogger(CallContext.class);
	public static WebServer instance = new WebServer();
	private HttpServer server;
	
	private WebServer()
	{
		
	}
	
	public void init()
	{
		try {
			server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8060), 10);
			server.createContext("/", new WebHandler());
			server.start();
		}
		catch (Exception e)
		{
			logger.error("Error starting web server");
		}
	}
}
