package com.freeyourcode.testgenerator.agent.plugins;

import javassist.CtClass;
import javassist.CtMethod;

public class SpyPlugin extends PublicMethodListenerPlugin {

	public SpyPlugin() {
	}

	@Override
	void modifyMethod(CtClass redefinedClass, CtMethod method,
			CtClass exceptionClass) throws Exception {
    	method.insertBefore(SpyPlugin.class.getName()+".onSpy($class);");
	}

	public static void onSpy(Class<?> methodClass){
		singleton.getManager().onSpy(methodClass);
	}
	
}