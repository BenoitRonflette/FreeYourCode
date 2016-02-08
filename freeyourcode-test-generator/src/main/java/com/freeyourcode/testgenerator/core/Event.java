package com.freeyourcode.testgenerator.core;

import java.io.IOException;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;


public class Event {
	
	private final MethodDescriptor descriptor;
	private final MethodParameters parameters;
	private Object response;
	private String serializedResponse;
	private Exception exception;
	private final Class<?> returnedClass;
	
	public Event(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
		this.descriptor = descriptor;
		this.parameters = new MethodParameters(parameters);
		this.returnedClass = returnedClass;
	}

	public Object getResponse() {
		return response;
	}

	public Event setResponse(Object response)  {
		this.response = response;
		return this;
	}
	
	public void freezeResponse() throws IOException{
		if(response != null && serializedResponse == null){
			serializedResponse = JsonSerialisationUtils.writeObject(response);
		}
	}

	public String getSerializedResponse() {
		return serializedResponse;
	}

	public Exception getException() {
		return exception;
	}

	public Event setException(Exception exception) {
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