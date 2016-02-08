package com.freeyourcode.testgenerator.agent.plugins;

import java.lang.reflect.Modifier;

import javassist.CtClass;
import javassist.CtMethod;

public class MockPlugin extends PublicMethodListenerPlugin {

	public MockPlugin() {
	}

	@Override
	void modifyMethod(CtClass redefinedClass, CtMethod method,
			CtClass exceptionClass) throws Exception {
    	//Whenever you are writing Java code for javassist to inject, keep in mind that you must use full qualified class names, 
    	//otherwise javassist's classpool wouldn't be able to find the classes resulting in a javassist.CannotCompileException.
    	method.insertBefore(MockPlugin.class.getName()+".onEventIn($class,\""+method.getName()+"\", "+CtClass.voidType.equals(method.getReturnType())+", $args, $sig, $type, "+Modifier.isStatic(method.getModifiers())+");");
    	//If the result type is void, then the type of $_ is Object and the value of $_ is null.
    	//$w represents a wrapper type
    	method.insertAfter(MockPlugin.class.getName()+".onEventOut(($w)$_);");
    	method.addCatch("{"+MockPlugin.class.getName()+".onEventException($e); throw $e;}", exceptionClass);	
	}
	
	public static void onEventIn(Class<?> methodClass, String methodName, boolean voidMethod, Object[] params, Class<?>[] paramClasses, Class<?> returnedClass, boolean isStatic){
		singleton.getManager().onEventIn(methodClass, methodName, voidMethod, params, paramClasses, returnedClass,isStatic);
	}
	
	public static void onEventOut(Object outputValue){
		singleton.getManager().onEventOut(outputValue);
	}
	
	public static void onEventException(Exception e){
		singleton.getManager().onEventException(e);
	}

}