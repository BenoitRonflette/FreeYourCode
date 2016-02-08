package com.freeyourcode.testgenerator.agent.plugins;

import javassist.CtClass;

import org.w3c.dom.Element;

import com.freeyourcode.testgenerator.core.ListenerManager;

public interface Plugin {
	
	void start(Element config, ListenerManager manager);
	
	boolean handleClassDefinition(ClassLoader loader, String className);
	
	void define(CtClass redefinedClass) throws Exception;

}
