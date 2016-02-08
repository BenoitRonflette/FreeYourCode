package com.freeyourcode.testgenerator.core.utils;

import com.google.common.primitives.Primitives;

/**
 * We have to cast deserialized variable, however, we cannot use $sig only because according to the moment where the class is
 * loaded and modified by the agent, the generic methods could be not resolved. So, we could get a type as the extended generic
 * one instead of the real implementation. We have to try to deduce the real type when a type is an Object (we are going to cast
 * with the "greatest common divisor" between the values used to call the method.
 * If a param is always called with null, we cannot perform this deduction !
 *
 */
public class ClassResolver {
	
	private Class<?> deducedFromCall;
	private final Class<?> declaredClass;
	
	public ClassResolver(Class<?> declaredClass) {
		this.declaredClass = declaredClass;
	}
	
	public void addCall(Object param){
		if(param != null){
			if(deducedFromCall == null){
				deducedFromCall = param.getClass();
			}
			else{
				deducedFromCall = resolveAncestor(deducedFromCall, param.getClass());
			}
		}
	}
	
	private Class<?> resolveAncestor(Class<?> c1, Class<?> c2){
		if(c1 == c2){
			return c1;
		}
		else if(c1.isAssignableFrom(c2)){
			return c1;
		}
		else if(c2.isAssignableFrom(c1)){
			return c2;
		}
		return resolveAncestor(c1.getSuperclass(), c2.getSuperclass());
	}
	
	public Class<?> resolve(boolean wrapPrimitives){
			//It's a primitive type, we don't try to use the resolved type
			if(declaredClass.isPrimitive()){
				return wrapPrimitives ? Primitives.wrap(declaredClass) : declaredClass;
			}
			else{
				//we use the deduced type if it's not null (i.e. at least one call with param != null)
				return deducedFromCall != null ? deducedFromCall : declaredClass;
			}
	}
	
}
