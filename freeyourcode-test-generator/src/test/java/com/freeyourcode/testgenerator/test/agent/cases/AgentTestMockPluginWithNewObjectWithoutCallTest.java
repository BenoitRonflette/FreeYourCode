package com.freeyourcode.testgenerator.test.agent.cases;

import java.lang.reflect.Field;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.test.ClassWithNewObjectWithoutCall;
import com.freeyourcode.testgenerator.test.TestedBean;
import com.freeyourcode.testgenerator.test.agent.AgentOneTestedClassTest;

/**
 * In ClassWithNewObjectWithoutCall, there is a (listened or not) field named "bean", there will be no mock on this field (maybe it was parameterized
 * to be mocked but there has been no call and no use of constructors on it ! But we listen to another class to be mocked, named TestedClass. Only one
 * action is performed to this object, a call to its constructor which has got only one parameter: field named "bean" and use it to be initialized.
 * When the test is generated, we have to create a mocked instance on TestedClass and the call to its constructor has to return the mocked instance,
 * real constructor has not to be performed to avoid a NPE (in fact, "bean" is going to be null in generated test).
 */
public class AgentTestMockPluginWithNewObjectWithoutCallTest extends AgentOneTestedClassTest {

	@Override
	protected Class<?> getTestedClass() {
		return ClassWithNewObjectWithoutCall.class;
	}

	@BeforeClass
	public void lancementAgent() {
		lancementAgent(AgentProperties.CONFIG_FILE_PATH + "=./src/test/java/agentTestMockPluginConfigWithNewObjectWithoutCall.xml");
	}

	@BeforeMethod
	public void resetBetweenTwoTests() {
		resetTestChecker();
	}

	@Override
	protected <T> T createNewInstanceOfTestedClass(Class<T> cls) throws Exception {
		T instance = super.createNewInstanceOfTestedClass(cls);
		// we simulate an injection by any framework
		Field field = cls.getDeclaredField("bean");
		field.setAccessible(true);
		field.set(instance, loadClass(TestedBean.class).newInstance());
		field.setAccessible(false);
		return instance;
	}

	@Test
	public void testMyMethodIsCreatingANewInstanceOfTestedClassInitializedWithAlreadySetField() throws Exception {
		invokeMethod("myMethodIsCreatingANewInstanceOfTestedClassInitializedWithAlreadySetField");
		// real test is when we are going to execute the generated test, here, only a call to the tested method is generated, it's the test
		// fmk which is going to return the mocked instance automatically as soon as there is a call to the constructor (because the original
		// call to the constructor added a field in generated tests with annotation @Mock
		assertTestIs("@Test" + 
				"public void testmyMethodIsCreatingANewInstanceOfTestedClassInitializedWithAlreadySetField_0() throws Exception {" + 
				"//Call to tested method" + 
				"classWithNewObjectWithoutCall.myMethodIsCreatingANewInstanceOfTestedClassInitializedWithAlreadySetField();" + 
				"}");
	}

}
