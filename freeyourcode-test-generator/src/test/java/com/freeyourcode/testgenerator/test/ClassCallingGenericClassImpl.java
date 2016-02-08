package com.freeyourcode.testgenerator.test;

public class ClassCallingGenericClassImpl {
	
	public Double compute(){
		return new GenericClassImpl().compute();
	}
	
	public void compute(Double d){
		new GenericClassImpl().compute(d);
	}
	
	public void abstractCompute(Double d){
		new GenericClassImpl().abstractCompute(d);
	}
	
	
}
