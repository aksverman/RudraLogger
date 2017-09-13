package com.rudra.aks.fluentd.logger;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.rudra.aks.fluentd.LoggerInit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


/*
 * not required for fluentd logger
 */
@ComponentScan(basePackages = "com.rudra.aks")
public class InitRunner /*implements CommandLineRunner*/
{

	static Logger logger = (Logger)LoggerFactory.getLogger(InitRunner.class);
	
	
	public static void run(@SuppressWarnings("rawtypes") Class clazz, String [] args) throws Exception {
		logger.info("Setting application build & logger");
		
		logger.setLevel(Level.INFO);
		SpringApplicationBuilder builder = new SpringApplicationBuilder();
		builder.sources(InitRunner.class).sources(clazz).run(args);
		
		//override the logging
		LoggerInit logInit = new LoggerInit();
		try {
			logInit.initLoggerContext(logger);
		} catch (Exception e) {
			logger.error("Unable to initialize logger", e);
			
			if(null != logInit.getFluentAppender())
				logInit.getFluentAppender().stop();
		}

		logger.info("InitRunner completed ...");
	}

}
