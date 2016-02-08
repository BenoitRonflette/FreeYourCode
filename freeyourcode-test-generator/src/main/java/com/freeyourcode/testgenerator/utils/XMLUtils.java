package com.freeyourcode.testgenerator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class XMLUtils {

	private XMLUtils(){
	}
	
	public static NodeList extractNodeListFromElement(Element config, String tag, boolean required){
		NodeList node = config.getElementsByTagName(tag);
		return check1Most(node, tag, required);
	}
	
	public static NodeList check1Most(NodeList node,String tag, boolean required){
		if(node.getLength() == 0){
			if(!required){
				return null;
			}
			throw new RuntimeException("Tag "+tag+" is required in configuration file");
		}
		else if(node.getLength() > 1){
			throw new RuntimeException("Only one tag "+tag+" is required in configuration file");
		}
		return node.item(0).getChildNodes();
	}
	
	public static List<Element> extractElementsFromNodeList(NodeList config, String tag, boolean required){
		List<Element> res = new ArrayList<Element>();
		for (int i = 0; i < config.getLength(); i++) {
			Node n = config.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(tag)) {
				res.add((Element) config.item(i));
			}
		}
		if(required && res.size() == 0){
			throw new RuntimeException("Tag "+tag+" is required in configuration file");
		}
		return res;
	}
	
	public static Properties extractProperties(Node node){
		Properties props = new Properties();
		NamedNodeMap attributes = node.getAttributes();
		if(attributes != null){
			for(int i = 0; i < attributes.getLength(); i++){
				Node item = attributes.item(i);
				props.put(item.getNodeName(), item.getNodeValue());
			}
		}
		return props;
	}
	
}
