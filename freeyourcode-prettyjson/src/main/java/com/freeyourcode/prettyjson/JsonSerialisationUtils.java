package com.freeyourcode.prettyjson;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public final class JsonSerialisationUtils {
	
	private JsonSerialisationUtils(){
	}
	
	public static String writeObject(Object o) throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("JsonSerialisationUtils.deserialize(\"");
		sb.append(StringEscapeUtils.escapeJava(serialize(o)));
		sb.append("\")");
		return sb.toString();
	}
	
	private static String serialize(Object o) throws IOException{
		return JsonWriter.objectToJson(o);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String serialized) throws IOException{
		return (T) JsonReader.jsonToJava(serialized);
	}
	
	

}
