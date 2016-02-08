package com.freeyourcode.testgenerator.test.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import com.freeyourcode.testgenerator.agent.AgentClassFileTransformer;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;

/**
 * Permet de simuler l'appel au transform du ClassFileTransformer apr�s que celui-ci ait �t� ajout� dans l'instrumentation.
 * 
 * @author BRE
 *
 */
public class InstrumentationMock implements Instrumentation {
	
	private ClassLoader loader;
	
	private ClassFileTransformer transformer;
	
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	
	@Override
	public void addTransformer(final ClassFileTransformer transformer) {
		this.transformer = transformer;
		loader = new ClassLoader() {
		    @Override
		    public Class<?> loadClass(String name) throws ClassNotFoundException {
		    	Class<?> cls = classes.get(name);
		    	if(cls != null){
		    		return cls;
		    	}
		    	
		            byte[] byteBuffer;
					try {
						//Le transform doit recevoir les noms des classes et leur path avec des '/' et non le '.' du package.
						String classNameTransform = name.replace(".","/");
						byteBuffer = transformer.transform(this, classNameTransform, Class.forName(name), null, null);
					} catch (IllegalClassFormatException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					cls =  byteBuffer != null ? defineClass(name, byteBuffer, 0, byteBuffer.length) : super.loadClass(name);
					classes.put(name, cls);
					return cls;
		        }
		   };

	}
	
	public TestGeneratorLogger getLogger(){
		return ((AgentClassFileTransformer)transformer).getLogger();
	}
	
	public ClassLoader getLoader(){
		return loader;
	}

	@Override
	public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {
	}

	@Override
	public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {
	}

	@Override
	public void appendToSystemClassLoaderSearch(JarFile jarfile) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAllLoadedClasses() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getInitiatedClasses(ClassLoader loader) {
		return null;
	}

	@Override
	public long getObjectSize(Object objectToSize) {
		return 0;
	}

	@Override
	public boolean isModifiableClass(Class<?> theClass) {
		return false;
	}

	@Override
	public boolean isNativeMethodPrefixSupported() {
		return false;
	}

	@Override
	public boolean isRedefineClassesSupported() {
		return false;
	}

	@Override
	public boolean isRetransformClassesSupported() {
		return false;
	}

	@Override
	public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {
	}

	@Override
	public boolean removeTransformer(ClassFileTransformer transformer) {

		return false;
	}

	@Override
	public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {


	}

	@Override
	public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {


	}

}
