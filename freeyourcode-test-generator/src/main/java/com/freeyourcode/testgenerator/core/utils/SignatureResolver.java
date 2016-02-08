package com.freeyourcode.testgenerator.core.utils;

import java.util.ArrayList;
import java.util.List;

import com.freeyourcode.testgenerator.core.MethodDescriptor;
import com.google.common.base.Preconditions;

/**
 * We have to cast deserialized variable, however, we cannot use $sig only because according to the moment where the class is
 * loaded and modified by the agent, the generic methods could be not resolved. So, we could get a type as the extended generic
 * one instead of the real implementation. We have to try to deduce the real type when a type is an Object (we are going to cast
 * with the "greatest common divisor" between the values used to call the method.
 * If a param is always called with null, we cannot perform this deduction !
 *
 */
public class SignatureResolver {
	
	private final List<ClassResolver> classResolvers;

	public SignatureResolver(MethodDescriptor descriptor) {
		classResolvers = new ArrayList<ClassResolver>(descriptor.getParamClasses().length);
		for(Class<?> declaredClass : descriptor.getParamClasses()){
			classResolvers.add(new ClassResolver(declaredClass));
		}
	}
	
	public void addCall(List<Object> params){
		Preconditions.checkNotNull(params, "Parameter list cannot be null");
		Preconditions.checkState(params.size() == classResolvers.size());
		for(int i = 0; i < params.size(); i++){
			classResolvers.get(i).addCall(params.get(i));
		}
	}

	public List<Class<?>> resolve(boolean wrapPrimitives){
		List<Class<?>> res = new ArrayList<Class<?>>(classResolvers.size());
		for(ClassResolver resolver : classResolvers){
			res.add(resolver.resolve(wrapPrimitives));
		}
		return res;
	}
	
}
