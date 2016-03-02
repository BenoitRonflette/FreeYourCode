package com.freeyourcode.test.utils.matchers;

import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.freeyourcode.test.utils.deepanalyser.DeepDiff;
import com.google.common.primitives.Primitives;

public class MediumMockitoEqMatcher<T> extends ArgumentMatcher<T> {

	protected Object expectedValue;

	public MediumMockitoEqMatcher(final Object expectedValue) {
		this.expectedValue = expectedValue;
	}

	@Override
	public boolean matches(Object actual) {
		try {
			return new DeepDiff().diff(expectedValue, actual).getDiffs().size() == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static <T> T eq(T v) {
		// From Mockito argThat doc, when the parameter is a primitive then you must use relevant intThat(), floatThat(), etc. method.
		// This way you will avoid NullPointerException during autounboxing.
		// => We prefer to use Mockito.eq if the argument is a primitive-like object.
		if (v != null && Primitives.unwrap(v.getClass()).isPrimitive()) {
			return Mockito.eq(v);
		}
		return Matchers.argThat(new MediumMockitoEqMatcher<T>(v));
	}

}
