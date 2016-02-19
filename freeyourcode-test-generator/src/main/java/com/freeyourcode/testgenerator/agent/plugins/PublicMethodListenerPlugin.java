package com.freeyourcode.testgenerator.agent.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.freeyourcode.testgenerator.agent.constant.AgentConfigTags;
import com.freeyourcode.testgenerator.core.ListenerManager;
import com.freeyourcode.testgenerator.utils.XMLUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

public abstract class PublicMethodListenerPlugin implements Plugin {

	// Singleton allows only one instance by plugin in order to be able to call the plugin from static method (using javassist).
	protected static PublicMethodListenerPlugin singleton;

	private final List<IncludedPattern> includedPatterns = new ArrayList<IncludedPattern>();
	private final List<String> excludedClassPatterns = new ArrayList<String>();
	private final List<String> excludedMethodPatterns = new ArrayList<String>();

	private ListenerManager manager;

	public PublicMethodListenerPlugin() {
		// FIXME si exception, avec singleton, cela pose des probs de test
		// if(singleton != null){
		// throw new RuntimeException(this.getClass().getSimpleName()+" plugin is already defined");
		// }
		singleton = this;
	}

	public ListenerManager getManager() {
		return manager;
	}

	private String safeGetPattern(Element e) {
		String value = Preconditions.checkNotNull(e.getFirstChild().getNodeValue());
		return value.trim();// Trim() is useful when we include a method, there are spaces between the class name and the first method tag.
	}

	@Override
	public void start(Element config, ListenerManager manager) {
		this.manager = manager;

		NodeList includesConfig = XMLUtils.extractNodeListFromElement(config, AgentConfigTags.INCLUDES, true);
		for (Element e : XMLUtils.extractElementsFromNodeList(includesConfig, AgentConfigTags.CLASS, true)) {
			IncludedPattern includedPattern = new IncludedPattern(safeGetPattern(e));
			for (Element methodElement : XMLUtils.extractElementsFromNodeList(e.getChildNodes(), AgentConfigTags.METHOD, false)) {
				includedPattern.addMethod(safeGetPattern(methodElement));
			}
			includedPatterns.add(includedPattern);
		}

		NodeList excludesConfig = XMLUtils.extractNodeListFromElement(config, AgentConfigTags.EXCLUDES, false);
		if (excludesConfig != null) {
			for (Element e : XMLUtils.extractElementsFromNodeList(excludesConfig, AgentConfigTags.CLASS, false)) {
				excludedClassPatterns.add(safeGetPattern(e));
			}
			for (Element e : XMLUtils.extractElementsFromNodeList(excludesConfig, AgentConfigTags.METHOD, false)) {
				excludedMethodPatterns.add(safeGetPattern(e));
			}
		}
	}

	@Override
	public final boolean handleClassDefinition(ClassLoader loader, String className) {
		String cleanedClass = className.replace("/", ".");
		for (IncludedPattern include : includedPatterns) {
			if (cleanedClass.matches(include.cls) && !isExcludeClass(cleanedClass)) {
				return true;
			}
		}
		return false;
	}

	private boolean isExcludeClass(String className) {
		for (String exclude : excludedClassPatterns) {
			if (className.matches(exclude)) {
				return true;
			}
		}
		return false;
	}

