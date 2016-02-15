package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.Lists;

public class DeepAnalyser {

	protected final Set<Integer> visitedObject = new HashSet<Integer>();

	/**
	 * If the Object has already been visited, we ignore it!
	 */
	protected boolean shouldVisit(Object o) {
		int idCode = System.identityHashCode(o);
		return visitedObject.add(idCode);
	}

	protected static boolean isBasicType(Class<?> type) {
		return type.isPrimitive() || String.class == type || Date.class.isAssignableFrom(type);
	}

	/**
	 * Get all fields (inherited and private too).
	 */
	public static List<Field> getAllField(Class<?> c) {
		List<Field> fields = Lists.newArrayList();
		while (c.getSuperclass() != null) {
			for (Field field : c.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					fields.add(field);
				}
			}
			// We don't process the Object fields.
			c = c.getSuperclass();
		}
		return fields;
	}

	protected boolean isNonDeterministCollection(Class<?> c) {
		return !List.class.isAssignableFrom(c) && !SortedSet.class.isAssignableFrom(c) && !LinkedHashSet.class.isAssignableFrom(c);
	}

	protected <T> Collection<T> determine(Collection<T> c) {
		List<T> determinedC = new ArrayList<T>(c);
		Collections.sort(determinedC, new HashCodeComparator());
		return determinedC;
	}

}
