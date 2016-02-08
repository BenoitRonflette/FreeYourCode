package com.freeyourcode.testgenerator.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PropertiesUtils {

	private final static String PROPERTIES_PATTERN = "([^=,]+)=([^=,]+),?";
	
	private PropertiesUtils(){
	}
	
	 public static Properties parseProperties(String args) {
		    Properties properties = new Properties();
		    if (args != null) {
		      Pattern pat = Pattern.compile(PROPERTIES_PATTERN);
		      Matcher matcher = pat.matcher(args);
		      while (matcher.find())
		    	  properties.put(matcher.group(1), matcher.group(2));
		    }
		    return properties;
	}
	 
	 public static int getInt(Properties props, String key, boolean required){
		 String value = (String) props.get(key);
		 if((value == null || value.isEmpty())){
			 if(required){
				 throw new IllegalArgumentException("Property "+key+" is required");
			 }
			 return 0;
		 }
		 return Integer.parseInt(value);
	 }
	 
	 public static boolean getBoolean(Properties props, String key, boolean required){
		 String value = (String) props.get(key);
		 if((value == null || value.isEmpty())){
			 if(required){
				 throw new IllegalArgumentException("Property "+key+" is required");
			 }
			 return false;
		 }
		 return Boolean.parseBoolean(value);
	 }
	 
}