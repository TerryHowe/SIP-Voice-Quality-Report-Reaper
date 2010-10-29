package com.tt.reaper.http;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.tt.reaper.call.CallManager;
import com.tt.reaper.message.DataRequest;
import com.tt.reaper.message.DataResponse;
import com.tt.reaper.message.Message;
import com.tt.reaper.message.MessageQueue;

public class WebHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange xchange) throws IOException {
		String body = "<title>Active Calls</title><h1>Active Calls</h1>";
		body += "<pre>";
		MessageQueue queue = new MessageQueue();
		CallManager.instance.send(new DataRequest(queue));
		Message response = queue.getBlocking();
		if (response instanceof DataResponse)
		{
			body += ((DataResponse)response).data.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		body += "</pre>";
		xchange.sendResponseHeaders(200, body.length());
		OutputStream outputStream = xchange.getResponseBody();
		outputStream.write(body.getBytes());
		outputStream.close();
	}
}
