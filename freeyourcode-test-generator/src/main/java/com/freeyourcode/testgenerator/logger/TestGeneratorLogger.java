package com.freeyourcode.testgenerator.logger;

import java.util.Properties;

import com.freeyourcode.testgenerator.server.ServerStateListener;

public abstract class TestGeneratorLogger implements ServerStateListener {

	protected final Properties props;

	public TestGeneratorLogger(Properties props) {
		this.props = props;
	}

	public void onGenerationSuccess(String... codeLines) {
	}

	public void onGenerationFail(String msg, Exception e) {
	}

	public void onUsedClass(Class<?> cls, boolean onlyForImport) {
	}

	public void onDeclaratedField(String field, String... annotations) {
	}

	public Properties getProperties() {
		return props;
	}

}