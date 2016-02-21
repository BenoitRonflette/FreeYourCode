package com.freeyourcode.testgenerator.agent.plugins;

import java.lang.reflect.Modifier;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;

public class MockPlugin extends PublicMethodListenerPlugin {

	public MockPlugin() {
	}

	@Override
	void modifyMethod(CtClass redefinedClass, CtMethod method, CtClass exceptionClass) throws Exception {
		// Whenever you are writing Java code for javassist to inject, keep in mind that you must use full qualified class names,
		// otherwise javassist's classpool wouldn't be able to find the classes resulting in a javassist.CannotCompileException.
		method.insertBefore(MockPlugin.class.getName() + ".onEventIn($class,\"" + method.getName() + "\", " + CtClass.voidType.equals(method.getReturnType()) + ", $args, $sig, $type, " + Modifier.isStatic(method.getModifiers()) + ");");
		// If the result type is void, then the type of $_ is Object and the value of $_ is null.
		// $w represents a wrapper type
		method.insertAfter(MockPlugin.class.getName() + ".onEventOut(($w)$_);");
		method.addCatch("{" + MockPlugin.class.getName() + ".onEventException($e); throw $e;}", exceptionClass);
	}

	@Override
	public void define(CtClass redefinedClass) throws Exception {
		super.define(redefinedClass);
		mock(redefinedClass);

	}

	public final void mock(CtClass redefinedClass) throws Exception {
		for (CtBehavior method : redefinedClass.getConstructors()) {
			if (shouldInstrumentMethod(redefinedClass, method)) {
				// When a mocked class is listened, a testNG mock will be created if a call is performed on this mock, however,
				// if the code creates a useless object (no call on this object) but this object requires initialized parameters
				// that could create init or NPE problems. So, even if there will be no call on this object, when he is created,
				// a mock is created too in the generated tested class !
				// The generated test framework will ensure that all instances created on this class
				// will make a reference to the mocked instance!
				method.insertBefore(MockPlugin.class.getName() + ".onEventCreation($class);");
			}
		}
	}

	public static void onEventIn(Class<?> methodClass, String methodName, boolean voidMethod, Object[] params, Class<?>[] paramClasses, Class<?> returnedClass, boolean isStatic) {
		singleton.getManager().onEventIn(methodClass, methodName, voidMethod, params, paramClasses, returnedClass, isStatic);
	}

	public static void onEventOut(Object outputValue) {
		singleton.getManager().onEventOut(outputValue);
	}

	public static void onEventException(Exception e) {
		singleton.getManager().onEventException(e);
	}

	public static void onEventCreation(Class<?> methodClass) {
		singleton.getManager().onEventCreation(methodClass);
	}
}