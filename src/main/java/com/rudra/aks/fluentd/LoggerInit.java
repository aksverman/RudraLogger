package com.rudra.aks.fluentd;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.ConsoleAppender;


public class LoggerInit {

	private DataFluentAppender fle = null;
	
	//make return as void 
	@SuppressWarnings({ "rawtypes", "unchecked" })
	//public Logger initLoggerContext(Logger logger){
	public void initLoggerContext(Logger logger){
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		logger.setLevel(Level.INFO);
		fle = new DataFluentAppender();
		fle.setName("FLUENT");
		fle.setContext(loggerContext);
		fle.setTag("mysql");
		fle.setLabel("INFO");
		fle.setRemoteHost("127.0.0.1");
		fle.setPort(24224);
		fle.setMaxQueueSize(20);
		
		/* #Optional
		 * Set the supplied threshold
		 */
		ThresholdFilter ftf = new ThresholdFilter();
		ftf.setLevel(Level.toLevel("INFO".toString(), Level.WARN).toString());
		fle.addFilter(ftf);
		
		//Now start the appender
		fle.start();
		
		//Now add it to the passed in logger as an appender
		logger.addAppender(fle);
		
		
		/* #Optional, comment if don't logs on std console.
		 * to make the logger logs event into console also.
		 */
		ConsoleAppender console = (ConsoleAppender)loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("STDOUT");
		if(console == null)
		    console = (ConsoleAppender)loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("console");
		ThresholdFilter ctf = new ThresholdFilter();
		ctf.setLevel(Level.toLevel("INFO".toString(), Level.WARN).toString());
		if(console != null)
		    console.addFilter(ctf);
		
		
		//return logger;
	}

	public DataFluentAppender getFluentAppender(){
		return fle;
	}
}
