package com.freeyourcode.testgenerator.test.agent.cases;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.Assert;

import org.hibernate.collection.PersistentSet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.freeyourcode.testgenerator.agent.constant.AgentProperties;
import com.freeyourcode.testgenerator.test.HibernateProxyTestedBean;
import com.freeyourcode.testgenerator.test.HibernateProxyTestedSubBean;
import com.freeyourcode.testgenerator.test.TestedBean;
import com.freeyourcode.testgenerator.test.TestedClass;
import com.freeyourcode.testgenerator.test.TestedSubBean;
import com.freeyourcode.testgenerator.test.agent.AgentOneTestedClassTest;

public class AgentTestGeneratorPluginTest extends AgentOneTestedClassTest {

	// FIXME tester que les méthodes privates sont ignorées

	@BeforeClass
	public void lancementAgent() {
		lancementAgent(AgentProperties.CONFIG_FILE_PATH + "=./src/test/java/agentTestGeneratorPluginConfig.xml");
	}

	@BeforeMethod
	public void resetBetweenTwoTests() {
		resetTestChecker();
	}

	@Override
	protected Class<?> getTestedClass() {
		return TestedClass.class;
	}

	@Test
	public void testMyMethodNoParamNoResult() throws Exception {

		invokeMethod("myMethodNoParamNoResult");
		assertTestIs("@Test" + "public void testmyMethodNoParamNoResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "testedClass.myMethodNoParamNoResult();" + "}");
	}

	@Test
	public void testMyMethodNoParamResult() throws Exception {

		invokeMethod("myMethodNoParamResult");
		assertTestIs("@Test" + "public void testmyMethodNoParamResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object testedMethodResult = testedClass.myMethodNoParamResult();" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":2.0}\"), testedMethodResult);" + "}");
	}

	@Test
	public void testMyMethodNoParamResultPrimitif() throws Exception {

		invokeMethod("myMethodNoParamResultPrimitif");
		assertTestIs("@Test" + "public void testmyMethodNoParamResultPrimitif_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object testedMethodResult = testedClass.myMethodNoParamResultPrimitif();" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":2.0}\"), testedMethodResult);" + "}");
	}

