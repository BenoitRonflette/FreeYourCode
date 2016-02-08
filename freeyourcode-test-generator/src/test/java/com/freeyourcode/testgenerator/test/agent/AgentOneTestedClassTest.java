package com.freeyourcode.testgenerator.test.agent;


public abstract class AgentOneTestedClassTest extends AgentTest{

	protected abstract Class<?> getTestedClass();
	
	protected void invokeMethod(String methodName, Object...params) throws Exception{
		invokeMethod(getTestedClass(), methodName, params);
	}
	

}