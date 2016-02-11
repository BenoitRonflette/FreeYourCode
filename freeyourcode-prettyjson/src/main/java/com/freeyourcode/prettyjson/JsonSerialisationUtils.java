package com.freeyourcode.prettyjson;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

public final class JsonSerialisationUtils {
	
	private JsonSerialisationUtils(){
	}
	
	public static String writeObjectInJava(Object o) throws IOException{
		return writeSerializedObjectInJava(serialize(o));
	}
	
	public static String writeSerializedObjectInJava(String serializedObject) throws IOException{
		if(serializedObject == null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("JsonSerialisationUtils.deserialize(\"");
		sb.append(StringEscapeUtils.escapeJava(serializedObject));
		sb.append("\")");
		return sb.toString();
	}
	
	public static String[] writeSerializedObjectsInJava(String[] serializedObjects) throws IOException {
		String[] serializedObjectsInJava = new String[serializedObjects.length];
		for (int i = 0; i < serializedObjects.length; i++) {
			serializedObjectsInJava[i] = JsonSerialisationUtils.writeSerializedObjectInJava(serializedObjects[i]);
		}
		return serializedObjectsInJava;
}
	
	public static String serialize(Object o) throws IOException{
		if(o == null){
			return null;
		}
		return JsonWriter.objectToJson(o);
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(String serialized) throws IOException{
		if(serialized == null){
			return null;
		}
		return (T) JsonReader.jsonToJava(serialized);
	}
	
	public static String[] serializeList(List<?> objects) throws IOException {
		if (objects != null && objects.size() > 0) {
			String[] serializedObjects = new String[objects.size()];
			for (int i = 0; i < objects.size(); i++) {
				Object object = objects.get(i);
				serializedObjects[i] = JsonSerialisationUtils.serialize(object);
			}
			return serializedObjects;
		}
		return new String[] {};
	}
	

}
