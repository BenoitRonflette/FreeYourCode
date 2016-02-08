package com.freeyourcode.testgenerator.logger;

import java.util.Properties;

public abstract class TestGeneratorLogger {
	
	protected final Properties props;
	
	public TestGeneratorLogger(Properties props) {
		this.props = props;
	}
	
	public void startKilling(){
	}
	
	public void onGenerationSuccess(String... codeLines){
	}
	
	public void onGenerationFail(String msg, Exception e){
	}
	
	public void onUsedClass(Class<?> cls, boolean onlyForImport){
	}
	
	public void onDeclaratedField(String field, String... annotations){
	}
	
	public void stopKilling(){
	}
	
	public Properties getProperties(){
		return props;
	}

}