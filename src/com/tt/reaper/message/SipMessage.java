package com.tt.reaper.message;

import gov.nist.javax.sdp.MediaDescriptionImpl;
import gov.nist.javax.sdp.fields.AttributeField;
import gov.nist.javax.sdp.fields.MediaField;
import gov.nist.javax.sip.message.SIPMessage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.sdp.SdpFactory;
import javax.sdp.SessionDescription;
import javax.sip.SipFactory;
import javax.sip.address.AddressFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

import com.tt.reaper.Configuration;
import com.tt.reaper.call.AudioData;
import com.tt.reaper.sip.CollectorStack;

public abstract class SipMessage extends Message {
	protected static AddressFactory addressFactory;
	protected static HeaderFactory headerFactory;
	protected static MessageFactory messageFactory;
	protected static SdpFactory sdpFactory;
	private static int fromPort;
	private static String fromHost;
	private static String fromUsername;
	private static String toHost;
	private static String toPort;
	private static String toUsername;
	private static String softwareVersion;
	
	protected SipMessage(int type)
	{
		super(type);
	}
	
	public static boolean initFactory(SipFactory sipFactory, Configuration configuration)
	{
		try {
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			sdpFactory = SdpFactory.getInstance();
			fromPort = configuration.getWritePort();
			fromHost = configuration.getWriteInterface();
			fromUsername = configuration.getWriteUsername();
			toHost = configuration.getCollectorHost();
			toPort = "" + configuration.getCollectorPort();
			toUsername = configuration.getCollectorUsername();
			softwareVersion = configuration.getSoftwareVersion();
			return true;
		}
		catch (Exception e)
		{
			logger.error("Error initializing stack: ", e);
		}
		return false;
	}

	void setSdp(String sdpContent) {
		try {
			ContentTypeHeader contentTypeHeader;
			contentTypeHeader = headerFactory
					.createContentTypeHeader("application", "sdp");
			getMessage().setContent(sdpContent, contentTypeHeader);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected static CallIdHeader getNewCallId() {
		return CollectorStack.instance.getNewCallId();
	}
	
	protected final int getFromPort() {
		return fromPort;
	}

	protected final String getFromHost() {
		return fromHost;
	}

	protected final String getFromUsername() {
		return fromUsername;
	}

	protected final String getToHost() {
		return toHost;
	}

	protected final String getToPort() {
		return toPort;
	}
	
	protected final String getToUsername() {
		return toUsername;
	}

	protected final String getSoftwareVersion() {
		return softwareVersion;
	}
	
	public abstract String getCallId();
	public abstract SIPMessage getMessage();
	
	@SuppressWarnings("unchecked")
	public final ArrayList<AudioData> getAudioData()
	{
		if (getMessage().getRawContent() == null)
			return null;
		ArrayList<AudioData> list = new ArrayList<AudioData>();
		String content = new String(getMessage().getRawContent());
		logger.debug("content=<" + content + ">");
		try {
		    SessionDescription sdp = sdpFactory.createSessionDescription(content);
			String ipAddy = sdp.getConnection().getAddress();
			Vector<MediaDescriptionImpl> descriptors = (Vector<MediaDescriptionImpl>)sdp.getMediaDescriptions(true);
			Iterator<MediaDescriptionImpl> it = descriptors.iterator();
			while (it.hasNext()) {
				MediaDescriptionImpl mediaDescription = it.next();
				MediaField field = mediaDescription.getMediaField();
				if (field == null) {
					logger.warn("Missing media field");
					continue;
				}
				AudioData audio = new AudioData();
				audio.ipAddy = ipAddy;
				audio.rtpPort = field.getPort();
				Vector formats = field.getFormats();
				if (formats == null) {
					list.add(audio);
					continue;
				}
				if (formats.size() < 1) {
					list.add(audio);
					continue;
				}
				audio.payloadType = (String) field.getFormats().get(0);

				Vector attributes = mediaDescription.getAttributeFields();
				if (attributes == null) {
					list.add(audio);
					continue;
				}
				Iterator ait = attributes.iterator();
				while (ait.hasNext()) {
					Object objay = ait.next();
					if (! (objay instanceof AttributeField))
						continue;
					AttributeField afield = (AttributeField)objay;
					if (! "rtpmap".equals(afield.getName()))
						continue;
					String [] parsed = afield.getValue().split("[ /]");
					if (parsed.length < 3)
						continue;
					if (! parsed[0].equals(audio.payloadType))
						continue;
					audio.payloadDescription = parsed[1];
					audio.sampleRate = parsed[2];
				}
				list.add(audio);
				logger.info(getCallId() + " audio=" + audio.toString());
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
