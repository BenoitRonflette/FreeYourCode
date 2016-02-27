package com.freeyourcode.testgenerator.core.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;
import com.freeyourcode.testgenerator.core.CallOnMock;
import com.freeyourcode.testgenerator.core.EventMap;
import com.freeyourcode.testgenerator.core.ListenerManagerConfig;
import com.freeyourcode.testgenerator.core.MethodDescriptor;
import com.freeyourcode.testgenerator.core.MethodParameters;
import com.freeyourcode.testgenerator.core.utils.ClassResolver;
import com.freeyourcode.testgenerator.core.utils.SignatureResolver;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;

public class DefaultTestGeneratorListener implements TestGeneratorListener {

	private final static Log log = LogFactory.getLog(DefaultTestGeneratorListener.class);

	private static final String TESTED_METHOD_RESULT_NAME = "testedMethodResult";
	private static final String INPUT_PARAM_VAR_NAME = "inputParams";
	private static final String SUFFIX_ENTER = "_enter";
	private static final String SUFFIX_EXIT = "_exit";
	private static final String SUFFIX_DIFFS_ON_EXIT = "_diffsOnExit";

	private static final String EVENT_RESPONSE_VAR_NAME = "response";

	private static final String EMPTY_LINE = "";

	private final int methodId;

	// TODO encapsuler la gestion du pendind/events
	protected CallOnMock pendingEvent;
	protected final EventMap eventMap = new EventMap();
	private final MethodDescriptor methodDescriptor;
	int methodInputNumber = 0;
	int methodResponseNumber = 0;

	final LinkedList<String> testCodeLines = new LinkedList<String>();
	private MethodParameters inputParameters;

	private final ListenerManagerConfig config;

	public DefaultTestGeneratorListener(int id, MethodDescriptor methodDescriptor, ListenerManagerConfig config) {
		log.info("FreeYourCode Test Generator: new listener on " + methodDescriptor);
		this.config = config;
		this.methodId = id;
		this.methodDescriptor = methodDescriptor;
	}

	private TestGeneratorLogger getLogger() {
		return config.getLogger();
	}

	private static String toLowerFirstLetter(String anyString) {
		char firstLetter = anyString.charAt(0);
		return anyString.replaceFirst(String.valueOf(firstLetter), String.valueOf(firstLetter).toLowerCase());
	}

	private void writeParamsAsObjectArray(String varName, MethodParameters parameters, String[] inputParams) throws IOException {
		if (parameters.getInputParams() != null && parameters.getInputParams().size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("Object[] ").append(varName).append(" = ").append(writeAsObjectArray(inputParams)).append(";");
			testCodeLines.add(sb.toString());
		}
	}

	private static String writeAsObjectArray(String[] inputParams) throws IOException {
		if (inputParams != null && inputParams.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("new Object[]{");

			for (int i = 0; i < inputParams.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(inputParams[i]);
			}
			sb.append("}");
			return sb.toString();
		}
		return null;
	}

	private static String buildCallMethod(String methodName, String[] parameters) {
		StringBuilder sb = new StringBuilder();
		sb.append(".").append(methodName).append("(");
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(parameters[i]);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public void onInput(MethodParameters parameters) {
		inputParameters = parameters;
		try {
			inputParameters.freezeEnter();
		} catch (IOException e) {
			getLogger().onGenerationFail(e.getMessage(), e);
		}
	}

	@Override
	public void onOutput(Object outputValue) {
		// FIXME le faire plus tôt
		try {
			this.inputParameters.freezeExit();
		} catch (Exception e) {
			getLogger().onGenerationFail(e.getMessage(), e);
		}
		writeTest("@Test", outputValue, false);
	}

	@Override
	public void onException(Exception e) {
		writeTest("@Test(expectedExceptions = " + e.getClass().getSimpleName() + ".class" + (e.getMessage() != null ? ", expectedExceptionsMessageRegExp = \"" + e.getMessage() + "\"" : "") + ")", null, true);
	}

	private void assertEquals(String expected, String actual) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("assertEquals(").append(expected).append(", ").append(actual).append(");");
		testCodeLines.add(sb.toString());
	}

