package com.freeyourcode.testgenerator.core;

import java.util.Properties;

import com.freeyourcode.test.utils.MatcherMode;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.utils.TestGeneratorProperties;

public class ListenerManagerConfig {
	// TODO remove this useless class or REFAC as a property toolbox.
	private final TestGeneratorLogger logger;

	public ListenerManagerConfig(Properties props, TestGeneratorLogger logger) {
		this.logger = logger;
		this.logger.getProperties().setProperty(TestGeneratorProperties.MATCHER_MODE, getMatcherModeFromProperties(props).getValue());
	}

	public TestGeneratorLogger getLogger() {
		return logger;
	}

	public static MatcherMode getMatcherModeFromProperties(Properties props) {
		MatcherMode mode = MatcherMode.fromValue(props.getProperty(TestGeneratorProperties.MATCHER_MODE));
		if (mode == null) {
			mode = MatcherMode.defaultMode();
		}
		return mode;
	}

	public MatcherMode getMatcherMode() {
		return getMatcherModeFromProperties(this.logger.getProperties());
	}

}
