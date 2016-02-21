package com.freeyourcode.testgenerator.core.listener;

import com.freeyourcode.testgenerator.core.CallOnMock;
import com.freeyourcode.testgenerator.core.MethodParameters;

public interface TestGeneratorListener {

	public void onInput(MethodParameters parameters);

	public void onOutput(Object outputValue);

	public void onException(Exception e);

	public boolean canListenEvent(CallOnMock event);

	public void onEventStart(CallOnMock event);

	public void onEventEnd(CallOnMock event);

	public void onEventCreation(Class<?> eventClass);

	void onSpy(Class<?> cls);

}
