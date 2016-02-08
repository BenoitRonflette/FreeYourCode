package com.freeyourcode.testgenerator.test.agent.cases;

import org.testng.annotations.Test;

import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.test.TestedClass;
import com.freeyourcode.testgenerator.test.agent.AgentOneTestedClassTest;

public class AgentConfigTest extends AgentOneTestedClassTest {
	
	@Override
	protected Class<?> getTestedClass() {
		return TestedClass.class;
	}

	@Test(expectedExceptions={RuntimeException.class}, expectedExceptionsMessageRegExp="Config file is required")
	public void testNoAgentArgs(){
		lancementAgent(null);
	}
	
	@Test(expectedExceptions={RuntimeException.class}, expectedExceptionsMessageRegExp="Config file is required")
	public void testEmptyAgentArgs(){
		lancementAgent("");
	}
	
	@Test(expectedExceptions={RuntimeException.class}, expectedExceptionsMessageRegExp="Config file is required")
	public void testNoListenedSvc(){
		lancementAgent(AgentProperties.CONFIG_FILE_PATH+"=");
	}
	
}
