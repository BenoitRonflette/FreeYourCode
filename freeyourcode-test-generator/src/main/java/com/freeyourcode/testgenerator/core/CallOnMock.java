package com.freeyourcode.testgenerator.core;

import java.io.IOException;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;


public class CallOnMock {
	
	private final MethodDescriptor descriptor;
	private final MethodParameters parameters;
	private Object response;
	private String serializedResponse;
	private Exception exception;
	private final Class<?> returnedClass;
	
	public CallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
		this.descriptor = descriptor;
		this.parameters = new MethodParameters(parameters);
		this.returnedClass = returnedClass;
	}

	public Object getResponse() {
		return response;
	}

	public CallOnMock setResponse(Object response)  {
		this.response = response;
		return this;
	}
	
	public void freezeResponse() throws IOException{
		if(response != null && serializedResponse == null){
			serializedResponse = JsonSerialisationUtils.writeObjectInJava(response);
		}
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