package com.freeyourcode.testgenerator.test;

public class TestedClass {

	public void myMethodNoParamNoResult() {
	}

	public Double myMethodNoParamResult() {
		return Double.valueOf(2d);
	}

	public double myMethodNoParamResultPrimitif() {
		return 2d;
	}

	public void myMethod1ParamNoResult(Double d) {
	}

	public Double myMethod1ParamResult(Double d) {
		return Double.valueOf(2d * d);
	}

	public double myMethod1ParamPrimitifResultPrimitif(double d) {
		return 2d * d;
	}

	public void myMethod2ParamNoResult(Double d, TestedBean o) {
	}

	public Double myMethod2ParamResult(Double d, TestedBean o) {
		return Double.valueOf(2d * d);
	}

	public double myMethod2ParamPrimitifResultPrimitif(double d, TestedBean o) {
		return 2d * d;
	}

	public Double myMethodCallingAPrivateOne(Double d) {
		return myPrivateMethod(d);
	}

	private Double myPrivateMethod(Double d) {
		return Double.valueOf(10d * d);
	}

	public static Double myStaticMethod(Double d) {
		return 3d * d;
	}

	public static Double myStaticMethodCallingAPrivateOne(Double d) {
		return myStaticPrivateMethod(d);
	}

	private static Double myStaticPrivateMethod(Double d) {
		return 4d * d;
	}

	public Double myMethodThrowingAnException(Double d) throws Exception {
		throw new Exception("Exception for tests");
	}

	public TestedBean checkSerializedValueIsDifferentWithSameObject(TestedBean input) {
		input.setValue(2 * input.getValue());
		return input;
	}

	public String myMethodIsModifyingInput(double d, TestedBean o) {
		o.setLibelle("NewLibelle");
		if (o.getSubBean() != null) {
			o.getSubBean().setValue2("NewValueOnSubBean");
			return o.getSubBean().getValue2();
		}
		return "fail";
	}

	public TestedSubBean myMethodIsReturningSubBean(double d, TestedBean o) {
		return o.getSubBean();
	}

	public String myMethodIsReturningSubBeanValue(double d, TestedBean o) {
		return o.getSubBean() != null ? o.getSubBean().getValue2() : null;
	}

}