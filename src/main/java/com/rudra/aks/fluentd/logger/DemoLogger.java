package com.rudra.aks.fluentd.logger;

import org.slf4j.LoggerFactory;

import com.rudra.aks.fluentd.LoggerInit;

import ch.qos.logback.classic.Logger;


public class DemoLogger {

	Logger	logger;

	private String ctxString;
	
	@SuppressWarnings("rawtypes")
	public DemoLogger(Class clazz) {
		logger = (Logger)LoggerFactory.getLogger(clazz);
		LoggerInit	loggerInit = new LoggerInit();
		loggerInit.initLoggerContext(logger);
		
	}
	
	
	public String getCtxString() {
		return ctxString;
	}

	public void setCtxString(String ctxString) {
		this.ctxString = ctxString;
	}

	public	void info(String msg) {
		logger.info(ctxString + " " + msg);
	}
	
	public void info(String format, Object... arguments) {
		logger.info(ctxString + format, arguments);
	}
	
	public void info(String msg, Throwable t) {
		logger.info(ctxString + msg, t);
	}
	
	public	void debug(String msg) {
		logger.debug(ctxString + " " + msg);
	}
	
	public void debug(String format, Object... arguments) {
		logger.debug(ctxString + format, arguments);
	}
	
	public void debug(String msg, Throwable t) {
		logger.debug(ctxString + msg, t);
	}
	
	public	void trace(String msg) {
		logger.trace(ctxString + " " + msg);
	}
	
	public void trace(String format, Object... arguments) {
		logger.trace(ctxString + format, arguments);
	}
	
	public void trace(String msg, Throwable t) {
		logger.trace(ctxString + msg, t);
	}
	
	public	void warn(String msg) {
		logger.warn(ctxString + " " + msg);
	}
	
	public void warn(String format, Object... arguments) {
		logger.warn(ctxString + format, arguments);
	}
	
	public void warn(String msg, Throwable t) {
		logger.warn(ctxString + msg, t);
	}
	
	public	void error(String msg) {
		logger.error(ctxString + " " + msg);
	}
	
	public void error(String format, Object... arguments) {
		logger.error(ctxString + format, arguments);
	}
	
	public void error(String msg, Throwable t) {
		logger.error(ctxString + msg, t);
	}
	
	
}
