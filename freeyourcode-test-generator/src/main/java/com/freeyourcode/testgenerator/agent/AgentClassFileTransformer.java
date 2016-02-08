package com.freeyourcode.testgenerator.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.runtime.Desc;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.freeyourcode.testgenerator.agent.constant.AgentConfigCommonAttr;
import com.freeyourcode.testgenerator.agent.constant.AgentConfigTags;
import com.freeyourcode.testgenerator.agent.plugins.Plugin;
import com.freeyourcode.testgenerator.core.ListenerManager;
import com.freeyourcode.testgenerator.core.ListenerManagerConfig;
import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.utils.XMLUtils;

/**
 * Plug the Legacy Code Test Generator to modify the listened public class methods. 
 * 
 * @author BRE
 *
 */
public class AgentClassFileTransformer implements ClassFileTransformer {
	
	private final List<Plugin> plugins = new ArrayList<Plugin>();
	
	private static final ClassPool classPool = ClassPool.getDefault();
	
	private final TestGeneratorLogger logger;
	
	
	public AgentClassFileTransformer(NodeList pluginsConfig, TestGeneratorLogger logger, Properties props) {
		this.logger = logger;
		
		ListenerManagerConfig config = new ListenerManagerConfig(props, logger);
		ListenerManager manager = new ListenerManager(config);
		
		//FIXME: a etudier Pr le $sig, on charge les classes depuis un context loader, sinon elles ne seront pas trouvées dans le classpath.
		Desc.useContextClassLoader = true;
		
		for(Element pluginConfig : XMLUtils.extractElementsFromNodeList(pluginsConfig, AgentConfigTags.PLUGIN, true)){
			try {
				String pluginClass = pluginConfig.getAttribute(AgentConfigCommonAttr.CLASS);
				Plugin plugin = (Plugin) Class.forName(pluginClass).newInstance();
				plugins.add(plugin);
				plugin.start(pluginConfig, manager);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
	
		byte[] returnedBuffer = null;
		for(Plugin plugin : plugins){
			if(plugin.handleClassDefinition(loader, className)){
				try{
					String normalizedClassName = className.replaceAll("/", ".");
					//If a program is running on a web application server such as JBoss and Tomcat, the ClassPool object may not be able to find user classes since such a web application server uses multiple class loaders as well as the system class loader.
					classPool.insertClassPath(new LoaderClassPath(loader));
					CtClass cc = classPool.get(normalizedClassName);
					plugin.define(cc);
		            returnedBuffer = cc.toBytecode();
		            //FIXME verif le detach
		            cc.detach();
				}
				catch(Exception e){
					logger.onGenerationFail(e.getMessage(), e);
				}
			}
		}
		
		return returnedBuffer;
	}

	public TestGeneratorLogger getLogger() {
		return logger;
	}
	
	
	
}