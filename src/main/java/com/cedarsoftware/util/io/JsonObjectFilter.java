package com.cedarsoftware.util.io;

public interface JsonObjectFilter {
	
	public boolean isFiltered(Object o);
	
	public Object prepareToWrite(Object o);

}
