package com.freeyourcode.testgenerator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;
import com.freeyourcode.test.utils.InputPointerResolver;
import com.freeyourcode.test.utils.deepanalyser.DeepDiff;
import com.freeyourcode.test.utils.deepanalyser.DeepFinder;

public class CallOnMock {

	private final MethodDescriptor descriptor;
	private final MethodParameters parameters;
	protected Object response;
	protected String serializedResponse;
	private Exception exception;
	private final Class<?> returnedClass;

	// We serialize differences between the parameters values on enter and on exit because parameters could be modified during method execution.
	private String[] frozenParameterDifferencesOnExit;

	public CallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
		this.descriptor = descriptor;
		this.parameters = new MethodParameters(parameters);
		this.returnedClass = returnedClass;
	}

	public Object getResponse() {
		return response;
	}

	public CallOnMock setResponse(Object response) {
		this.response = response;
		return this;
	}

	public void freezeResponse() throws Exception {
		if (shouldSerializedResponse()) {
			String pathToInputRef = findResponseInParams();
			serializedResponse = JsonSerialisationUtils.writeObjectInJava(pathToInputRef != null ? new InputPointerResolver(pathToInputRef) : response);
		}
	}

	protected String findResponseInParams() throws Exception {
		return createDeepFinder().find(response, parameters.getInputParams());
	}

	protected boolean shouldSerializedResponse() {
		return response != null && serializedResponse == null;
	}

	protected DeepFinder createDeepFinder() {
		return new DeepFinder();
	}

	public void freezeDiffsExit() throws Exception {
		// Several listeners can ask for a frozen event, we freeze it only once!
		if (frozenParameterDifferencesOnExit == null) {
			List<Map<String, Object>> differencesWithEnter = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < parameters.getFrozenParametersOnEnter().length; i++) {
				// Modified values are updated on exit when test will be executed.
				DeepDiff diffs = createDeepDiff().diff(JsonSerialisationUtils.deserialize(parameters.getFrozenParametersOnEnter()[i]), parameters.getInputParams().get(i));
				differencesWithEnter.add(diffs.getDiffs().size() > 0 ? diffs.getDiffsAsMap() : null);
			}
			frozenParameterDifferencesOnExit = JsonSerialisationUtils.serializeList(differencesWithEnter);
		}
	}

	protected DeepDiff createDeepDiff() {
		return new DeepDiff();
	}

	public String[] getFrozenParameterDifferencesOnExit() {
		return frozenParameterDifferencesOnExit;
	}

	public String getSerializedResponse() {
		return serializedResponse;
	}

	public Exception getException() {
		return exception;
	}

	public CallOnMock setException(Exception exception) {
		this.exception = exception;
		return this;
	}

	public MethodDescriptor getDescriptor() {
		return descriptor;
	}

	public Class<?> getReturnedClass() {
		return returnedClass;
	}

	public MethodParameters getParameters() {
		return parameters;
	}

}