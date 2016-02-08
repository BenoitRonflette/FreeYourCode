package com.freeyourcode.testgenerator.test;

public interface GenericClass<T>{	
	T compute();
	
	void compute(T v);
	
	void abstractCompute(T v);
}
