package com.freeyourcode.testgenerator.core.factory;

import com.freeyourcode.testgenerator.core.CallOnMock;
import com.freeyourcode.testgenerator.core.ListenerManagerConfig;
import com.freeyourcode.testgenerator.core.MethodDescriptor;
import com.freeyourcode.testgenerator.core.listener.TestGeneratorListener;

public interface TestGeneratorListenerFactory {

	TestGeneratorListener create(int testId, MethodDescriptor descriptor, ListenerManagerConfig config);

	CallOnMock createAssociatedCallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass);

}
