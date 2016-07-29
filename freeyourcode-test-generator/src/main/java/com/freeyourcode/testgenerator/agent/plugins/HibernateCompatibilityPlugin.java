package com.freeyourcode.testgenerator.agent.plugins;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.cedarsoftware.util.io.JsonObjectFilter;
import com.cedarsoftware.util.io.JsonWriter;
import com.freeyourcode.prettyjson.JsonSerialisationUtils;
import com.freeyourcode.test.utils.InputPointerResolver;
import com.freeyourcode.test.utils.deepanalyser.DeepDiff;
import com.freeyourcode.test.utils.deepanalyser.DeepFinder;
import com.freeyourcode.testgenerator.core.CallOnMock;
import com.freeyourcode.testgenerator.core.ListenerManager;
import com.freeyourcode.testgenerator.core.ListenerManagerConfig;
import com.freeyourcode.testgenerator.core.MethodDescriptor;
import com.freeyourcode.testgenerator.core.factory.TestGeneratorListenerFactory;
import com.freeyourcode.testgenerator.core.listener.DefaultTestGeneratorListener;
import com.freeyourcode.testgenerator.core.listener.TestGeneratorListener;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.rits.cloning.Cloner;

/**
 * The Java Agent cannot load the Hibernate library to avoid incompatibilitiy problems. So we have to suppose to be able to load
 * this library from the targeted code.
 *
 */
public class HibernateCompatibilityPlugin implements Plugin {

	private final static String HIBERNATE_CLASS = "org/hibernate/Hibernate";

	// /TODO Refact
	private ClassLoader hibernateClassLoader;

	private TestGeneratorLogger logger;

	private final static Log log = LogFactory.getLog(HibernateCompatibilityPlugin.class);

	private Method isInitializedHibernateMethod;

	private Class<?> hibernateProxyClass;
	private Method getHibernateLazyInitializerMethod;
	private Class<?> lazyInitializerClass;
	private Method getImplementationMethod;

	private Class<?> persistentCollectionClass;
	private Class<?> persistentSetClass;

	private Method persistentCollectionGetStoredSnapshotMethod;

	private Cloner cloner;

	@Override
	public void start(Element config, ListenerManager manager) {
		adaptTestGeneratorListener(manager);

		// To avoid "duplicated class definition" errors caused by multi-threading, we cannot load hibernate class here.
		this.logger = manager.getConfig().getLogger();
		// TODO ne pas dependre de JSONWriter dans ce projet
		JsonWriter.setFilter(new JsonObjectFilter() {

			@Override
			public boolean isFiltered(Object o) {
				if (o == null) {
					return true;
				}
				return !isHibernateInitialized(o);
			}

			@Override
			public Object prepareToWrite(Object o) {
				return unproxy(o);
			}
		});
	}

