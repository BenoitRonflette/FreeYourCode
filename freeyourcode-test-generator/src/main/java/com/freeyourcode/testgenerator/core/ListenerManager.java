package com.freeyourcode.testgenerator.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.freeyourcode.testgenerator.core.factory.DefaultTestGeneratorListenerFactory;
import com.freeyourcode.testgenerator.core.factory.TestGeneratorListenerFactory;
import com.freeyourcode.testgenerator.core.listener.TestGeneratorListener;
import com.google.common.base.Preconditions;

public class ListenerManager {
	
	private static class MethodStack extends LinkedList<TestGeneratorListener>{
		private static final long serialVersionUID = 8656389576594141981L;
	}
	
	//FIXME est ce qu'utiliser la meme stack pour des methodes de meme nom avec differents params pourrait poser un probleme ?!
	private final Map<String, MethodStack> listeners = new HashMap<String, MethodStack>();
	private int testId = 0;
	private final LinkedList<Event> pendingEvents = new LinkedList<Event>();

	private TestGeneratorListenerFactory factory;
	private final ListenerManagerConfig config;
	
	public ListenerManager(ListenerManagerConfig config) {
		this.config = config;
		factory = new DefaultTestGeneratorListenerFactory();
	}
	
	private int generateId(){
		return testId++;
	}
	
	public void onMethodInput(Class<?> methodClass, String methodName, boolean isVoidMethod, Object[] params, Class<?>[] paramClasses, boolean isStatic){
		MethodStack currentStack = listeners.get(methodName);
		if(currentStack == null){
			currentStack = new MethodStack();
			listeners.put(methodName, currentStack);
		}
		
		MethodDescriptor descriptor = new MethodDescriptor(methodClass, methodName, isVoidMethod, paramClasses,isStatic);
		TestGeneratorListener listener = factory.create(generateId(), descriptor, config);
		currentStack.add(listener);
		listener.onInput(new MethodParameters(params));
	}
	
	private TestGeneratorListener removeListener(String methodName){
		MethodStack currentStack = listeners.get(methodName);
		if(currentStack != null){
			TestGeneratorListener listener = currentStack.removeLast();
			if(currentStack.isEmpty()){
				listeners.remove(methodName);
			}
			return listener;
		}
		return null;
	}
	
	public void onMethodOutput(String methodName, Object returnedValue){
		TestGeneratorListener listener = removeListener(methodName);
		if(listener != null){
			listener.onOutput(returnedValue);
		}
		else{
			config.getLogger().onGenerationFail("Un onMethodOutput a �t� appel� sur une stack vide pour la m�thode "+methodName, null);
		}
	}
	
	public void onException(String methodName, Exception e){
		TestGeneratorListener listener = removeListener(methodName);
		if(listener != null){
			listener.onException(e);
		}
		else{
			config.getLogger().onGenerationFail("Un onException a �t� appel� sur une stack vide pour la m�thode "+methodName, null);
		}
	}
	
	public void onEventIn(Class<?> methodClass, String methodName, boolean voidMethod, Object[] params, Class<?>[] paramClasses, Class<?> returnedClass, boolean isStatic){
		if(listeners.size() > 0){
			MethodDescriptor descriptor = new MethodDescriptor(methodClass, methodName, voidMethod, paramClasses, isStatic);
			Event event = new Event(descriptor, params, returnedClass);
			pendingEvents.add(event);

			for(MethodStack stack : listeners.values()){
				for(TestGeneratorListener listener : stack){
					if(listener.canListenEvent(event)){
						listener.onEventStart(event);
					}
				}
			}
		}
	}
	
	public void onEventOut(Object outputValue){
		if(listeners.size() > 0){
			if(pendingEvents.size() > 0){
				fireEventIsFinished(pendingEvents.removeLast().setResponse(outputValue));
			}
			else{
				config.getLogger().onGenerationFail("Receiving an event out whereas there has not been an 'in', output="+outputValue, null);
			}
		}
	}
	
	public void onEventException(Exception e){
		if(listeners.size() > 0){
			if(pendingEvents.size() > 0){
				fireEventIsFinished(pendingEvents.removeLast().setException(e));
			}
			else{
				config.getLogger().onGenerationFail("Receiving an event exception whereas there has not been an 'in', exception="+e.getMessage(), e);
			}
		}
	}
	
	private void fireEventIsFinished(Event finishedEvent){
		for(MethodStack stack : listeners.values()){
			for(TestGeneratorListener listener : stack){
				if(listener.canListenEvent(finishedEvent)){
					listener.onEventEnd(finishedEvent);
				}
			}
		}
	}
	
	public void onSpy(Class<?> spiedClass){
		for(MethodStack stack : listeners.values()){
			for(TestGeneratorListener listener : stack){
				listener.onSpy(spiedClass);
			}
		}
	}
	
	public void setFactory(TestGeneratorListenerFactory factory){
		this.factory = Preconditions.checkNotNull(factory, "Factory cannot be null");
	}

	public ListenerManagerConfig getConfig() {
		return config;
	}
	
}