	private void openTestMethod(String methodName) {
		StringBuilder sb = new StringBuilder();
		sb.append("public void test").append(methodName).append("_").append(methodId).append("() throws Exception {");
		testCodeLines.add(sb.toString());
	}

	private void closeAndWriteTestMethod() {
		testCodeLines.add("}");

		getLogger().onGenerationSuccess(testCodeLines.toArray(new String[testCodeLines.size()]));
	}

	private void writeComment(String comment) {
		testCodeLines.add("//" + comment);
	}

	private void writeTestedMethod(Object outputValue, boolean exception) throws IOException {
		String testedClassName = methodDescriptor.getMethodClass().getSimpleName();
		getLogger().onUsedClass(methodDescriptor.getMethodClass(), false);

		writeComment("Call to tested method");

		String testedClassVarName;
		if (methodDescriptor.isStatic()) {
			testedClassVarName = testedClassName;
			// FIXME si full injection,
			// pr permettre l'injection de fields static que cette méthode pourrait utiliser.
			// getLogger().onDeclaratedField(testedClassName+" "+testedClassVarName, "@InjectMocks");
		} else {
			testedClassVarName = toLowerFirstLetter(testedClassName);
			getLogger().onDeclaratedField(testedClassName + " " + testedClassVarName, "@InjectMocks");
		}

		writeParamsAsObjectArray(INPUT_PARAM_VAR_NAME + SUFFIX_ENTER, inputParameters, JsonSerialisationUtils.writeSerializedObjectsInJava(inputParameters.getFrozenParametersOnEnter()));
		if (!exception) {
			writeParamsAsObjectArray(INPUT_PARAM_VAR_NAME + SUFFIX_EXIT, inputParameters, JsonSerialisationUtils.writeSerializedObjectsInJava(inputParameters.getFrozenParametersOnExit()));
		}

		StringBuilder callSvcMethodBuilder = new StringBuilder();
		if (!methodDescriptor.isVoid() && !exception) {
			callSvcMethodBuilder.append("Object ").append(TESTED_METHOD_RESULT_NAME).append(" = ");
		}

		SignatureResolver sigResolver = new SignatureResolver(methodDescriptor);
		sigResolver.addCall(prepareObjectsForCast(inputParameters.getInputParams()));
		String[] params = generateParamsFromArray(sigResolver, INPUT_PARAM_VAR_NAME + SUFFIX_ENTER, false);
		callSvcMethodBuilder.append(testedClassVarName).append(buildCallMethod(methodDescriptor.getName(), params)).append(";");
		testCodeLines.add(callSvcMethodBuilder.toString());

		if (!methodDescriptor.isVoid() && !exception) {
			assertEquals(JsonSerialisationUtils.writeObjectInJava(outputValue), TESTED_METHOD_RESULT_NAME);
		}

		if (inputParameters.getInputParams() != null && inputParameters.getInputParams().size() > 0 && !exception) {
			assertEquals(INPUT_PARAM_VAR_NAME + SUFFIX_EXIT, INPUT_PARAM_VAR_NAME + SUFFIX_ENTER);
		}
	}

	private void writeStubVerifications(List<String> stubVerifications) {
		if (stubVerifications.size() > 0) {
			testCodeLines.add(EMPTY_LINE);
			writeComment("Check the number of calls to stub methods");
			testCodeLines.addAll(stubVerifications);
		}
	}

	private void writeTest(String annotation, Object outputValue, boolean exception) {
		try {
			testCodeLines.add(annotation);
			openTestMethod(methodDescriptor.getName());
			List<String> verifyCalledEvent = writeStubs();
			writeTestedMethod(outputValue, exception);
			if (!exception) {
				writeStubVerifications(verifyCalledEvent);
			}
			closeAndWriteTestMethod();
		} catch (Exception ex) {
			getLogger().onGenerationFail(ex.getMessage(), ex);
		}
	}

