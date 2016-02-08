package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class DeepAnalyser {
	
	private final Set<Integer> visitedObject = new HashSet<Integer>();
	
	/**
	 * If the Object has already been visited, we ignore it!
	 */
	protected boolean shouldVisit(Object o){
		int idCode = System.identityHashCode(o);
		return visitedObject.add(idCode);	
	}
	
	protected static boolean isBasicType(Class<?> type){
		return type.isPrimitive() || String.class == type || Date.class.isAssignableFrom(type);
	}
	
	/**
	 * Get all fields (inherited and private too).
	 */
	protected static List<Field> getAllField(Class<?> c){
		List<Field> fields = Lists.newArrayList();
		while(c.getSuperclass() != null){
			for(Field field : c.getDeclaredFields()){
				if(!Modifier.isStatic(field.getModifiers())){
					fields.add(field);
				}
			}
			//We don't process the Object fields.
			c = c.getSuperclass();
		}
		return fields;
	}

}
