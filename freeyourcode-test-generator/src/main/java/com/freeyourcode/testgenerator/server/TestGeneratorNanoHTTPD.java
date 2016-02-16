package com.freeyourcode.testgenerator.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.freeyourcode.testgenerator.server.NanoHTTPD.Response.Status;
import com.freeyourcode.testgenerator.utils.PropertiesUtils;
import com.freeyourcode.testgenerator.utils.TestGeneratorProperties;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TestGeneratorNanoHTTPD extends NanoHTTPD {

	private final List<ServerStateListener> serverListeners = Lists.newArrayList();
	private final Map<String, Properties> updatableProperties = Maps.newHashMap();

	private final static Log log = LogFactory.getLog(TestGeneratorNanoHTTPD.class);

	private boolean isKilling;
	private final boolean autoStart;
	private final String webTemplate;
	private final Map<String, TestGeneratorNanoUri> uris = new HashMap<String, TestGeneratorNanoUri>();

	// Pages
	private static String START = "start";
	private static String STOP = "stop";

	// Paths
	private static final String ROOT = "/";
	private static final String LIB = "lib";
	private static final String CSS = "css";
	private static final String DATA = "data";

	private static final String TEMPLATE = "serverTemplate.html";

	public TestGeneratorNanoHTTPD(Properties props) {
		super(PropertiesUtils.getInt(props, TestGeneratorProperties.PORT, true));
		autoStart = PropertiesUtils.getBoolean(props, TestGeneratorProperties.AUTO_START, false);

		webTemplate = loadTextResource(TEMPLATE);

		// There is no guarantee that shutdownhook is called however, this hook could be useful if the
		// user forget to call "stop killing" using the server interface and this is supported by its
		// environment.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				TestGeneratorNanoHTTPD.this.stop();
			}
		});

		registerUris();
	}

	public void registerUpdatableProperties(String section, Properties props) {
		Preconditions.checkNotNull(section, "Property section is required");
		Preconditions.checkArgument(!updatableProperties.containsKey(section), "Property section " + section + " already exists.");
		updatableProperties.put(section, props);
	}

	public void registerListener(ServerStateListener listener) {
		serverListeners.add(listener);
	}

	public void unregisterListener(ServerStateListener listener) {
		serverListeners.remove(listener);
	}

	public Path getPath(String name) {
		try {
			URI uri = getClass().getResource(ROOT + name).toURI();
			try {
				// first try getting a path via existing file systems
				return Paths.get(uri);// If file is available via existing file systems.
			} catch (final FileSystemNotFoundException e) {
				// not directly on file system, so then it's somewhere else (e.g.: JAR)
				final Map<String, ?> env = Collections.emptyMap();
				return FileSystems.newFileSystem(uri, env).provider().getPath(uri);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private InputStream loadResource(String name) {
		try {
			return Files.newInputStream(getPath(name));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String loadTextResource(String name) {
		try {
			byte[] encoded = Files.readAllBytes(getPath(name));
			return new String(encoded, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void startKilling() {
		if (!isKilling) {
			isKilling = true;
			for (ServerStateListener listener : serverListeners) {
				listener.startKilling();
			}
		}
	}

	private void stopKilling() {
		if (isKilling) {
			isKilling = false;
			for (ServerStateListener listener : serverListeners) {
				listener.stopKilling();
			}
		}
	}

	@Override
	public void start() throws IOException {
		if (autoStart) {
			startKilling();
		}
		super.start();
	}

	@Override
	public void stop() {
		stopKilling();
		super.stop();
	}

	@Override
	public Response serve(IHTTPSession session) {
		String sessionUri = session.getUri().replaceFirst(ROOT, "");
		TestGeneratorNanoUri uri = uris.get(sessionUri);
		if (uri != null) {
			return new Response(uri.execute(session));
		} else if (sessionUri.startsWith(LIB) || sessionUri.startsWith(CSS) || sessionUri.startsWith(DATA)) {
			try {
				return new Response(Status.OK, getTypeMime(session.getUri()), loadResource(sessionUri));
			} catch (Exception e) {
				return new Response(Status.INTERNAL_ERROR, (String) null, e.getMessage());
			}
		}

		return new Response(Status.NOT_FOUND, (String) null, session.getUri() + " not found");
	}

	private String getTypeMime(String uri) throws Exception {
		SupportedExtension extension = SupportedExtension.fromExtension(getExtension(uri));
		if (extension != null) {
			return extension.getTypeMime();
		}
		throw new Exception("No type MIME for uri " + uri);
	}

	private String getExtension(String uri) {
		String extension = "";
		int i = uri.lastIndexOf('.');
		if (i > 0) {
			extension = uri.substring(i + 1);
		}
		return extension.toLowerCase();
	}

	private String generatePage() {
		StringBuilder sb = new StringBuilder();

		sb.append("<form action=\"" + (isKilling ? "stop" : "start") + "\" method=\"POST\">");

		if (isKilling) {
			for (Entry<String, Properties> updatablePropertiesEntry : updatableProperties.entrySet()) {
				sb.append("<fieldset>");
				sb.append("<legend>" + updatablePropertiesEntry.getKey() + "</legend>");
				for (Entry<Object, Object> prop : updatablePropertiesEntry.getValue().entrySet()) {
					String key = updatablePropertiesEntry.getKey() + "_" + prop.getKey();
					sb.append("<div class=\"form-group\">");
					sb.append("<label for=\"" + key + "\">" + prop.getKey() + "</label>");
					sb.append("<input type=\"text\" class=\"form-control\" id=\"" + key + "\"  name=\"" + key + "\"  value=\"" + prop.getValue() + "\">");
					sb.append("</div><br/>");
				}
				sb.append("</fieldset>");
			}
		}

		sb.append("<center><input type=\"submit\" class=\"btn btn-primary\" value=\"" + (isKilling ? "Stop" : "Start") + " killing\"></center>");
		sb.append("</form>");

		return webTemplate.replace("<!--here apply template-->", sb.toString());
	}

	private void registerUris() {
		uris.put(START, new TestGeneratorNanoUri() {

			@Override
			String execute(IHTTPSession session) {
				startKilling();
				return generatePage();
			}
		});
		uris.put(STOP, new TestGeneratorNanoUri() {

			@SuppressWarnings("unchecked")
			@Override
			String execute(IHTTPSession session) {
				try {
					// We have to parse body before getting getQueryParameterString()
					session.parseBody(Collections.EMPTY_MAP);
				} catch (IOException | ResponseException e) {
					log.error("Cannot stop killing, please, try again. Error is " + e.getMessage(), e);
					throw new RuntimeException(e);
				}

				// Logger properties are updated
				Map<String, List<String>> updatedParameters = decodeParameters(session.getQueryParameterString());
				for (Entry<String, Properties> updatablePropertiesEntry : updatableProperties.entrySet()) {
					for (Entry<Object, Object> prop : updatablePropertiesEntry.getValue().entrySet()) {
						String key = updatablePropertiesEntry.getKey() + "_" + prop.getKey();
						List<String> updatedParameter = updatedParameters.get(key);
						if (updatedParameter != null && updatedParameter.size() > 0) {
							updatablePropertiesEntry.getValue().setProperty(String.valueOf(prop.getKey()), updatedParameter.get(0));
						}

					}
				}
				stopKilling();
				return generatePage();
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

	private abstract class TestGeneratorNanoUri {

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

	private void mapHtmlPage(final String name) {
		uris.put(name, new TestGeneratorNanoUri() {
			@Override
			String execute(IHTTPSession session) {
				return loadTextResource(name);
			}
		});
	}

}