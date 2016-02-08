package com.freeyourcode.testgenerator.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class EventMap {
	
	private final Map<MethodDescriptor, ArrayListMultimap<List<Object>, Event>> eventsByCallByMethodDescriptor
							= new HashMap<MethodDescriptor, ArrayListMultimap<List<Object>, Event>>();
	
	public EventMap(){
	}
	
	public void put( Event event) {
		MethodDescriptor descriptor = event.getDescriptor();
		ArrayListMultimap<List<Object>, Event> methodByParameters = eventsByCallByMethodDescriptor.get(descriptor);
		if(methodByParameters == null){
			methodByParameters = ArrayListMultimap.create();
			eventsByCallByMethodDescriptor.put(descriptor, methodByParameters);
		}
		methodByParameters.put(Lists.newArrayList(event.getParameters().getInputParams()), event);
	}
	
	@SuppressWarnings("unchecked")
	public List<Event> getEvents(MethodDescriptor descriptor, List<Object> parameters) {
		ArrayListMultimap<List<Object>, Event> params = eventsByCallByMethodDescriptor.get(descriptor);
		return params != null ? params.get(parameters) : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public Set<List<Object>> getParameters(MethodDescriptor descriptor) {
		ArrayListMultimap<List<Object>, Event> params = eventsByCallByMethodDescriptor.get(descriptor);
		return params != null ? params.keySet() : Collections.EMPTY_SET;
	}
	
	public Set<MethodDescriptor> getMethodDescriptors() {
		return eventsByCallByMethodDescriptor.keySet();
	} 
	
}