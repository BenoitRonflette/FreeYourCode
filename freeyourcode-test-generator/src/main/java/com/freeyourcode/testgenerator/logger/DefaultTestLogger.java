package com.freeyourcode.testgenerator.logger;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Joiner;


public class DefaultTestLogger extends TestGeneratorLogger {
	
	private final static Log log = LogFactory.getLog(DefaultTestLogger.class);
	
	public DefaultTestLogger(Properties props) {
		super(props);
	}

	@Override
	public void startKilling() {
		log.info("Begin the legacy code generation with properties "+props);
	}

	@Override
	public void onGenerationSuccess(String... codeLines) {
		log.info(Joiner.on('\n').join(codeLines));
	}

	@Override
	public void onGenerationFail(String msg, Exception e) {
		log.error(msg, e);
	}

	@Override
	public void stopKilling() {
		log.error("Legacy code generation is finished");
	}

}
