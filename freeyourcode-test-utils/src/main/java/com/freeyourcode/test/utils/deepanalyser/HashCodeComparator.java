package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Field;
import java.util.Comparator;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class HashCodeComparator implements Comparator<Object> {

	@Override
	public int compare(Object arg0, Object arg1) {
		// Null values are after the not null values.
		if (arg0 == null) {
			return arg1 == null ? 0 : 1;
		} else if (arg1 == null) {
			return -1;
		}

		try {
			return Integer.valueOf(reflectionHashCode(arg0)).compareTo(reflectionHashCode(arg1));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int reflectionHashCode(Object o) throws IllegalArgumentException, IllegalAccessException {
		HashCodeBuilder builder = new HashCodeBuilder();
		for (Field field : DeepAnalyser.getAllField(o.getClass())) {
			boolean wasAccessible = field.isAccessible();
			if (!wasAccessible) {
				field.setAccessible(true);
			}

			Object v = field.get(o);
			if (v != null && DeepAnalyser.isBasicType(v.getClass())) {
				builder.append(v);
			}

			if (!wasAccessible) {
				field.setAccessible(false);
			}
		}
		return builder.toHashCode();

	}

}
