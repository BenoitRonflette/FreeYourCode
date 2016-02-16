package com.freeyourcode.testgenerator.core;

import java.util.Properties;

import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.utils.TestGeneratorProperties;

public class ListenerManagerConfig {

	private final Properties props;
	private final TestGeneratorLogger logger;

	public ListenerManagerConfig(Properties props, TestGeneratorLogger logger) {
		this.props = new Properties();
		this.logger = logger;

		// We copy updatable properties only
		this.props.setProperty(TestGeneratorProperties.TEST_EQUALITY_ON_STUBBING, props.getProperty(TestGeneratorProperties.TEST_EQUALITY_ON_STUBBING, defaultTestEqualityValue()));
	}

	public Properties getProps() {
		return props;
	}

	public TestGeneratorLogger getLogger() {
		return logger;
	}

	public boolean isTestEqualityOnStubbing() {
		return Boolean.parseBoolean(props.getProperty(TestGeneratorProperties.TEST_EQUALITY_ON_STUBBING, defaultTestEqualityValue()));
	}

	private String defaultTestEqualityValue() {
		return String.valueOf(true);
	}

}
