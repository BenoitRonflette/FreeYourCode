package com.freeyourcode.testgenerator.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.freeyourcode.testgenerator.agent.constant.AgentConfigCommonAttr;
import com.freeyourcode.testgenerator.agent.constant.AgentConfigTags;
import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.logger.DefaultTestLogger;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.server.TestGeneratorNanoHTTPD;
import com.freeyourcode.testgenerator.utils.PropertiesUtils;
import com.freeyourcode.testgenerator.utils.XMLUtils;

/**
 * Input to use the Legacy Code generator using a java agent.
 * 
 * @author BRE
 * 
 */
public class Agent {

	public static void premain(String args, Instrumentation instr) {
		Properties properties = PropertiesUtils.parseProperties(args);
		String configFile = properties.getProperty(AgentProperties.CONFIG_FILE_PATH);
		
		if(configFile == null || configFile.isEmpty()){
			throw new RuntimeException("Config file is required");
		}

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dBuilder;
		Document doc;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(new File(configFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		Properties applicationProperties = XMLUtils.extractProperties(doc.getFirstChild());

		final TestGeneratorLogger logger = createLogger(doc);
		TestGeneratorNanoHTTPD server = new TestGeneratorNanoHTTPD(logger, applicationProperties);

		NodeList pluginsConfig = XMLUtils.check1Most(doc.getElementsByTagName(AgentConfigTags.PLUGINS), AgentConfigTags.PLUGINS, true);
		instr.addTransformer(new AgentClassFileTransformer(pluginsConfig, logger, applicationProperties));
		
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static TestGeneratorLogger createLogger(Document doc) {
		TestGeneratorLogger logger = null;

		NodeList loggerConfig = doc.getElementsByTagName(AgentConfigTags.LOGGER);
		if (loggerConfig.getLength() == 0) {
			logger = new DefaultTestLogger(new Properties());
		} else if (loggerConfig.getLength() > 1) {
			throw new RuntimeException("Only one logger can be defined in configuration");
		} else {
			Properties props = XMLUtils.extractProperties(loggerConfig.item(0));
			String loggerClass = props.getProperty(AgentConfigCommonAttr.CLASS);
			props.remove(AgentConfigCommonAttr.CLASS);
			try {
				logger = (TestGeneratorLogger) Class.forName(loggerClass).getConstructor(Properties.class).newInstance(props);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
		return logger;
	}

}