package com.freeyourcode.test.utils;

public class ManualInjectionData {
	
	public Object data;
	
	public Class<?> usedClass;

	public ManualInjectionData(Object data, Class<?> usedClass) {
		super();
		this.data = data;
		this.usedClass = usedClass;
	}

}
