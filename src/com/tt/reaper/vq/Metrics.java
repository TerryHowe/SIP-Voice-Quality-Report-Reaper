package com.tt.reaper.vq;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.tt.reaper.call.AudioData;
import com.tt.reaper.call.CallContext;
import com.tt.reaper.rtcp.ReportBlock;
import com.tt.reaper.rtcp.VoipMetricsExtendedReportBlock;

public class Metrics {
	String metrics;
	
	protected Metrics(String name)
	{
		metrics = name + ":\r\n";
	}
	
	protected Metrics(String name, CallContext context, AudioData data) {
		metrics = name + ":\r\n";
		if (data.from) {
			context.endTime = new Date();
			setSessionDescription(data.payloadType, data.payloadDescription, data.sampleRate);
			setTimeStamps(context.startTime, context.endTime);
			setCallID(context.callId);
			setFromID(context.from);
			setToID(context.to);
			setOrigID(context.from);
			setLocalAddr(context.from);
			setLocalMac(context.fromMac);
			setRemoteAddr(context.to);
			setRemoteMac(context.toMac);
		}
		else {
			if (context.endTime == null)
				context.endTime = new Date();
			setSessionDescription(data.payloadType, data.payloadDescription, data.sampleRate);
			setTimeStamps(context.startTime, context.endTime);
			setCallID(context.callId);
			setFromID(context.from);
			setToID(context.to);
			setOrigID(context.from);
			setLocalAddr(context.to);
			setLocalMac(context.toMac);
			setRemoteAddr(context.from);
			setRemoteMac(context.fromMac);
		}
	}
	public static String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
	
	public void setSessionDescription(String type, String description, String sample) {
		if ((type == null) && (description == null) && (sample == null))
			return;
		metrics += "SessionDesc:";
		if (type != null)
			metrics += "PT=" + type;
		if (description != null)
			metrics += " PD=" + description;
		if (sample != null)
			metrics += " SR="+ sample;
		metrics += "\r\n";
	}
	
	public void setTimeStamps(Date startTime, Date endTime) {
		metrics += "Timestamps:START=" + formatDate(startTime) + " STOP=" + formatDate(endTime) + "\r\n";
	}
	
	public void setCallID(String value)
	{
		if (value == null)
			return;
		metrics += "CallID:" + value + "\r\n";
	}
	
	public void setFromID(String value)
	{
		if (value == null)
			return;
		metrics += "FromID:" + value + "\r\n";
	}
	
	public void setToID(String value)
	{
		if (value == null)
			return;
		metrics += "ToID:" + value + "\r\n";
	}
	
	public void setOrigID(String value)
	{
		if (value == null)
			return;
		metrics += "OrigID:" + value + "\r\n";
	}
	
	public void setLocalAddr(String value)
	{
		if (value == null)
			return;
		metrics += "LocalAddr:" + value + "\r\n";
	}
	
	public void setLocalMac(String value) {
		if (value == null)
			return;
		metrics += "LocalMAC:" + value + "\r\n";
	}
	
	public void setRemoteAddr(String value)
	{
		if (value == null)
			return;
		metrics += "RemoteAddr:" + value + "\r\n";
	}
	
	public void setRemoteMac(String value) {
		if (value == null)
			return;
		metrics += "RemoteMAC:" + value + "\r\n";
	}
	
	private String addAttribute(String result, String name, int value)
	{
		if (! result.isEmpty())
			result += " ";
		result += name + "=" + value;
		return result;
	}

	private String addAttribute(String result, String name, double value)
	{
		if (! result.isEmpty())
			result += " ";
		result += name + String.format("=%.1f", value);
		return result;
	}
	
	public void setJitterBuffer(int adaptive, int rate, int nominal, int max, int absMax) {
		metrics += String.format("JitterBuffer:JBA=%d JBR=%d JBN=%d JBM=%d JBX=%d\r\n", adaptive, rate, nominal, max, absMax);
	}
	
	public void setPacketLoss(double lossRate, double discardRate)
	{
		metrics += String.format("PacketLoss:NLR=%.1f JDR=%.1f\r\n", lossRate, discardRate);
	}
	
	public void setPacketLoss(double lossRate)
	{
		metrics += String.format("PacketLoss:NLR=%.1f\r\n", lossRate);
	}
	
	public void setBurstGapLoss(double burstDensity, int burstDuration, double gapDensity, int gapDuration, int gmin)
	{
		metrics += String.format("BurstGapLoss:BLD=%.1f BD=%d GLD=%.1f GD=%d GMIN=%d\r\n", burstDensity, burstDuration, gapDensity, gapDuration, gmin);
	}
	
	public void setDelay(int roundTrip, int endSystem, int symmOneWay, int interarrivalJitter, int meanAbsJitter)
	{
		metrics += String.format("Delay:RTD=%d ESD=%d SOWD=%d IAJ=%d MAJ=%d\r\n", roundTrip, endSystem, symmOneWay, interarrivalJitter, meanAbsJitter);
	}
	
	public void setDelay(int interarrivalJitter)
	{
		metrics += String.format("Delay:IAJ=%d\r\n", interarrivalJitter);
	}
	
	public void setDelay(int roundTrip, int endSystem)
	{
		metrics += String.format("Delay:RTD=%d ESD=%d\r\n", roundTrip, endSystem);
	}
	
	public void setSignal(int signal, int noise, int rerl) {
		metrics += String.format("Signal:SL=%d NL=%d RERL=%d\r\n", signal, noise, rerl);
	}

	public void setQualityEst(int rlq, int rcq, int extri, double moslq, double moscq, boolean usedP564) {
		metrics += String.format("QualityEst:RLQ=%d RCQ=%d EXTRI=%d MOSLQ=%.1f MOSCQ=%.1f QoEEstAlg=%s\r\n", rlq, rcq, extri, moslq, moscq, (usedP564?"P.564":"other"));
	}
	
	public void setQualityEst(int rcq, int extri, double moslq, double moscq) {
		String result = new String();
		if ((rcq < 127) && (rcq >= 0))
			result = addAttribute(result, "RCQ", rcq);
		if ((extri < 127) && (extri >= 0))
			result = addAttribute(result, "EXTRI", extri);
		if ((moslq <= 5.0) && (moslq > 0))
			result = addAttribute(result, "MOSLQ", moslq);
		if ((moscq <= 5.0) && (moscq > 0))
			result = addAttribute(result, "MOSCQ", moscq);
		if (! result.isEmpty())
			metrics += "QualityEst:" + result + "\r\n";
	}
	
	public void setMetrics(VoipMetricsExtendedReportBlock b)
	{
		setJitterBuffer(b.getJitterBufferAdaptive(), b.getJitterBufferRate(), b.getJitterBufferNominal(), b.getJitterBufferMaximum(), b.getJitterBufferAbsMaximum());
		setPacketLoss(b.getLossRate(), b.getDiscardRate());
		setBurstGapLoss(b.getBurstDensity(), b.getBurstDuration(), b.getGapDensity(), b.getGapDuration(), b.getGmin());
		setDelay(b.getRoundTripDelay(), b.getEndSystemDelay());
		setSignal(b.getSignalLevel(), b.getNoiseLevel(), b.getResidualEchoReturnLoss());
		setQualityEst(b.getRFactor(), b.getExtRFactor(), b.getMOSLQ(), b.getMOSCQ());
	}
	

	public void setMetrics(ReportBlock report) {
		report.getCumulativeLost();
		setPacketLoss(report.getFractionLost());
		setDelay(report.getJitter());
	}
	
	public String toString()
	{
		return metrics;
	}
}
