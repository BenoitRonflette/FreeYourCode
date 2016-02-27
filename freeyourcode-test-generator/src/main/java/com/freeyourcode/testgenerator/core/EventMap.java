package com.freeyourcode.testgenerator.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

public class EventMap {

	private static class EventsOnMethod {
		// All received events in reception order
		private final List<CallOnMock> events = Lists.newLinkedList();

		// All received events by call parameters in reception order (we have to classify the events when we receive the call
		// because the input could change later). This is
		private final ArrayListMultimap<List<Object>, CallOnMock> eventsByParameters = ArrayListMultimap.create();

		public EventsOnMethod() {
		}

		public void addEvent(CallOnMock event) {
			events.add(event);
			eventsByParameters.put(Lists.newArrayList(event.getParameters().getInputParams()), event);
		}

	}

	private final Map<MethodDescriptor, EventsOnMethod> eventsByMethodDescriptor = new LinkedHashMap<MethodDescriptor, EventsOnMethod>();

	public EventMap() {
	}

	public void put(CallOnMock event) {
		EventsOnMethod eventsOnMethod = eventsByMethodDescriptor.get(event.getDescriptor());
		if (eventsOnMethod == null) {
			eventsOnMethod = new EventsOnMethod();
			eventsByMethodDescriptor.put(event.getDescriptor(), eventsOnMethod);
		}
		eventsOnMethod.addEvent(event);
	}

	@SuppressWarnings("unchecked")
	public Collection<List<CallOnMock>> getEventsByParameters(MethodDescriptor descriptor) {
		EventsOnMethod eventsOnMethod = eventsByMethodDescriptor.get(descriptor);
		return eventsOnMethod != null ? eventsOnMethod.eventsByParameters.asMap().values() : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public List<CallOnMock> getEvents(MethodDescriptor descriptor) {
		EventsOnMethod eventsOnMethod = eventsByMethodDescriptor.get(descriptor);
		return eventsOnMethod != null ? eventsOnMethod.events : Collections.EMPTY_LIST;
	}

	@SuppressWarnings("unchecked")
	public Set<List<Object>> getParameters(MethodDescriptor descriptor) {
		EventsOnMethod eventsOnMethod = eventsByMethodDescriptor.get(descriptor);
		return eventsOnMethod != null ? eventsOnMethod.eventsByParameters.keySet() : Collections.EMPTY_SET;
	}

	public Set<MethodDescriptor> getMethodDescriptors() {
		return eventsByMethodDescriptor.keySet();
	}

}