	protected String declareClassIsStubbed(Class<?> cls) {
		getLogger().onUsedClass(cls, true);
		// We create a mocked object for this output event:
		String mockedClassAsVar = toLowerFirstLetter(cls.getSimpleName()) + "Stub";
		getLogger().onDeclaratedField(cls.getSimpleName() + " " + mockedClassAsVar, "@Mock");
		return mockedClassAsVar;
	}

	protected List<Object> prepareObjectsForCast(List<Object> params) {
		for (int i = 0; i < params.size(); i++) {
			params.set(i, prepareObjectForCast(params.get(i)));
		}
		return params;
	}

	// TODO renommer les deux methodes et permettre de préparer les objets de manière plus générale !!
	protected Object prepareObjectForCast(Object o) {
		return o;
	}

	private List<String> writeStubs() throws Exception {
		boolean testMockitoEq = config.isTestEqualityOnStubbing();

		List<String> verifyCalledEvent = new ArrayList<String>();
		// TODO refac en petites methodes

		Set<MethodDescriptor> descriptors = eventMap.getMethodDescriptors();
		if (descriptors.size() > 0) {
			writeComment("Mock the stub methods");
			StringBuilder sb = new StringBuilder();
			// TODO refac
			Set<Class<?>> classesWithStaticMock = new HashSet<Class<?>>();
			for (MethodDescriptor descriptor : descriptors) {
				if (descriptor.isStatic()) {
					classesWithStaticMock.add(descriptor.getMethodClass());
				}
			}

			// TODO refac car trop de code
			for (Class<?> classWithStaticMock : classesWithStaticMock) {
				getLogger().onUsedClass(classWithStaticMock, false);
				testCodeLines.add("PowerMockito.mockStatic(" + classWithStaticMock.getSimpleName() + ".class);");
			}

			for (MethodDescriptor descriptor : descriptors) {
				// FIXME si mock static, y a t'il besoin de déclarer la var ?! uniquement si full injection ??
				String mockedClassAsVar = declareClassIsStubbed(descriptor.getMethodClass());

				String mockedObjectVariableName = descriptor.isStatic() ? descriptor.getMethodClass().getSimpleName() : mockedClassAsVar;

				SignatureResolver sigResolver = createSignatureResolverForStubbing(descriptor);

				int callNumberForThisMethod = testMockitoEq ? generateMocksWithParameters(mockedObjectVariableName, descriptor, sigResolver) : generateMocksWithAnyParameters(mockedObjectVariableName, descriptor, sigResolver);

				// Check the call number on this method
				sb.setLength(0);

				String call = buildCallMethod(descriptor.getName(), generateParamsMockitoAny(sigResolver));
				if (descriptor.isStatic()) {
					verifyCalledEvent.add("PowerMockito.verifyStatic(Mockito.times(" + callNumberForThisMethod + "));");
					sb.append(descriptor.getMethodClass().getSimpleName()).append(call).append(";");
				} else {
					sb.append("Mockito.verify(").append(mockedObjectVariableName).append(", Mockito.times(").append(callNumberForThisMethod).append("))").append(call).append(";");
				}

				verifyCalledEvent.add(sb.toString());
			}
			testCodeLines.add(EMPTY_LINE);
		}
		return verifyCalledEvent;
	}

	/**
	 * We have to resolve the signature. We haven't used javassist $sig because sometimes, with generic, we ignore
	 * the real parameter types when the class is modified by javassist. So we have to deduce the real signature
	 * ourselves.
	 */
	protected SignatureResolver createSignatureResolverForStubbing(MethodDescriptor descriptor) {
		SignatureResolver sigResolver = new SignatureResolver(descriptor);
		for (List<Object> parameters : eventMap.getParameters(descriptor)) {
			parameters = prepareObjectsForCast(parameters);
			sigResolver.addCall(parameters);
		}
		return sigResolver;

	}

	protected int generateMocksWithAnyParameters(String mockedObjectVariableName, MethodDescriptor descriptor, SignatureResolver sigResolver) throws IOException {
		List<CallOnMock> allEvents = eventMap.getEvents(descriptor);
		generateStubs(mockedObjectVariableName, descriptor, allEvents, generateParamsMockitoAny(sigResolver));
		return allEvents.size();
	}

