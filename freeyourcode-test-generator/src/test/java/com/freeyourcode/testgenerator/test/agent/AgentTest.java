package com.freeyourcode.testgenerator.test.agent;

import java.lang.reflect.Method;

import junit.framework.Assert;

import org.mockito.internal.util.Primitives;

import com.freeyourcode.testgenerator.agent.Agent;

public abstract class AgentTest {

	private InstrumentationMock instrumentation;
	
	private int testId = 0;

	protected int nextTestId(){
		return testId++;
	}
	
	protected void invokeMethod(Class<?> usedClass, String methodName, Object...params) throws Exception{
		Class<?> subjectClass = loadClass(usedClass);

		if(params == null){
			params = new Object[]{};
		}
		Method targetedMethod = null;
		for(Method method : subjectClass.getMethods()){
			if(method.getName().equals(methodName) && (method.getParameterTypes().length == params.length)){
				boolean paramCanMatch = true;
				for(int i = 0; i < method.getParameterTypes().length && paramCanMatch;i++){
					Class<?> paramClass = method.getParameterTypes()[i];
					if(params[i] != null && !paramClass.isAssignableFrom(params[i].getClass()) && !(paramClass.isPrimitive() && paramClass.equals(Primitives.primitiveTypeOf(params[i].getClass())))){
						paramCanMatch = false;
					}
				}
				if(!paramCanMatch){
					continue;
				}
				targetedMethod = method;
				break;
			}
		}
		
		if(targetedMethod == null){
			throw new Exception("Aucune méthode "+methodName+" ne matche pour les paramètres données");
		}
		//On n'utile pas directement subjectClass.getMethod(methodName, paramClass) car la m�thode test�e peut avoir des types primitifs en param�tre.
		targetedMethod.invoke(createNewInstanceOfTestedClass(subjectClass), params);
	}
	
	protected <T> T createNewInstanceOfTestedClass(Class<T> cls) throws Exception{
		return cls.getConstructor().newInstance();
	}
	
	protected void assertTestIs(String expectedTest) throws Exception{
		Assert.assertEquals(expectedTest, ((MockTestLogger)instrumentation.getLogger()).getSb().toString());
	}
	
	protected  void resetTestChecker(){
		((MockTestLogger)instrumentation.getLogger()).resetSb();
	}
	
	protected  void lancementAgent(String args){
		instrumentation = new InstrumentationMock();
		Agent.premain(args, instrumentation);
	}
	
	protected Class<?> loadClass(Class<?> usedClass) throws Exception{
			if(instrumentation == null || instrumentation.getLoader() == null){
				throw new Exception("Dans le cadre de ce mock, l'agent doit avoir été lancé avant le chargement des classes");
			}
			
			return instrumentation.getLoader().loadClass(usedClass.getName());
	}

}