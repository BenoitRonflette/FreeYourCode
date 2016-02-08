package com.freeyourcode.testgenerator.agent.plugins;

import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class TestGeneratorPlugin extends PublicMethodListenerPlugin {
	
	public TestGeneratorPlugin() {
		//TODO pouvoir  ecouter plusieurs classes
	}

	@Override
	void modifyMethod(CtClass redefinedClass, CtMethod method,
			CtClass exceptionClass) throws CannotCompileException, NotFoundException {
		Modifier.isStatic(method.getModifiers());
	  	//Whenever you are writing Java code for javassist to inject, keep in mind that you must use full qualified class names, 
    	//otherwise javassist's classpool wouldn't be able to find the classes resulting in a javassist.CannotCompileException.
    	method.insertBefore(TestGeneratorPlugin.class.getName()+".onMethodInput($class,\""+method.getName()+"\", "+CtClass.voidType.equals(method.getReturnType())+", $args, $sig, "+Modifier.isStatic(method.getModifiers())+");");
    	//If the result type is void, then the type of $_ is Object and the value of $_ is null.
    	//$w represents a wrapper type
    	method.insertAfter(TestGeneratorPlugin.class.getName()+".onMethodOutput(\""+method.getName()+"\", ($w)$_);");
    	method.addCatch("{"+TestGeneratorPlugin.class.getName()+".onException(\""+method.getName()+"\", $e); throw $e;}", exceptionClass);
	}
	
	public static void onMethodInput(Class<?> methodClass, String methodName, boolean isVoidMethod, Object[] params, Class<?>[] paramClasses, boolean isStatic){
		singleton.getManager().onMethodInput(methodClass, methodName, isVoidMethod, params,paramClasses, isStatic);
	}
	
	public static void onMethodOutput(String methodName, Object returnedValue){
		singleton.getManager().onMethodOutput(methodName, returnedValue);
	}
	
	public static void onException(String methodName, Exception e){
		singleton.getManager().onException(methodName, e);
	}
	

}
