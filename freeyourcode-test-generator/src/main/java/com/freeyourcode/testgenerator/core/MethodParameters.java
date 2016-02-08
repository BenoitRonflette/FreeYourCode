package com.freeyourcode.testgenerator.core;

import java.io.IOException;
import java.util.List;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;
import com.google.common.collect.Lists;

/**
 * Represent input parameters for a method. Serialization is performed in constructor to congeal the input values.
 */
public class MethodParameters {

	//TODO essayer de faire disparaître l'utilisation des inputs params qui ne seraient plus à stocker (mais actuellement ils
	//st utilisés dans l'indexation des events, pr la résolution des casts et les casts.
	//essayer de gérer un enregistrement des class ici (cad pouvoir un truc a surcharger pr le unproxy avant le cast + factory).
	//ca veut aussi dire enregistrer le hash code et ainsi qu'une clé pr gérer le equals... (plus complexe).
	//private List<Class<?>> paramsClass;
	//FIXME Par ailleurs le equals de l'indexation est pas forcément top car il n'y a pas forcément une méthode équals sur chaque objet testé !!
	private List<Object> inputParams;
	private String serializedParamsAsObjectArrayOnEnter;
	private String serializedParamsAsObjectArrayOnExit;

	public MethodParameters(Object[] inputParams) {
		this.inputParams = Lists.newArrayList(inputParams);
	}

	public List<Object> getInputParams() {
		return inputParams;
	}

	public void setInputParams(List<Object> inputParams) {
		this.inputParams = inputParams;
	}
	
	public void freezeExit() throws IOException{
		//Several listeners can ask for a frozen event, we freeze it only once!
		if(serializedParamsAsObjectArrayOnExit == null){
			serializedParamsAsObjectArrayOnExit = buildSerializedParams(this.inputParams);
		}
	}
	
	public void freezeEnter() throws IOException{
		//Several listeners can ask for a frozen event, we freeze it only once!
		if(serializedParamsAsObjectArrayOnEnter == null){
			serializedParamsAsObjectArrayOnEnter = buildSerializedParams(this.inputParams);
		}
	}
	
	public String getSerializedParamsAsObjectArrayOnExit(){
		return serializedParamsAsObjectArrayOnExit;
	}
	
	public String getSerializedParamsAsObjectArrayOnEnter() {
		return serializedParamsAsObjectArrayOnEnter;
	}
	
	private static String buildSerializedParams(List<Object> inputParams) throws IOException{
		if (inputParams != null && inputParams.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("new Object[]{");
			
			for (int i = 0; i < inputParams.size(); i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(JsonSerialisationUtils.writeObject(inputParams.get(i)));
			}
			sb.append("}");
			return sb.toString();
		}
		return null;
	}
	
}