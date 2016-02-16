package com.freeyourcode.testgenerator.test.server;

import java.util.Properties;

import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.server.NanoHTTPD.ServerRunner;
import com.freeyourcode.testgenerator.server.TestGeneratorNanoHTTPD;
import com.freeyourcode.testgenerator.utils.TestGeneratorProperties;

public class TestGeneratorNanoHTTPDTest {

	// TODO add test ng about server

	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty(TestGeneratorProperties.PORT, "8099");
		Properties lProps = new Properties();
		lProps.setProperty("PROP1", "Value1");
		lProps.setProperty("PROP2", "Value2");
		lProps.setProperty("PROP3", "Value3");
		TestGeneratorLogger logger = new TestGeneratorLogger(lProps) {
			@Override
			public void startKilling() {
				System.out.println("Logger is started with properties: " + props);
			}

			@Override
			public void stopKilling() {
				System.out.println("Logger is stopped with properties: " + props);
			}
		};
		TestGeneratorNanoHTTPD s = new TestGeneratorNanoHTTPD(props);
		s.registerListener(logger);
		s.registerUpdatableProperties("Logger properties", lProps);

		ServerRunner.executeInstance(s);
	}

}
