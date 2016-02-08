package com.freeyourcode.testgenerator.core;

import java.util.Properties;

import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;

public class ListenerManagerConfig {
	
	private final Properties props;
	private final TestGeneratorLogger logger;
	
	public ListenerManagerConfig(Properties props, TestGeneratorLogger logger) {
		this.props = props;
		this.logger = logger;
	}

	public Properties getProps() {
		return props;
	}

	public TestGeneratorLogger getLogger() {
		return logger;
	}
	
	
	

}
