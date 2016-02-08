package com.freeyourcode.testgenerator.core;

import java.util.Arrays;



public class MethodDescriptor {
	
	protected final Class<?> methodClass;
	protected final String name;
	protected final boolean isVoid;
	protected final Class<?>[] paramClasses;
	protected final boolean isStatic;

	
	public MethodDescriptor(Class<?> methodClass, String method, boolean isVoid, Class<?>[] paramClasses, boolean isStatic) {
		this.methodClass = methodClass;
		this.name = method;
		this.isVoid = isVoid;
		this.paramClasses = paramClasses;
		this.isStatic = isStatic;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isVoid() {
		return isVoid;
	}

	public Class<?> getMethodClass() {
		return methodClass;
	}

	public Class<?>[] getParamClasses() {
		return paramClasses;
	}

	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public String toString() {
		return "MethodDescriptor [methodClass=" + methodClass + ", name="
				+ name + ", isVoid=" + isVoid + ", paramClasses="
				+ Arrays.toString(paramClasses) + ", isStatic=" + isStatic
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isStatic ? 1231 : 1237);
		result = prime * result + (isVoid ? 1231 : 1237);
		result = prime * result
				+ ((methodClass == null) ? 0 : methodClass.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Arrays.hashCode(paramClasses);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodDescriptor other = (MethodDescriptor) obj;
		if (isStatic != other.isStatic)
			return false;
		if (isVoid != other.isVoid)
			return false;
		if (methodClass == null) {
			if (other.methodClass != null)
				return false;
		} else if (!methodClass.equals(other.methodClass))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (!Arrays.equals(paramClasses, other.paramClasses))
			return false;
		return true;
	}

}