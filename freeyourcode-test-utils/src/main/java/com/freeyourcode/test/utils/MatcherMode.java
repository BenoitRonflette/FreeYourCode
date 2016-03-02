package com.freeyourcode.test.utils;

import org.apache.commons.lang.StringUtils;

public enum MatcherMode {

	SOFT("soft"), MEDIUM("medium"), HARD("hard");

	private String value;

	MatcherMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static MatcherMode fromValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		for (MatcherMode mode : MatcherMode.values()) {
			if (mode.getValue().equals(value.toLowerCase())) {
				return mode;
			}
		}
		return null;
	}

	public static MatcherMode defaultMode() {
		return MatcherMode.SOFT;
	}
}
