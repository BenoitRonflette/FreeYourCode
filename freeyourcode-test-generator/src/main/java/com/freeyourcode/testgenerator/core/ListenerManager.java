package com.freeyourcode.testgenerator.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.freeyourcode.testgenerator.core.factory.DefaultTestGeneratorListenerFactory;
import com.freeyourcode.testgenerator.core.factory.TestGeneratorListenerFactory;
import com.freeyourcode.testgenerator.core.listener.TestGeneratorListener;
import com.freeyourcode.testgenerator.server.ServerStateListener;
import com.google.common.base.Preconditions;

public class ListenerManager implements ServerStateListener {

	private static class MethodStack extends LinkedList<TestGeneratorListener> {
		private static final long serialVersionUID = 8656389576594141981L;
	}

	private boolean isStarted;

	// FIXME est ce qu'utiliser la meme stack pour des methodes de meme nom avec differents params pourrait poser un probleme ?!
	private final Map<String, MethodStack> listeners = new HashMap<String, MethodStack>();
	private int testId = 0;
	private final LinkedList<CallOnMock> pendingEvents = new LinkedList<CallOnMock>();

	private TestGeneratorListenerFactory factory;
	private final ListenerManagerConfig config;

	public ListenerManager(ListenerManagerConfig config) {
		this.config = config;
		factory = new DefaultTestGeneratorListenerFactory();
	}

	private int generateId() {
		return testId++;
	}

	public void onMethodInput(Class<?> methodClass, String methodName, boolean isVoidMethod, Object[] params, Class<?>[] paramClasses, boolean isStatic) {
		if (!isStarted) {
			return;
		}

		MethodStack currentStack = listeners.get(methodName);
		if (currentStack == null) {
			currentStack = new MethodStack();
			listeners.put(methodName, currentStack);
		}

		MethodDescriptor descriptor = new MethodDescriptor(methodClass, methodName, isVoidMethod, paramClasses, isStatic);
		TestGeneratorListener listener = factory.create(generateId(), descriptor, config);
		currentStack.add(listener);
		listener.onInput(new MethodParameters(params));
	}

	private TestGeneratorListener removeListener(String methodName) {
		MethodStack currentStack = listeners.get(methodName);
		if (currentStack != null) {
			TestGeneratorListener listener = currentStack.removeLast();
			if (currentStack.isEmpty()) {
				listeners.remove(methodName);
			}
			return listener;
		}
		return null;
	}

	public void onMethodOutput(String methodName, Object returnedValue) {
		if (!isStarted) {
			return;
		}

		TestGeneratorListener listener = removeListener(methodName);
		if (listener != null) {
			listener.onOutput(returnedValue);
		} else {
			config.getLogger().onGenerationFail("Un onMethodOutput a été appelé sur une stack vide pour la méthode " + methodName, null);
		}
	}

	public void onException(String methodName, Exception e) {
		if (!isStarted) {
			return;
		}

		TestGeneratorListener listener = removeListener(methodName);
		if (listener != null) {
			listener.onException(e);
		} else {
			config.getLogger().onGenerationFail("Un onException a été appelé sur une stack vide pour la méthode " + methodName, null);
		}
	}

	public void onEventIn(Class<?> methodClass, String methodName, boolean voidMethod, Object[] params, Class<?>[] paramClasses, Class<?> returnedClass, boolean isStatic) {
		if (!isStarted) {
			return;
		}

		if (listeners.size() > 0) {
			MethodDescriptor descriptor = new MethodDescriptor(methodClass, methodName, voidMethod, paramClasses, isStatic);
			CallOnMock event = factory.createAssociatedCallOnMock(descriptor, params, returnedClass);
			pendingEvents.add(event);

			for (MethodStack stack : listeners.values()) {
				for (TestGeneratorListener listener : stack) {
					if (listener.canListenEvent(event)) {
						listener.onEventStart(event);
					}
				}
			}
		}
	}

	public void onEventOut(Object outputValue) {
		if (!isStarted) {
			return;
		}

		if (listeners.size() > 0) {
			if (pendingEvents.size() > 0) {
				fireEventIsFinished(pendingEvents.removeLast().setResponse(outputValue));
			} else {
				config.getLogger().onGenerationFail("Receiving an event out whereas there has not been an 'in', output=" + outputValue, null);
			}
		}
	}

	public void onEventException(Exception e) {
		if (!isStarted) {
			return;
		}

		if (listeners.size() > 0) {
			if (pendingEvents.size() > 0) {
				fireEventIsFinished(pendingEvents.removeLast().setException(e));
			} else {
				config.getLogger().onGenerationFail("Receiving an event exception whereas there has not been an 'in', exception=" + e.getMessage(), e);
			}
		}
	}

	private void fireEventIsFinished(CallOnMock finishedEvent) {
		for (MethodStack stack : listeners.values()) {
			for (TestGeneratorListener listener : stack) {
				if (listener.canListenEvent(finishedEvent)) {
					listener.onEventEnd(finishedEvent);
				}
			}
		}
	}

	public void onSpy(Class<?> spiedClass) {
		if (!isStarted) {
			return;
		}

		for (MethodStack stack : listeners.values()) {
			for (TestGeneratorListener listener : stack) {
				listener.onSpy(spiedClass);
			}
		}
	}

	public void setFactory(TestGeneratorListenerFactory factory) {
		this.factory = Preconditions.checkNotNull(factory, "Factory cannot be null");
	}

	public ListenerManagerConfig getConfig() {
		return config;
	}

	@Override
	public void startKilling() {
		listeners.clear();
		pendingEvents.clear();
		isStarted = true;
	}

	@Override
	public void stopKilling() {
		isStarted = false;
	}

}