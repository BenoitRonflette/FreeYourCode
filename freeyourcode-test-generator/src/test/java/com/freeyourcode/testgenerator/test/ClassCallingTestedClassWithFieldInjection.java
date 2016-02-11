package com.freeyourcode.testgenerator.test;

public class ClassCallingTestedClassWithFieldInjection {

	// Field filled by injection
	private TestedClass testedClass;

	public void myMethodNoParamNoResult() {
		testedClass.myMethodNoParamNoResult();
	}

	public Double myMethodNoParamResult() {
		return testedClass.myMethodNoParamResult();
	}

	public double myMethodNoParamResultPrimitif() {
		return testedClass.myMethodNoParamResultPrimitif();
	}

	public void myMethod1ParamNoResult(Double d) {
		testedClass.myMethod1ParamNoResult(d);
	}

	public Double myMethod1ParamResult(Double d) {
		return testedClass.myMethod1ParamResult(d);
	}

	public double myMethod1ParamPrimitifResultPrimitif(double d) {
		return testedClass.myMethod1ParamPrimitifResultPrimitif(d);
	}

	public void myMethod2ParamNoResult(Double d, TestedBean o) {
		testedClass.myMethod2ParamNoResult(d, o);
	}

	public Double myMethod2ParamResult(Double d, TestedBean o) {
		return testedClass.myMethod2ParamResult(d, o);
	}

	public double myMethod2ParamPrimitifResultPrimitif(double d, TestedBean o) {
		return testedClass.myMethod2ParamPrimitifResultPrimitif(d, o);
	}

	public Double myMethodCallingAPrivateOne(Double d) {
		return myPrivateMethod(d);
	}

	private Double myPrivateMethod(Double d) {
		return testedClass.myMethodCallingAPrivateOne(d);
	}

	public static Double myStaticMethod(Double d) {
		return TestedClass.myStaticMethod(d);
	}

	public static Double myStaticMethodCallingAPrivateOne(Double d) {
		return myStaticPrivateMethod(d);
	}

	private static Double myStaticPrivateMethod(Double d) {
		return TestedClass.myStaticMethodCallingAPrivateOne(d);
	}

	public String getModifiedValue2FromMyMethodIsModifyingInput(double d, TestedBean o) {
		return testedClass.myMethodIsModifyingInput(d, o);
	}

	public TestedSubBean myMethodIsReturningSubBean(double d, TestedBean o) {
		return testedClass.myMethodIsReturningSubBean(d, o);
	}

	public String myMethodIsReturningSubBeanValue(double d, TestedBean o) {
		return testedClass.myMethodIsReturningSubBeanValue(d, o);
	}

}
