package com.freeyourcode.testgenerator.test;

public class ClassWithNewObjectWithoutCall {

	private TestedBean bean;

	public void myMethodIsCreatingANewInstanceOfTestedClassInitializedWithAlreadySetField() {
		new TestedClass(bean);
	}
}
