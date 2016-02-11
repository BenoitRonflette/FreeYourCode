package com.freeyourcode.test.utils;

import com.freeyourcode.test.utils.deepanalyser.DeepResolver;

public class InputPointerResolver {

	private final String pathToInput;

	public InputPointerResolver(String pathToInput) {
		this.pathToInput = pathToInput;
	}

	public Object resolve(Object params) throws Exception {
		return DeepResolver.resolve(pathToInput, params);
	}

}