	protected int generateMocksWithParameters(String mockedObjectVariableName, MethodDescriptor descriptor, SignatureResolver sigResolver) throws IOException {
		int callNumberForThisMethod = 0;
		for (List<CallOnMock> eventsByParameters : eventMap.getEventsByParameters(descriptor)) {
			callNumberForThisMethod += eventsByParameters.size();

			String methodVarName = descriptor.getName() + SUFFIX_ENTER + "_" + methodInputNumber++;
			// FIXME indexé par MethodParameters pr un truc plus propre (mais étudier le hashcode et equals avant).
			writeParamsAsObjectArray(methodVarName, eventsByParameters.get(0).getParameters(), JsonSerialisationUtils.writeSerializedObjectsInJava(eventsByParameters.get(0).getParameters().getFrozenParametersOnEnter()));
			// Signature is partially resolved but it's enough to use it here.
			String[] params = generateParamsFromArray(sigResolver, methodVarName, true);
			generateStubs(mockedObjectVariableName, descriptor, eventsByParameters, params);
		}
		return callNumberForThisMethod;
	}

	protected void generateStubs(String mockedClassObject, MethodDescriptor eventMethod, List<CallOnMock> eventsOnThisMethod, String[] params) throws IOException {
		List<String> methodInputsOnExitVars = new ArrayList<String>();
		if (params.length > 0) {
			for (CallOnMock event : eventsOnThisMethod) {
				if (event.getException() == null) {
					String methodInputsOnExitVar = eventMethod.getName() + SUFFIX_DIFFS_ON_EXIT + "_" + methodInputNumber++;
					methodInputsOnExitVars.add(methodInputsOnExitVar);
					writeParamsAsObjectArray(methodInputsOnExitVar, event.getParameters(), JsonSerialisationUtils.writeSerializedObjectsInJava(event.getFrozenParameterDifferencesOnExit()));
				}
			}
		}
		Iterator<String> methodInputsOnExitVarsIt = methodInputsOnExitVars.iterator();

		// TODO refac
		String methodWithParams = buildCallMethod(eventMethod.getName(), params);

		StringBuilder sb = new StringBuilder();
		sb.append("Mockito");

		if (!eventMethod.isVoid()) {
			// No void method example:
			// Mockito.when(objectMocked.equals(Mockito.eq(params[0]))).thenThrow((Throwable)null).thenReturn(returnedObject1).thenReturn(returnedObject2);
			sb.append(".when(").append(mockedClassObject).append(methodWithParams).append(")");
		}

		for (CallOnMock event : eventsOnThisMethod) {
			if (eventMethod.isVoid()) {
				if (event.getException() != null) {
					sb.append(".doThrow((Throwable)").append(JsonSerialisationUtils.writeObjectInJava(event.getException())).append(")");
				} else {
					// TODO pas très propre si prob avec le next !
					sb.append(params.length > 0 ? ".doAnswer(exitAnswer(" + methodInputsOnExitVarsIt.next() + "))" : ".doNothing()");
				}
			} else {
				if (event.getException() != null) {
					sb.append(".thenThrow((Throwable)").append(JsonSerialisationUtils.writeObjectInJava(event.getException())).append(")");
				} else {
					String responseVarName = EVENT_RESPONSE_VAR_NAME + methodResponseNumber++;
					testCodeLines.add("Object " + responseVarName + " = " + event.getSerializedResponse() + ";");

					if (params.length > 0) {
						// TODO pas très propre si prob avec le next !
						sb.append(".then(exitAnswer(").append(methodInputsOnExitVarsIt.next()).append(", ").append(responseVarName).append("))");
					} else {
						// FIXME browse all returned type with the same resolver to improve the result (and reduce the number of null call).
						ClassResolver classResolver = new ClassResolver(event.getReturnedClass());
						classResolver.addCall(prepareObjectForCast(event.getResponse()));// TODO voir pr supprimer le getResponse()
						sb.append(".thenReturn(").append(cast(classResolver.resolve(true), responseVarName)).append(")"); // FIXME
					}
				}
			}
		}

		if (eventMethod.isVoid()) {
			// Void Method example
			// Mockito.doThrow((Throwable)null).doNothing().when(objectMocked).equals(Mockito.eq(params[0]));
			sb.append(".when(").append(mockedClassObject).append(")").append(methodWithParams).append(";");
		} else {
			sb.append(";");
		}

		testCodeLines.add(sb.toString());
	}

