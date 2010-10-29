package com.tt.reaper.message;

import gov.nist.javax.sip.message.SIPMessage;

import java.util.ArrayList;

import javax.sip.address.Address;
import javax.sip.address.SipURI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

public class RequestMessage extends SipMessage {
	protected boolean status = false;
	Request request;
	String message;
	CallIdHeader callIdHeader;
	
	RequestMessage(int type, String message, CallIdHeader callIdHeader)
	{
		super(type);
		this.message = message;
		this.callIdHeader = callIdHeader;
	}

	RequestMessage(int type, Request request)
	{
		super(type);
		this.request = request;
	}
	
	boolean init()
	{
		try {
			SipURI from = addressFactory.createSipURI(getFromUsername(), getFromHost()
					+ ":" + getFromPort());
			Address fromNameAddress = addressFactory.createAddress(from);
			fromNameAddress.setDisplayName(getFromUsername());
			FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
					getSoftwareVersion());
	
			SipURI toAddress = addressFactory.createSipURI(getToUsername(), getToHost()
					+ ":" + getToPort());
			Address toNameAddress = addressFactory.createAddress(toAddress);
			toNameAddress.setDisplayName(getToUsername());
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress, null);
	
			SipURI requestURI = addressFactory.createSipURI(getToUsername(), getToHost()
					+ ":" + getToPort());
			requestURI.setTransportParam("udp");
	
			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = headerFactory.createViaHeader(getFromHost(),
					getFromPort(), "udp", null);
			viaHeaders.add(viaHeader);

			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, message);
	
			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);
			request = messageFactory.createRequest(requestURI,
					message, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
	
			SipURI contactURI = addressFactory.createSipURI(getFromUsername(),
					getFromHost());
			contactURI.setPort(getFromPort());
			Address contactAddress = addressFactory.createAddress(contactURI);
			contactAddress.setDisplayName(getFromUsername());
			ContactHeader contactHeader = headerFactory
					.createContactHeader(contactAddress);
			request.addHeader(contactHeader);
		}
		catch (Exception e)
		{
			logger.error("Error initializing stack: ", e);
			return false;
		}
		return true;
	}

	public final Request getRequest() {
		return request;
	}
	
	public final String getFrom()
	{
		FromHeader header = (FromHeader) request.getHeader(FromHeader.NAME);
		if (header == null)
			return "null";
		return header.getAddress().toString();
	}
	
	public final String getTo()
	{
		ToHeader header = (ToHeader) request.getHeader(ToHeader.NAME);
		if (header == null)
			return "null";
		return header.getAddress().toString();
	}
	
	public final String getCallId()
	{
		CallIdHeader header = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
		if (header == null)
			return "null";
		return header.getCallId();
	}
	
	public final SIPMessage getMessage() {
		return (SIPMessage)request;
	}
}