	private void adaptTestGeneratorListener(ListenerManager manager) {
		manager.setFactory(new TestGeneratorListenerFactory() {
			@Override
			public TestGeneratorListener create(int testId, MethodDescriptor descriptor, ListenerManagerConfig config) {
				return new DefaultTestGeneratorListener(testId, descriptor, config) {
					@Override
					protected Object prepareObjectForCast(Object o) {
						return unproxy(o);
					}

					@Override
					public void onEventEnd(CallOnMock event) {
						// When hibernate is used with lazy mode, an object can contain proxies which are not initialized.
						// Another object loading can perform the proxy object init so the object will be initialized in
						// the first object too. So we have to clone the initial object to keep the initial object (broking
						// the Java reference but we keep a reference to the initial uninitialized proxies to get the real
						// implementations on stub method exit.
						eventMap.put(pendingEvent);
						pendingEvent = null;

						HibernateCallOnMock hEvent = (HibernateCallOnMock) event;
						if (!hEvent.isAlreadyCloned) {
							try {
								// TODO log management properly log.info("onEventEnd on " + event.getDescriptor());
								hEvent.freezeFindResponseInParams();
								hEvent.getParameters().setInputParams(getCloner().deepClone(event.getParameters().getInputParams()));
								hEvent.setResponse(getCloner().deepClone(event.getResponse()));
							} catch (Throwable t) {
								t.printStackTrace();
								log.error("onEventEnd cloning error on method " + event.getDescriptor() + " : " + t.getMessage());
							}
							// If several listeners listen the same event, we don't have to clone params everytime !
							hEvent.isAlreadyCloned = true;
						}
					}

					@Override
					protected void generateStubs(String mockedClassObject, MethodDescriptor eventMethod, List<CallOnMock> eventsOnThisMethod, String[] params) throws IOException {
						for (CallOnMock event : eventsOnThisMethod) {
							try {
								// FIXME le faire sur l'enter et autres ?!
								event.freezeResponse();
								event.freezeDiffsExit();
							} catch (Throwable e) {
								logger.onGenerationFail("The event params and response cannot be frozen on exit because " + e.getMessage(), new Exception(e));
							}
						}
						super.generateStubs(mockedClassObject, eventMethod, eventsOnThisMethod, params);
					}

				};
			}

			@Override
			public CallOnMock createAssociatedCallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
				return new HibernateCallOnMock(descriptor, parameters, returnedClass);
			}

		});
	}

	@Override
	public boolean handleClassDefinition(ClassLoader loader, String className) {
		if (HIBERNATE_CLASS.equals(className)) {
			this.hibernateClassLoader = loader;
		}
		return false;
	}

	private Cloner getCloner() {
		if (cloner == null) {
			cloner = createCloner();
		}
		return cloner;
	}

	private Cloner createCloner() {
		Cloner cloner = new Cloner() {
			@SuppressWarnings("unchecked")
			@Override
			protected <T> T cloneInternal(T o, Map<Object, Object> clones) throws IllegalAccessException {
				if (isHibernateInitialized(o)) {
					Object underlyingObject = unproxy(o);
					return (T) super.cloneInternal(underlyingObject, clones);
				} else {
					// Proxy not initialized, we keep the ref.
					return o;
				}
			}

		};
		cloner.registerImmutable(Date.class);
		return cloner;
	}

	@Override
	public void define(CtClass redefinedClass) {
	}

	public boolean isHibernateInitialized(Object o) {
		try {
			return (Boolean) getIsInitializedHibernateMethod().invoke(null, o);
		} catch (Exception e) {
			logger.onGenerationFail(e.getMessage(), e);
			return false;
		}
	}

	public ClassLoader getHibernateClassLoader() {
		return hibernateClassLoader == null ? ClassLoader.getSystemClassLoader() : hibernateClassLoader;
	}

	private Method getIsInitializedHibernateMethod() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		if (isInitializedHibernateMethod == null) {
			isInitializedHibernateMethod = getHibernateClassLoader().loadClass("org.hibernate.Hibernate").getMethod("isInitialized", Object.class);
		}
		return isInitializedHibernateMethod;
	}

	private Class<?> getHibernateProxyClass() {
		if (hibernateProxyClass == null) {
			try {
				hibernateProxyClass = getHibernateClassLoader().loadClass("org.hibernate.proxy.HibernateProxy");
				getHibernateLazyInitializerMethod = hibernateProxyClass.getMethod("getHibernateLazyInitializer");
				lazyInitializerClass = getHibernateClassLoader().loadClass("org.hibernate.proxy.LazyInitializer");
				getImplementationMethod = lazyInitializerClass.getMethod("getImplementation");
				persistentCollectionClass = getHibernateClassLoader().loadClass("org.hibernate.collection.PersistentCollection");
				persistentCollectionGetStoredSnapshotMethod = persistentCollectionClass.getMethod("getStoredSnapshot");
				persistentSetClass = getHibernateClassLoader().loadClass("org.hibernate.collection.PersistentSet");
			} catch (Exception e) {
				logger.onGenerationFail(e.toString(), e);
			}
		}
		return hibernateProxyClass;
	}

	public Object unproxy(Object entity) {
		if (entity != null) {
			try {
				if (getHibernateProxyClass().isInstance(entity)) {
					return getImplementationMethod.invoke(getHibernateLazyInitializerMethod.invoke(getHibernateProxyClass().cast(entity)));
				}// TODO refac la gestion du chargement - peut etre objet indépendant
				else if (persistentCollectionClass.isInstance(entity)) {
					Object snapshot = persistentCollectionGetStoredSnapshotMethod.invoke(persistentCollectionClass.cast(entity));
					if (persistentSetClass.isInstance(entity)) {
						return unproxyPersistentSet((Map<?, ?>) snapshot);
					}
					return snapshot;
				}
			} catch (Exception e) {
				logger.onGenerationFail(e.toString(), e);
			}
		}
		return entity;
	}

	private static <T> Set<T> unproxyPersistentSet(Map<T, ?> persistenceSet) {
		return new HashSet<T>(persistenceSet.keySet());
	}

	private class HibernateCallOnMock extends CallOnMock {

		private String pathToInputRef;
		private boolean isAlreadyCloned;

		public HibernateCallOnMock(MethodDescriptor descriptor, Object[] parameters, Class<?> returnedClass) {
			super(descriptor, parameters, returnedClass);
		}

		public void freezeFindResponseInParams() throws Exception {
			pathToInputRef = findResponseInParams();
		}

		@Override
		public void freezeResponse() throws Exception {
			if (shouldSerializedResponse()) {
				serializedResponse = JsonSerialisationUtils.writeObjectInJava(pathToInputRef != null ? new InputPointerResolver(pathToInputRef) : response);
			}
		}

		@Override
		protected DeepDiff createDeepDiff() {
			return new DeepDiff() {

				@Override
				public DeepDiff diff(Object expected, Object actual) throws Exception {
					// Expected is the serialized input object when code was entering in the method to get the initial object values.
					// To make a right comparison, we have compare to deserialized object to compare both objects without proxies
					// and WITH initialized fields by the constructors !
					actual = JsonSerialisationUtils.deserialize(JsonSerialisationUtils.serialize(actual));
					return super.diff(expected, actual);
				}

			};
		}

		@Override
		protected DeepFinder createDeepFinder() {
			return new DeepFinder() {
				@Override
				protected boolean shouldVisit(Object o) {
					// Don't visit all database !!
					return isHibernateInitialized(o) && super.shouldVisit(o);
				}
			};
		}

	}

}