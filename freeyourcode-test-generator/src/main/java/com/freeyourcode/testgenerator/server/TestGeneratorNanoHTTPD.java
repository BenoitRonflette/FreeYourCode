package com.freeyourcode.testgenerator.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.freeyourcode.testgenerator.logger.TestGeneratorLogger;
import com.freeyourcode.testgenerator.server.NanoHTTPD.Response.Status;
import com.freeyourcode.testgenerator.utils.PropertiesUtils;
import com.freeyourcode.testgenerator.utils.TestGeneratorProperties;

public class TestGeneratorNanoHTTPD extends NanoHTTPD {
	
	private final TestGeneratorLogger logger;
	private boolean isKilling;
	private final boolean autoStart;
	private final String webTemplate;
	private final Map<String, TestGeneratorNanoUri> uris = new HashMap<String, TestGeneratorNanoUri>();

	//Pages
	private static String START = "start";
	private static String STOP = "stop";
	
	//Paths
	private static final String ROOT = "/";
	private static final String LIB = "lib";
	private static final String CSS = "css";
	private static final String DATA = "data";
	
	private static final String TEMPLATE = "serverTemplate.html";
	

	public TestGeneratorNanoHTTPD(TestGeneratorLogger logger, Properties props) {
		super(PropertiesUtils.getInt(props, TestGeneratorProperties.PORT, true));
		this.logger = logger;
		autoStart = PropertiesUtils.getBoolean(props, TestGeneratorProperties.AUTO_START, false);
		
		webTemplate = loadTextResource(TEMPLATE);
		
		//There is no guarantee that shutdownhook is called however, this hook could be useful if the
		//user forget to call "stop killing" using the server interface and this is supported by its
		//environment.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TestGeneratorNanoHTTPD.this.stop();
			}
		});
		
		registerUris();
	}
	
	public  Path getPath(String name){
		try{
			URI uri = getClass().getResource(ROOT+name).toURI();
		    try
		    {
		        // first try getting a path via existing file systems
		        return  Paths.get(uri);//If file is available via existing file systems.
		    }
		    catch (final FileSystemNotFoundException e)
		    {
		         //not directly on file system, so then it's somewhere else (e.g.: JAR)
		        final Map<String, ?> env = Collections.emptyMap();
		        return FileSystems.newFileSystem(uri, env).provider().getPath(uri);
		    }
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	

	
	private InputStream loadResource(String name){
			try {
				return Files.newInputStream(getPath(name));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
	}
	
	private String loadTextResource(String name){
		try {
			byte[] encoded = Files.readAllBytes(getPath(name));
			return new String(encoded, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void startLogger(){
		if(!isKilling){
			isKilling = true;
			logger.startKilling();
		}
	}
	
	private void stopLogger(){
		if(isKilling){
			isKilling = false;
			logger.stopKilling();
		}
	}
	
	@Override
	public void start() throws IOException {
		if(autoStart){
			startLogger();
		}
		super.start();
	}
	
	@Override
	public void stop() {
		stopLogger();
		super.stop();
	}
	
	@Override public Response serve(IHTTPSession session) {
		String sessionUri = session.getUri().replaceFirst(ROOT, "");
		TestGeneratorNanoUri uri = uris.get(sessionUri);
		if(uri != null){
			return new Response(uri.execute(session));
		}
		else if(sessionUri.startsWith(LIB) || sessionUri.startsWith(CSS)|| sessionUri.startsWith(DATA)){
			try{
				return new Response(Status.OK, getTypeMime(session.getUri()), loadResource(sessionUri));
			}
			catch(Exception e){
				return new Response(Status.INTERNAL_ERROR, (String)null, e.getMessage());
			}
		}
    
        return new Response(Status.NOT_FOUND, (String)null, session.getUri()+" not found");
    }
	
	private String getTypeMime(String uri) throws Exception{
		SupportedExtension extension = SupportedExtension.fromExtension(getExtension(uri));
		if(extension != null){
			return extension.getTypeMime();
		}
		throw new Exception("No type MIME for uri "+uri);
	}
	
	private String getExtension(String uri){
		String extension = "";
		int i = uri.lastIndexOf('.');
		if (i > 0) {
		    extension = uri.substring(i+1);
		}
		return extension.toLowerCase();
	}
	
	private String generatePage(){
		  StringBuilder sb = new StringBuilder();
	        
	        sb.append("<form action=\""+(isKilling ? "stop" : "start")+"\" method=\"POST\">");
	        
	        //TODO improvement: sort the properties by plugin.
	        if(isKilling){
		        for(Entry<Object, Object> prop  : logger.getProperties().entrySet()){
		        	sb.append("<div class=\"form-group_\">");
		        	sb.append("<label for=\""+prop.getKey()+"\">"+prop.getKey()+"</label>");
		        	sb.append("<input type=\"text\" class=\"form-control\" id=\""+prop.getKey()+"\"  name=\""+prop.getKey()+"\"  value=\""+prop.getValue()+"\">");
		        	sb.append("</div><br/>");
		        }
	        }

	        sb.append("<center><input type=\"submit\" class=\"btn btn-primary\" value=\""+(isKilling ? "Stop":"Start")+" killing\"></center>");
	        sb.append("</form>");

	        return webTemplate.replace("<!--here apply template-->", sb.toString());
	}
    
    private void registerUris(){
    	uris.put(START, new TestGeneratorNanoUri() {
			
			@Override
			String execute(IHTTPSession session) {
				startLogger();	
				return  generatePage();
			}
		});
    	uris.put(STOP, new TestGeneratorNanoUri() {
			
			@SuppressWarnings("unchecked")
			@Override
			String execute(IHTTPSession session) {
	            try {
	            	//We have to parse body before getting getQueryParameterString()
					session.parseBody(Collections.EMPTY_MAP);
				} catch (IOException | ResponseException e) {
					logger.onGenerationFail(e.getMessage(), e);
					e.printStackTrace();
				}

				//Logger properties are updated
				Properties loggerProperties = logger.getProperties();
				for(Entry<String, List<String>> param : decodeParameters(session.getQueryParameterString()).entrySet()){
					if(loggerProperties.containsKey(param.getKey()) && param.getValue().size() > 0){
						loggerProperties.setProperty(param.getKey(), param.getValue().get(0));
					}
				}
		        stopLogger(); 
		        return  generatePage();
			}
		});
    	
    	uris.put("serverTemplate.html", loadMainPage());
    	uris.put("", loadMainPage());
    	uris.put("index.html", loadMainPage());
   
    	mapHtmlPage("gettingStarted.html");
    	mapHtmlPage("docs.html");
    	mapHtmlPage("download.html");
    	mapHtmlPage("about.html");
    }
    
    private abstract class TestGeneratorNanoUri{
    	
		abstract String execute(IHTTPSession session);
    	
    }
    
	private TestGeneratorNanoUri loadMainPage() {
		return new TestGeneratorNanoUri() {
			@Override
			String execute(IHTTPSession session) {
				return generatePage();
			}
		};
	}
	
	private void mapHtmlPage(final String name){
    	uris.put(name, new TestGeneratorNanoUri() {
			@Override
			String execute(IHTTPSession session) {
				return  loadTextResource(name);
			}
		});
	}

}