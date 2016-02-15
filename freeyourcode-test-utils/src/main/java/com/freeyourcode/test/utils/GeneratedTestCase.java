package com.freeyourcode.test.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.BeforeMethod;

import com.freeyourcode.test.utils.deepanalyser.DeepDiffReverter;
import com.freeyourcode.test.utils.matchers.BasicTypeMatcherEquals;
import com.freeyourcode.test.utils.matchers.MatcherEquals;
import com.google.common.base.Preconditions;

public class GeneratedTestCase extends PowerMockTestCase {

	/**
	 * Mockito answer updating the input parameters on exit and returning the expected response.
	 */
	protected Answer<Object> exitAnswer(final Object[] inputDiffsOnExit) {
		return exitAnswer(inputDiffsOnExit, (Object) null);
	}

	/**
	 * Mockito answer updating the input parameters on exit and returning the expected response.
	 */
	protected <T> Answer<T> exitAnswer(final Object[] inputDiffsOnExit, final T response) {
		return new Answer<T>() {
			@SuppressWarnings("unchecked")
			@Override
			public T answer(InvocationOnMock invocation) throws Throwable {
				if (inputDiffsOnExit != null) {
					for (int i = 0; i < inputDiffsOnExit.length; i++) {
						new DeepDiffReverter().revertDiffs(invocation.getArguments()[i], (Map<String, Object>) inputDiffsOnExit[i]);
					}
				}

				return (T) (response instanceof InputPointerResolver ? ((InputPointerResolver) response).resolve(invocation.getArguments()) : response);
			}
		};
	}

	protected <T> T createStub(Class<T> mockedClass) throws Exception {
		T mockedClassInstance = Mockito.mock(mockedClass);
		PowerMockito.whenNew(mockedClass).withAnyArguments().thenReturn(mockedClassInstance);
		return mockedClassInstance;
	}

	@SuppressWarnings("unchecked")
	protected <T> void useMockForNextNewInstances(Class<T> c, Object object) throws Exception {
		PowerMockito.whenNew(c).withAnyArguments().thenReturn((T) object);
	}

	protected void injectMocksInFields(List<ManualInjectionData> injections) throws Exception {
		for (int i = 0; i < injections.size(); i++) {
			ManualInjectionData injection = injections.get(i);
			for (Field f : injection.usedClass.getDeclaredFields()) {
				for (int j = 0; j < injections.size(); j++) {
					ManualInjectionData otherInjection = injections.get(j);
					if (j != i && f.getType().isAssignableFrom(otherInjection.usedClass)) {
						f.setAccessible(true);
						f.set(injection.data, otherInjection.data);
					}
				}
			}
		}
	}

	protected void assertEquals(Object expected, Object actual) {
		MatcherAssert.assertThat(actual, Matchers.is(createMatcherEquals(expected)));
	}

	protected MatcherEquals createMatcherEquals(Object expected) {
		return new BasicTypeMatcherEquals(expected);
	}

	protected void assertEquals(Object[] expected, Object[] actual) {
		Preconditions.checkNotNull(expected, "An expected value is required");
		Preconditions.checkNotNull(actual, "An actual value is required");
		Preconditions.checkArgument(actual.length == expected.length, "Expected and actual values have not got the same length");
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

	/**
	 * Inject the mocks for all next instances created with same classes as the @mock ones.
	 */
	@BeforeMethod
	protected void setUpMocksForAllInstances() throws Exception {
		// Always init mocks manually because we ignore if the right mockito test runner is used.
		MockitoAnnotations.initMocks(this);

		boolean fullInjection = fullMockInjection();

		List<ManualInjectionData> usedObjectsForTests = new ArrayList<ManualInjectionData>();
		for (Field f : getClass().getDeclaredFields()) {
			if (f.getAnnotation(Mock.class) != null || f.getAnnotation(Spy.class) != null) {
				f.setAccessible(true);
				Object o = f.get(this);
				useMockForNextNewInstances(f.getType(), o);
				// Current class is a mockito one, no the tested one.
				usedObjectsForTests.add(new ManualInjectionData(o, o.getClass().getSuperclass()));
			} else if (fullInjection && f.getAnnotation(InjectMocks.class) != null) {
				f.setAccessible(true);
				Object o = f.get(this);
				usedObjectsForTests.add(new ManualInjectionData(o, o.getClass()));
			}
		}

		if (fullInjection) {
			injectMocksInFields(usedObjectsForTests);
		}
	}

	/**
	 * By default, mock injection is the Mockito default one, i.e. only null fields
	 * are injected. However
	 * 
	 * @return
	 */
	protected boolean fullMockInjection() {
		return false;
	}

}