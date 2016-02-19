package com.freeyourcode.testgenerator.test;

public class SuperTestedClass {

	public int methodFromSuperClassNotOverridden(int value) {
		return value * -1;
	}

	public int methodFromSuperClassNotOverriddenButSkippedBySuperClassName(int value) {
		return value * -2;
	}

	public int methodFromSuperClassNotOverriddenButSkipped(int value) {
		return value * -3;
	}

	public int methodFromSuperClassButOverridden(int value) {
		return value * -4;
	}
}