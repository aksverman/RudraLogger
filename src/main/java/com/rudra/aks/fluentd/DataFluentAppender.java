package com.rudra.aks.fluentd;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fluentd.logger.FluentLogger;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;


public class DataFluentAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	private static final class FluentDaemonAppender extends DaemonAppender<ILoggingEvent> {

		private FluentLogger fluentLogger;

		private final String tag;

		private final String label;

		private final String remoteHost;

		private final int port;

		private final Map<String, String> additionalFields;

		FluentDaemonAppender(String tag, String label, String remoteHost, int port, int maxQueueSize, Map<String, String> additionalFields) {
			super(maxQueueSize);
			this.tag = tag;
			this.label = label;
			this.remoteHost = remoteHost;
			this.port = port;
			this.additionalFields = additionalFields;
		}

		@Override
		public void execute() {
			this.fluentLogger = FluentLogger.getLogger(tag, remoteHost, port);
			super.execute();
		}

		@Override
		protected void close() {
			try {
				super.close();
			} finally {
				if (fluentLogger != null) {
					fluentLogger.close();
				}
			}
		}

		@Override
		protected void append(ILoggingEvent rawData) {
			final Map<String, Object> data = new HashMap<String, Object>();
			data.put("message", rawData.getFormattedMessage());
			data.put("logger", rawData.getLoggerName());
			data.put("thread", rawData.getThreadName());
			data.put("level", rawData.getLevel());
			if (rawData.getMarker() != null) 
				data.put("marker", rawData.getMarker());
			
			/*if (rawData.hasCallerData()) 
				data.put("caller", new CallerDataConverter().convert(rawData));
*/
			if (rawData.getThrowableProxy() != null)
				data.put("throwable", ThrowableProxyUtil.asString(rawData.getThrowableProxy()));

			if (additionalFields != null)
				data.putAll(additionalFields);

			for (Entry<String, String> entry : rawData.getMDCPropertyMap().entrySet())
				data.put(entry.getKey(), entry.getValue());

			fluentLogger.log(label, data, rawData.getTimeStamp() / 1000);
		}
	}

	private DaemonAppender<ILoggingEvent> appender;

	// Max queue size of logs which is waiting to be sent (When it reach to the
	// max size, the log will be disappeared).
	private int maxQueueSize;

	@Override
	protected void append(ILoggingEvent eventObject) {
		if (appender == null) {
			synchronized (this) {
				appender = new FluentDaemonAppender(tag, label, remoteHost, port, maxQueueSize, additionalFields);
			}
		}
		eventObject.prepareForDeferredProcessing();
		appender.log(eventObject);
	}

	@Override
	public void stop() {
		try {
			super.stop();
			appender.close();
		} finally {
			if(null != appender)
				appender.close();
		}
	}

	private String tag;

	private String label;

	private String remoteHost;

	private int port;

	private Map<String, String> additionalFields;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getMaxQueueSize() {
		return maxQueueSize;
	}

	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void addAdditionalField(Field field) {
		if (additionalFields == null) {
			additionalFields = new HashMap<String, String>();
		}
		additionalFields.put(field.getKey(), field.getValue());
	}

	public static class Field {

		private String key;

		private String value;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}