	private String[] generateParamsFromArray(SignatureResolver sigResolver, String objectArrayVarName, boolean testMockitoEq) {
		List<Class<?>> paramClasses = sigResolver.resolve(true);
		String[] parameters = new String[paramClasses.size()];
		for (int i = 0; i < paramClasses.size(); i++) {
			String param = cast(paramClasses.get(i), objectArrayVarName + "[" + i + "]");
			if (testMockitoEq) {
				param = "Mockito.eq(" + param + ")";
			}
			parameters[i] = param;
		}
		return parameters;
	}

	private String[] generateParamsMockitoAny(SignatureResolver sigResolver) {
		// Don't wrap primitives classes else Mockito.any() will throw a NPE.
		List<Class<?>> paramClasses = sigResolver.resolve(false);
		String[] any = new String[paramClasses.size()];
		for (int i = 0; i < paramClasses.size(); i++) {
			Class<?> paramClass = paramClasses.get(i);
			if (paramClass == boolean.class) {
				any[i] = "Mockito.anyBoolean()";
			} else if (paramClass == byte.class) {
				any[i] = "Mockito.anyByte()";
			} else if (paramClass == char.class) {
				any[i] = "Mockito.anyChar()";
			} else if (paramClass == double.class) {
				any[i] = "Mockito.anyDouble()";
			} else if (paramClass == float.class) {
				any[i] = "Mockito.anyFloat()";
			} else if (paramClass == int.class) {
				any[i] = "Mockito.anyInt()";
			} else if (paramClass == short.class) {
				any[i] = "Mockito.anyShort()";
			} else {
				any[i] = cast(paramClass, "Mockito.any()");
			}
		}
		return any;
	}

	private String cast(Class<?> cls, String var) {
		getLogger().onUsedClass(cls, true);
		return "(" + cls.getSimpleName() + ")" + var;
	}

	@Override
	public void onSpy(Class<?> cls) {
		String spiedClassName = cls.getSimpleName();
		getLogger().onUsedClass(cls, true);
		String spiedClassVarName = toLowerFirstLetter(spiedClassName);
		getLogger().onDeclaratedField(spiedClassName + " " + spiedClassVarName, "@InjectMocks", "@Spy");
	}

	@Override
	public void onEventStart(CallOnMock event) {
		pendingEvent = event;
		try {
			pendingEvent.getParameters().freezeEnter();
		} catch (IOException e) {
			getLogger().onGenerationFail("The event params cannot be frozen on entering because " + e.getMessage(), e);
		}
	}

	@Override
	public void onEventEnd(CallOnMock event) {
		// We are mono threaded, if this listener is alive, it would receive at least a start before.
		try {
			eventMap.put(pendingEvent);
			pendingEvent = null;
			event.freezeResponse();
			event.freezeDiffsExit();
		} catch (Exception e) {
			getLogger().onGenerationFail("The event params and response cannot be frozen on exit because " + e.getMessage(), e);
		}
	}

	@Override
	public boolean canListenEvent(CallOnMock event) {
		// We support mono threaded application only. So an event is a stub. If the method which generates this event
		// will carry on other events, we ignore them !
		return pendingEvent == null || pendingEvent.equals(event);
		// else: other events between the current started one and its future end.
	}

	@Override
	public void onEventCreation(Class<?> eventClass) {
		// TODO refac sur la gestion de la déclaration des mocks.
		declareClassIsStubbed(eventClass);
	}

}