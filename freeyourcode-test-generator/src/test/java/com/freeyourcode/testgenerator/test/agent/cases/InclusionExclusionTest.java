package com.freeyourcode.testgenerator.test.agent.cases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.test.TestedBean;
import com.freeyourcode.testgenerator.test.TestedClass;
import com.freeyourcode.testgenerator.test.TestedSubBean;
import com.freeyourcode.testgenerator.test.agent.AgentTest;

public class InclusionExclusionTest extends AgentTest {

	@BeforeClass
	public void lancementAgent() {
		lancementAgent(AgentProperties.CONFIG_FILE_PATH + "=./src/test/java/InclusionExclusionConfig.xml");
	}

	@BeforeMethod
	public void resetBetweenTwoTests() {
		resetTestChecker();
	}

	@Test
	public void test_noListenerOn_TestedSubBean() throws Exception {
		invokeMethod(TestedSubBean.class, "getValue2");
		assertTestIs("");
	}

	@Test
	public void test_listenerOn_TestedClass() throws Exception {
		invokeMethod(TestedClass.class, "myMethod1ParamResult", 2d);
		assertTestIs("@Test" + "public void testmyMethod1ParamResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":2.0}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":2.0}\")};" + "Object testedMethodResult = testedClass.myMethod1ParamResult((Double)inputParams_enter[0]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":4.0}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void test_noListenerOn_TestedClass_myMethodCallingAPrivateOne() throws Exception {
		invokeMethod(TestedClass.class, "myMethodCallingAPrivateOne", 2d);
		assertTestIs("");
	}

	@Test
	public void test_noListenerOn_TestedBean_setValue() throws Exception {
		invokeMethod(TestedBean.class, "setValue", 2);
		assertTestIs("");
	}

	@Test
	public void test_listenerOn_TestedBean_getValue() throws Exception {
		invokeMethod(TestedBean.class, "getValue");
		assertTestIs("@Test" + "public void testgetValue_" + nextTestId() + "() throws Exception {" + "//Call to tested method" + "Object testedMethodResult = testedBean.getValue();"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\"}\"), testedMethodResult);" + "}");
	}

}