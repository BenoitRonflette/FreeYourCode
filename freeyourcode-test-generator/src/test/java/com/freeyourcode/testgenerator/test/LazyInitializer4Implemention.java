package com.freeyourcode.testgenerator.test;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.proxy.LazyInitializer;

public class LazyInitializer4Implemention implements LazyInitializer {
	
	private final Object realObject;
	
	LazyInitializer4Implemention(Object realObject){
		this.realObject = realObject;
	}

	@Override
	public void unsetSession() {
	}
	
	@Override
	public void setUnwrap(boolean unwrap) {
	}
	
	@Override
	public void setSession(SessionImplementor session)
			throws HibernateException {
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
	}
	
	@Override
	public void setImplementation(Object target) {
	}
	
	@Override
	public void setIdentifier(Serializable id) {
	}
	
	@Override
	public boolean isUnwrap() {
		return false;
	}
	
	@Override
	public boolean isUninitialized() {
		return false;
	}
	
	@Override
	public boolean isReadOnlySettingAvailable() {

		return false;
	}
	
	@Override
	public boolean isReadOnly() {
		return false;
	}
	
	@Override
	public void initialize() throws HibernateException {
	}
	
	@Override
	public SessionImplementor getSession() {
		return null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class getPersistentClass() {
		return null;
	}
	
	@Override
	public Object getImplementation(SessionImplementor session)
			throws HibernateException {
		return realObject;
	}
	
	@Override
	public Object getImplementation() {
		return realObject;
	}
	
	@Override
	public Serializable getIdentifier() {
		return null;
	}
	
	@Override
	public String getEntityName() {
		return null;
	}

}
