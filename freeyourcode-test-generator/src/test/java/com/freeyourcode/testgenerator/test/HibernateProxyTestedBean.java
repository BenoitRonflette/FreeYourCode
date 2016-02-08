package com.freeyourcode.testgenerator.test;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class HibernateProxyTestedBean extends TestedBean implements HibernateProxy {

	private static final long serialVersionUID = 1L;
	
	private final LazyInitializer initializer;
	
	public HibernateProxyTestedBean(TestedBean realObject) {
		initializer = new LazyInitializer4Implemention(realObject);
	}

	@Override
	public Object writeReplace() {
		return null;
	}

	@Override
	public LazyInitializer getHibernateLazyInitializer() {
		return initializer;
	}

}