package com.freeyourcode.testgenerator.server;

public enum SupportedExtension {
	
	
	PNG("png", "image/png"),JPG("jpg", "image/jpeg"),JPEG("jpeg", "image/jpeg"),CSS("css", "text/css")
	,JS("js", "application/javascript"),WOFF("woff", "application/font-woff"),TTF("ttf", "application/font-ttf")
	,EOT("eot", "application/vnd.ms-fontobject"),OTF("otf", "application/font-otf"),SVG("svg", "image/svg+xml");
	
	private final String extension;
	private final String typeMime;
	
	private SupportedExtension(String extension, String typeMime) {
		this.extension = extension;
		this.typeMime = typeMime;
	}

	public String getExtension() {
		return extension;
	}

	public String getTypeMime() {
		return typeMime;
	}

	public static SupportedExtension fromExtension(String extension){
		for(SupportedExtension value : values()){
			if(value.getExtension().equals(extension)){
				return value;
			}
		}
		return null;
	}

}