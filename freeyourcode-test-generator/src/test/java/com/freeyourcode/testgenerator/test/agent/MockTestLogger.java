package com.freeyourcode.testgenerator.test.agent;

import java.util.Properties;

import com.freeyourcode.testgenerator.logger.FileTestLogger;
import com.google.common.base.Joiner;

public class MockTestLogger extends FileTestLogger {

	private StringBuilder sb;
	
	public MockTestLogger(Properties props) {
		super(props);
	}
	
	@Override
	public void startKilling() {
		sb = new StringBuilder();
		super.startKilling();
	}
	
	@Override
	public void stopKilling() {
		sb = null;
		super.stopKilling();
	}

	@Override
	public void onGenerationSuccess(String... codeLines) {
		if(sb != null){
			for(String line : codeLines){
				sb.append(line);
			}
			System.out.println(Joiner.on('\n').join(codeLines));
			super.onGenerationSuccess(codeLines);
		}
	}

	@Override
	public void onGenerationFail(String msg, Exception e) {
			System.out.println(msg);
			e.printStackTrace();
			super.onGenerationFail(msg, e);
	}

	public StringBuilder getSb() throws Exception {
		if(sb != null){
			return sb;
		}
		throw new Exception("Logger has not been started.");
	}
	
	public void resetSb(){
		if(sb != null){
			sb.setLength(0);
		}
	}

}