package com.freeyourcode.test.utils.matchers;

import org.hamcrest.BaseMatcher;

public abstract class MatcherEquals extends BaseMatcher<Object> {

	protected Object expectedValue;


	public MatcherEquals(final Object expectedValue) {
		this.expectedValue = expectedValue;
	}

}