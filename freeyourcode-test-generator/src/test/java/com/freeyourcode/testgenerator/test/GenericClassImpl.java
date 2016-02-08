package com.freeyourcode.testgenerator.test;


public class GenericClassImpl extends AbstractGenericClass<Double>{
	@Override
	public Double compute() {
		return new Double(7d);
	}

	@Override
	public void compute(Double v) {
		return;	
	}
	
	@Override
	public void abstractCompute(Double v) {
		super.abstractCompute(v);
	}
}