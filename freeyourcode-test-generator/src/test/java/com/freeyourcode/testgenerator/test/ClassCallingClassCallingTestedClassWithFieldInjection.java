package com.freeyourcode.testgenerator.test;

public class ClassCallingClassCallingTestedClassWithFieldInjection {
	
	//Field filled by injection
	private ClassCallingTestedClassWithFieldInjection classCallingTestedClassWithFieldInjection;

	public void myMethodNoParamNoResult(){
		classCallingTestedClassWithFieldInjection.myMethodNoParamNoResult();
	}
	
	public Double myMethodNoParamResult(){
		return classCallingTestedClassWithFieldInjection.myMethodNoParamResult();
	}
	
	public double myMethodNoParamResultPrimitif(){
		return classCallingTestedClassWithFieldInjection.myMethodNoParamResultPrimitif();
	}
	
	public void myMethod1ParamNoResult(Double d){
		classCallingTestedClassWithFieldInjection.myMethod1ParamNoResult(d);
	}
	
	public Double myMethod1ParamResult(Double d){
		return classCallingTestedClassWithFieldInjection.myMethod1ParamResult(d);
	}
	
	public double myMethod1ParamPrimitifResultPrimitif(double d){
		return classCallingTestedClassWithFieldInjection.myMethod1ParamPrimitifResultPrimitif(d);
	}
	
	public void myMethod2ParamNoResult(Double d, TestedBean o){
		classCallingTestedClassWithFieldInjection.myMethod2ParamNoResult(d, o);
	}
	
	public Double myMethod2ParamResult(Double d, TestedBean o){
		return classCallingTestedClassWithFieldInjection.myMethod2ParamResult(d, o);
	}
	
	public double myMethod2ParamPrimitifResultPrimitif(double d, TestedBean o){
		return classCallingTestedClassWithFieldInjection.myMethod2ParamPrimitifResultPrimitif(d, o);
	}
	
	public Double myMethodCallingAPrivateOne(Double d){
		return myPrivateMethod(d);
	}
	
	private Double myPrivateMethod(Double d){
		return classCallingTestedClassWithFieldInjection.myMethodCallingAPrivateOne(d);
	}
	
	public static Double myStaticMethod(Double d){
		return ClassCallingTestedClassWithFieldInjection.myStaticMethod(d);
	}
	
	public static Double myStaticMethodCallingAPrivateOne(Double d){
		return myStaticPrivateMethod(d);
	}
	
	private static Double myStaticPrivateMethod(Double d){
		return ClassCallingTestedClassWithFieldInjection.myStaticMethodCallingAPrivateOne(d);
	}
	

}
