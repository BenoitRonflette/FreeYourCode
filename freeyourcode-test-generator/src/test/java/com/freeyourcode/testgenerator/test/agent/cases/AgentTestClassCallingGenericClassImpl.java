package com.freeyourcode.testgenerator.test.agent.cases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.test.ClassCallingGenericClassImpl;
import com.freeyourcode.testgenerator.test.agent.AgentOneTestedClassTest;

public class AgentTestClassCallingGenericClassImpl extends AgentOneTestedClassTest {

	@BeforeClass
	public void lancementAgent() throws Exception {
		lancementAgent(AgentProperties.CONFIG_FILE_PATH + "=./src/test/java/agentTestClassCallingGenericClassImpl.xml");
	}

	@BeforeMethod
	public void resetBetweenTwoTests() {
		resetTestChecker();
	}

	@Override
	protected Class<?> getTestedClass() {
		return ClassCallingGenericClassImpl.class;
	}

	@Test
	public void testCompute() throws Exception {
		invokeMethod("compute");
		assertTestIs("@Test" + "public void testcompute_" + nextTestId() + "() throws Exception {" + "//Mock the stub methods" + "Object response0 = JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\");"
				+ "Mockito.when(genericClassImplStub.compute()).thenReturn((Double)response0);" +

				"//Call to tested method" + "Object testedMethodResult = classCallingGenericClassImpl.compute();" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\"), testedMethodResult);" +

				"//Check the number of calls to stub methods" + "Mockito.verify(genericClassImplStub, Mockito.times(1)).compute();" + "}");
	}

	@Test
	public void testComputeWithParam() throws Exception {
		invokeMethod("compute", 7d);
		assertTestIs("@Test" + "public void testcompute_" + nextTestId() + "() throws Exception {" + "//Mock the stub methods"
				+ "Object[] compute_enter_0 = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};" + "Object[] compute_diffsOnExit_1 = new Object[]{null};"
				+ "Mockito.doAnswer(exitAnswer(compute_diffsOnExit_1)).when(genericClassImplStub).compute(argEq((Double)compute_enter_0[0]));" +

				"//Call to tested method" + "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};" + "classCallingGenericClassImpl.compute((Double)inputParams_enter[0]);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" +

				"//Check the number of calls to stub methods" + "Mockito.verify(genericClassImplStub, Mockito.times(1)).compute((Double)Mockito.any());" + "}");
	}

	@Test
	public void testAbstractCompute() throws Exception {
		invokeMethod("abstractCompute", 7d);

		assertTestIs("@Test" + "public void testabstractCompute_" + nextTestId() + "() throws Exception {" + "//Mock the stub methods"
				+ "Object[] abstractCompute_enter_0 = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};" + "Object[] abstractCompute_diffsOnExit_1 = new Object[]{null};"
				+ "Mockito.doAnswer(exitAnswer(abstractCompute_diffsOnExit_1)).when(genericClassImplStub).abstractCompute(argEq((Double)abstractCompute_enter_0[0]));" +

				"//Call to tested method" + "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":7.0}\")};" + "classCallingGenericClassImpl.abstractCompute((Double)inputParams_enter[0]);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" +

				"//Check the number of calls to stub methods" + "Mockito.verify(genericClassImplStub, Mockito.times(1)).abstractCompute((Double)Mockito.any());" + "}");
	}

}