	@Test
	public void testMyMethod1ParamNoResult() throws Exception {

		invokeMethod("myMethod1ParamNoResult", 17.14);
		assertTestIs("@Test" + "public void testmyMethod1ParamNoResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":17.14}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":17.14}\")};" + "testedClass.myMethod1ParamNoResult((Double)inputParams_enter[0]);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod1ParamNoResult_ParamDefaultValue() throws Exception {

		invokeMethod("myMethod1ParamNoResult", new Object[] { null });
		assertTestIs("@Test" + "public void testmyMethod1ParamNoResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{null};" + "Object[] inputParams_exit = new Object[]{null};" + "testedClass.myMethod1ParamNoResult((Double)inputParams_enter[0]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod1ParamResult() throws Exception {

		invokeMethod("myMethod1ParamResult", 9d);
		assertTestIs("@Test" + "public void testmyMethod1ParamResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":9.0}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":9.0}\")};" + "Object testedMethodResult = testedClass.myMethod1ParamResult((Double)inputParams_enter[0]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":18.0}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod1ParamResult_NPEException() throws Exception {

		try {
			invokeMethod("myMethod1ParamResult", new Object[] { null });
		} catch (InvocationTargetException e) {
			// L'appel � myMethod1ParamResult a g�n�r�e une exception dans la m�thode, celle-ci doit donc �tre enregistr�e
			// dans les tests
			Assert.assertEquals(NullPointerException.class, e.getCause().getClass());
		}
		assertTestIs("@Test(expectedExceptions = NullPointerException.class)" + "public void testmyMethod1ParamResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{null};" + "testedClass.myMethod1ParamResult((Double)inputParams_enter[0]);" + "}");
	}

	@Test
	public void testMyMethod1ParamPrimitifResultPrimitif() throws Exception {

		invokeMethod("myMethod1ParamPrimitifResultPrimitif", 4.0003);
		assertTestIs("@Test" + "public void testmyMethod1ParamPrimitifResultPrimitif_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":4.0003}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":4.0003}\")};"
				+ "Object testedMethodResult = testedClass.myMethod1ParamPrimitifResultPrimitif((Double)inputParams_enter[0]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0006}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod1ParamPrimitifResultPrimitif_defaultPrimitiveValue_defaultResultValue() throws Exception {

		invokeMethod("myMethod1ParamPrimitifResultPrimitif", 0.0);
		assertTestIs("@Test" + "public void testmyMethod1ParamPrimitifResultPrimitif_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\"}\")};" + "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\"}\")};"
				+ "Object testedMethodResult = testedClass.myMethod1ParamPrimitifResultPrimitif((Double)inputParams_enter[0]);" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\"}\"), testedMethodResult);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamNoResult_withParamFieldDefaultValues() throws Exception {

		invokeMethod("myMethod2ParamNoResult", 8d, new TestedBean());
		assertTestIs("@Test"
				+ "public void testmyMethod2ParamNoResult_"
				+ nextTestId()
				+ "() throws Exception {"
				+ "//Call to tested method"

				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\"}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\"}\")};"
				+ "testedClass.myMethod2ParamNoResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamNoResult_withParamFieldDefaultValuesPassedInParameters() throws Exception {

		invokeMethod("myMethod2ParamNoResult", 8d, new TestedBean(null, 0, null));
		assertTestIs("@Test"
				+ "public void testmyMethod2ParamNoResult_"
				+ nextTestId()
				+ "() throws Exception {"
				+ "//Call to tested method"

				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\"}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\"}\")};"
				+ "testedClass.myMethod2ParamNoResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamNoResult_0ForIntegerIsNotADefaultValue() throws Exception {

		invokeMethod("myMethod2ParamNoResult", 8d, new TestedBean(null, 0, 0));
		assertTestIs("@Test"
				+ "public void testmyMethod2ParamNoResult_"
				+ nextTestId()
				+ "() throws Exception {"
				+ "//Call to tested method"

				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"valueObject\\\":0}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"valueObject\\\":0}\")};"
				+ "testedClass.myMethod2ParamNoResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamNoResult() throws Exception {

		invokeMethod("myMethod2ParamNoResult", 8d, new TestedBean("lib", 1, 2));
		assertTestIs("@Test"
				+ "public void testmyMethod2ParamNoResult_"
				+ nextTestId()
				+ "() throws Exception {"
				+ "//Call to tested method"

				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"lib\\\",\\\"value\\\":1,\\\"valueObject\\\":2}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"lib\\\",\\\"value\\\":1,\\\"valueObject\\\":2}\")};"
				+ "testedClass.myMethod2ParamNoResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamNoResultWithParamNull() throws Exception {

		invokeMethod("myMethod2ParamNoResult", 8d, null);
		assertTestIs("@Test" + "public void testmyMethod2ParamNoResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), null};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":8.0}\"), null};"
				+ "testedClass.myMethod2ParamNoResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamResult() throws Exception {

		invokeMethod("myMethod2ParamResult", 45.2d, new TestedBean("coucou", 7, 8));
		assertTestIs("@Test" + "public void testmyMethod2ParamResult_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{" + "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8}\")};" + "Object[] inputParams_exit = new Object[]{"
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8}\")};"
				+ "Object testedMethodResult = testedClass.myMethod2ParamResult((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":90.4}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	private void assertTestMyMethod2ParamPrimitifResultPrimitif() throws Exception {
		assertTestIs("@Test" + "public void testmyMethod2ParamPrimitifResultPrimitif_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{" + "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8}\")};" + "Object[] inputParams_exit = new Object[]{"
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8}\")};"
				+ "Object testedMethodResult = testedClass.myMethod2ParamPrimitifResultPrimitif((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":90.4}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamPrimitifResultPrimitif() throws Exception {

		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, new TestedBean("coucou", 7, 8));
		assertTestMyMethod2ParamPrimitifResultPrimitif();
	}

	@Test
	public void testHibernateProxy_testMyMethod2ParamPrimitifResultPrimitif() throws Exception {

		HibernateProxyTestedBean hibernateProxyObject = new HibernateProxyTestedBean(new TestedBean("coucou", 7, 8));
		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, hibernateProxyObject);
		assertTestMyMethod2ParamPrimitifResultPrimitif();
	}

	private void assertTestMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee() throws Exception {
		assertTestIs("@Test" + "public void testmyMethod2ParamPrimitifResultPrimitif_" + nextTestId() + "() throws Exception {" + "//Call to tested method"

		+ "Object[] inputParams_enter = new Object[]{" + "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8,\\\"subBean\\\":{\\\"value2\\\":\\\"test\\\"}}\")};"
				+ "Object[] inputParams_exit = new Object[]{" + "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8,\\\"subBean\\\":{\\\"value2\\\":\\\"test\\\"}}\")};"
				+ "Object testedMethodResult = testedClass.myMethod2ParamPrimitifResultPrimitif((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":90.4}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee() throws Exception {

		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, new TestedBean("coucou", 7, 8, new TestedSubBean("test")));
		assertTestMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee();
	}

	@Test
	public void testHibernateProxySubBeanOnly_testMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee() throws Exception {

		HibernateProxyTestedSubBean hibernateProxySubObject = new HibernateProxyTestedSubBean(new TestedSubBean("test"));
		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, new TestedBean("coucou", 7, 8, hibernateProxySubObject));
		assertTestMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee();
	}

	@Test
	public void testHibernateProxy_testMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee() throws Exception {

		HibernateProxyTestedSubBean hibernateProxySubObject = new HibernateProxyTestedSubBean(new TestedSubBean("test"));
		HibernateProxyTestedBean hibernateProxyObject = new HibernateProxyTestedBean(new TestedBean("coucou", 7, 8, hibernateProxySubObject));
		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, hibernateProxyObject);
		assertTestMyMethod2ParamPrimitifResultPrimitif_withSubBeanRenseignee();
	}

	@Test
	public void testMyMethodThrowingAnException() throws Exception {
		try {
			invokeMethod("myMethodThrowingAnException", 45.2d);
		} catch (InvocationTargetException e) {
			// we are tested a method throwing an exception
			Assert.assertEquals(Exception.class, e.getCause().getClass());
		}

		assertTestIs("@Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = \"Exception for tests\")" + "public void testmyMethodThrowingAnException_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\")};" + "testedClass.myMethodThrowingAnException((Double)inputParams_enter[0]);" + "}");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPersistentSetsAreConverted() throws Exception {
		// We create a initialized persistentSet with valid snapshot.
		PersistentSet set = new PersistentSet(null, new HashSet<>());
		set.setSnapshot(null, null, new HashMap<Object, Object>());

		invokeMethod("myMethod2ParamPrimitifResultPrimitif", 45.2d, new TestedBean("coucou", 7, 8, null, set));
		assertTestIs("@Test"
				+ "public void testmyMethod2ParamPrimitifResultPrimitif_"
				+ nextTestId()
				+ "() throws Exception {"
				+ "//Call to tested method"

				+ "Object[] inputParams_enter = new Object[]{"
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8,\\\"set\\\":{\\\"@type\\\":\\\"java.util.HashSet\\\"}}\")};"
				+ "Object[] inputParams_exit = new Object[]{"
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\"), "
				+ "JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"libelle\\\":\\\"coucou\\\",\\\"value\\\":7,\\\"valueObject\\\":8,\\\"set\\\":{\\\"@type\\\":\\\"java.util.HashSet\\\"}}\")};"
				+ "Object testedMethodResult = testedClass.myMethod2ParamPrimitifResultPrimitif((Double)inputParams_enter[0], (TestedBean)inputParams_enter[1]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":90.4}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testCallToStaticMethod() throws Exception {
		invokeMethod("myStaticMethod", 45.2d);
		assertTestIs("@Test" + "public void testmyStaticMethod_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\")};" + "Object testedMethodResult = TestedClass.myStaticMethod((Double)inputParams_enter[0]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":135.60000000000002}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testCallToMyStaticMethodCallingAPrivateOne() throws Exception {
		invokeMethod("myStaticMethodCallingAPrivateOne", 45.2d);
		assertTestIs("@Test" + "public void testmyStaticMethodCallingAPrivateOne_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":45.2}\")};"
				+ "Object testedMethodResult = TestedClass.myStaticMethodCallingAPrivateOne((Double)inputParams_enter[0]);" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"double\\\",\\\"value\\\":180.8}\"), testedMethodResult);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testCheckSerializedValueIsDifferentWithSameObject() throws Exception {
		invokeMethod("checkSerializedValueIsDifferentWithSameObject", new TestedBean(null, 7, null));
		assertTestIs("@Test" + "public void testcheckSerializedValueIsDifferentWithSameObject_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"value\\\":7}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"value\\\":14}\")};"
				+ "Object testedMethodResult = testedClass.checkSerializedValueIsDifferentWithSameObject((TestedBean)inputParams_enter[0]);"
				+ "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"com.freeyourcode.testgenerator.test.TestedBean\\\",\\\"value\\\":14}\"), testedMethodResult);" + "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testCheckMethodFromSuperClassNotOverridden() throws Exception {
		invokeMethod("methodFromSuperClassNotOverridden", 2);
		assertTestIs("@Test" + "public void testmethodFromSuperClassNotOverridden_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":2}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":2}\")};"
				+ "Object testedMethodResult = testedClass.methodFromSuperClassNotOverridden((Integer)inputParams_enter[0]);" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":-2}\"), testedMethodResult);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

	@Test
	public void testCheckMethodFromSuperClassNotOverriddenButSkipped() throws Exception {
		invokeMethod("methodFromSuperClassNotOverriddenButSkipped", 2);
		// This method is excluded by xml configuration !
		assertTestIs("");
	}

	@Test
	public void testCheckMethodFromSuperClassNotOverriddenButSkippedBySuperClassName() throws Exception {
		invokeMethod("methodFromSuperClassNotOverriddenButSkippedBySuperClassName", 2);
		// This method is excluded by xml configuration !
		assertTestIs("");
	}

	@Test
	public void testCheckMethodFromSuperClassButOverridden() throws Exception {
		invokeMethod("methodFromSuperClassButOverridden", 2);
		assertTestIs("@Test" + "public void testmethodFromSuperClassButOverridden_" + nextTestId() + "() throws Exception {" + "//Call to tested method"
				+ "Object[] inputParams_enter = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":2}\")};"
				+ "Object[] inputParams_exit = new Object[]{JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":2}\")};"
				+ "Object testedMethodResult = testedClass.methodFromSuperClassButOverridden((Integer)inputParams_enter[0]);" + "assertEquals(JsonSerialisationUtils.deserialize(\"{\\\"@type\\\":\\\"int\\\",\\\"value\\\":32}\"), testedMethodResult);"
				+ "assertEquals(inputParams_exit, inputParams_enter);" + "}");
	}

}