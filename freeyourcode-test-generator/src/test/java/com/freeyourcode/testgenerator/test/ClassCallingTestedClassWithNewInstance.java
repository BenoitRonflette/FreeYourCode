package com.freeyourcode.testgenerator.test;

public class ClassCallingTestedClassWithNewInstance {
	

	public void myMethodNoParamNoResult(){
		new TestedClass().myMethodNoParamNoResult();
	}
	
	public Double myMethodNoParamResult(){
		return new TestedClass().myMethodNoParamResult();
	}
	
	public double myMethodNoParamResultPrimitif(){
		return new TestedClass().myMethodNoParamResultPrimitif();
	}
	
	public void myMethod1ParamNoResult(Double d){
		new TestedClass().myMethod1ParamNoResult(d);
	}
	
	public Double myMethod1ParamResult(Double d){
		return new TestedClass().myMethod1ParamResult(d);
	}
	
	public double myMethod1ParamPrimitifResultPrimitif(double d){
		return new TestedClass().myMethod1ParamPrimitifResultPrimitif(d);
	}
	
	public void myMethod2ParamNoResult(Double d, TestedBean o){
		new TestedClass().myMethod2ParamNoResult(d, o);
	}
	
	public Double myMethod2ParamResult(Double d, TestedBean o){
		return new TestedClass().myMethod2ParamResult(d, o);
	}
	
	public double myMethod2ParamPrimitifResultPrimitif(double d, TestedBean o){
		return new TestedClass().myMethod2ParamPrimitifResultPrimitif(d, o);
	}
	
	public Double myMethodCallingAPrivateOne(Double d){
		return myPrivateMethod(d);
	}
	
	private Double myPrivateMethod(Double d){
		return new TestedClass().myMethodCallingAPrivateOne(d);
	}
	
	public static Double myStaticMethod(Double d){
		return TestedClass.myStaticMethod(d);
	}
	
	public static Double myStaticMethodCallingAPrivateOne(Double d){
		return myStaticPrivateMethod(d);
	}
	
	private static Double myStaticPrivateMethod(Double d){
		return TestedClass.myStaticMethodCallingAPrivateOne(d);
	}

}
