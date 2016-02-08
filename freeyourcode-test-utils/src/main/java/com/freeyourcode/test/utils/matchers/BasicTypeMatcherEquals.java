package com.freeyourcode.test.utils.matchers;

import java.util.List;

import org.hamcrest.Description;

import com.freeyourcode.test.utils.deepanalyser.DeepDiff;

/**
 * This matcher performs a deep analyze to check that basic type values (Primitives, String, Date) are the same.
 * It uses the DeepDiff.diff() method to check the basic type value differences only.
 * We cannot use the Object.equals() because it could be not implemented on objects so the default Object.equals() will be used, so all
 * objects with a different memory address will be different !
 * We cannot use Objects.deepEquals(expectedValue, item) (Java 7) or EqualsBuilder.reflectionEquals(expectedValue, item) because on
 * Collection or Map, the built equals() won't be true as expected in Javadoc.
 * So we prefer to use our own different analyzer but according to your requirement, you can extend MatcherEquals and build your own !
 */
public class BasicTypeMatcherEquals extends MatcherEquals {

	private List<String> differences;

	public BasicTypeMatcherEquals(Object expectedValue) {
		super(expectedValue);
	}
	
	@Override
	public void describeTo(final Description description) {	
		description.appendText("Basic type values (Primitives, String, Date) have to be the same"); 
	}
	
	@Override
	public void describeMismatch(Object item, Description description) {
		description.appendValueList("", "\n", "", differences);
	}

	@Override
	public boolean matches(final Object item) {
		try {
			differences = DeepDiff.diff(expectedValue, item);
			return differences.size() == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
