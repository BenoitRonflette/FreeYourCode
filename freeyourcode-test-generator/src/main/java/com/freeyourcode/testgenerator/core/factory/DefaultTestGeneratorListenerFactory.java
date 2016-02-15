package com.freeyourcode.testgenerator.core.factory;

import com.freeyourcode.testgenerator.core.CallOnMock;
import com.freeyourcode.testgenerator.core.ListenerManagerConfig;
import com.freeyourcode.testgenerator.core.MethodDescriptor;
import com.freeyourcode.testgenerator.core.listener.DefaultTestGeneratorListener;
import com.freeyourcode.testgenerator.core.listener.TestGeneratorListener;

public class DefaultTestGeneratorListenerFactory implements TestGeneratorListenerFactory {

	@Override
	public TestGeneratorListener create(int testId, MethodDescriptor descriptor, ListenerManagerConfig config) {
		return new DefaultTestGeneratorListener(testId, descriptor, config);
	}

	@Override
	public CallOnMock createAssociatedCallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
		return new CallOnMock(descriptor, parameters, returnedClass);
	}

}