	private boolean isExcludeMethod(String classMethodName) {
		for (String exclude : excludedMethodPatterns) {
			if (classMethodName.matches(exclude)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * We control again the inclusions to manage the complex ones (with classes + methods). We don't have to include
	 * all class methods.
	 */
	private boolean isInclude(String className, String methodName) {
		for (IncludedPattern pattern : includedPatterns) {
			if (pattern.matches(className, methodName)) {
				return true;
			}
		}
		return false;
	}

	private Set<CtMethod> getAllValidMethods(CtMethod[]... methodGroups) {
		Set<CtMethod> methods = Sets.newHashSet();
		for (CtMethod[] methodGroup : methodGroups) {
			for (CtMethod method : methodGroup) {
				if (!Modifier.isVolatile(method.getModifiers())) {
					methods.add(method);
				}
			}
		}
		return methods;
	}

	@Override
	public final void define(CtClass redefinedClass) throws Exception {
		CtClass exceptionClass = ClassPool.getDefault().get("java.lang.Exception");

		// There is a Javassist bug when we use redefinedClass.getMethods() with generic class types, when the returned type is a parameterized type and with no parameters,
		// it is not provided by redefinedClass.getMethods() but only by redefinedClass.getDeclaredMethods(). Union is all methods !
		// (method hashcode is the same between the version with parameterized type and with the defined type...).
		// But the method provided by the superclass without no defined type is considered as volatile, so if we keep non volatile methods only,
		// getting the union of declared methods and super class methods then put them into a Set, we get all valid methods.
		// (see test com.freeyourcode.testgenerator.test.agent.cases.AgentTestClassCallingGenericClassImpl.testCompute() about).
		for (CtMethod method : getAllValidMethods(redefinedClass.getMethods(), redefinedClass.getDeclaredMethods())) {
			if (shouldInstrumentMethod(redefinedClass, method)) {
				boolean modifyMethod = true;
				// If method is defined in a superclass, we have to override it (we don't want to listen all subclasses
				// which share the same superclass !).
				if (shouldOverrideMethod(redefinedClass, method)) {
					// All methods cannot be overrided !
					if (canOverrideMethod(method)
					// We can exclude a method by redefined class in configuration even if the method is not overridden
							&& !isExcludeMethod(method.getLongName().replace(method.getDeclaringClass().getName(), redefinedClass.getName()))) {
						method = override(redefinedClass, method);
					} else {
						modifyMethod = false;
					}
				}

				if (modifyMethod) {
					modifyMethod(redefinedClass, method, exceptionClass);
				}
			}
		}
	}

	public CtMethod override(CtClass redefinedClass, CtMethod method) throws CannotCompileException {
		method = CtNewMethod.delegator(method, redefinedClass);
		redefinedClass.addMethod(method);
		return method;
	}

	private boolean shouldOverrideMethod(CtClass redefinedClass, CtMethod method) {
		return !redefinedClass.equals(method.getDeclaringClass());
	}

	private boolean canOverrideMethod(CtMethod method) {
		int modifiers = method.getModifiers();
		return !Modifier.isFinal(modifiers) && !Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers);
	}

	private boolean shouldInstrumentMethod(CtClass redefinedClass, CtMethod method) {
		// Method declaring class could be different that the redefined class (a super class for instance), so
		// we replace the declaring class name by "" to get the method name with parameters.
		String nameWithParameterTypes = method.getLongName().replace(method.getDeclaringClass().getName() + ".", "");
		int modifiers = method.getModifiers();
		return Modifier.isPublic(modifiers) && !Modifier.isNative(modifiers)//
				&& isInclude(redefinedClass.getName(), nameWithParameterTypes)//
				// isExcludeMethod(method.getLongName()) excludes the method if the exclusion carries on the declaring class (could
				// be different that the redefined class if it's a superclass)
				&& !isExcludeMethod(method.getLongName());
	}

	abstract void modifyMethod(CtClass redefinedClass, CtMethod method, CtClass exceptionClass) throws Exception;

	private static class IncludedPattern {
		private final String cls;
		private final List<String> methods = new ArrayList<String>();

		public IncludedPattern(String cls) {
			this.cls = cls;
		}

		public void addMethod(String method) {
			methods.add(method);
		}

		public boolean matches(String className, String methodName) {
			boolean matches = className.matches(cls);
			if (matches && methods.size() > 0) {
				// If this inclusion has got restrictions on methods, we match only if one method matches at least.
				for (String method : methods) {
					if (methodName.matches(method)) {
						return true;
					}
				}
				matches = false;
			}
			return matches;
		}

	}

}