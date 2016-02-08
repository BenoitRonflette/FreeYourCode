package com.freeyourcode.testgenerator.core.listener;

import com.freeyourcode.testgenerator.core.Event;
import com.freeyourcode.testgenerator.core.MethodParameters;


public interface TestGeneratorListener {
	
	public void onInput(MethodParameters parameters);
	
	public void onOutput(Object outputValue);
	
	public void onException(Exception e);
	
	public boolean canListenEvent(Event event);
	
	public void onEventStart(Event event);
	
	public void onEventEnd(Event event);

	void onSpy(Class<?> cls);

}
