package com.freeyourcode.testgenerator.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.freeyourcode.prettyjson.JsonSerialisationUtils;
import com.freeyourcode.test.utils.deepanalyser.DeepDiff;
import com.google.common.collect.Lists;

/**
 * Represent input parameters for a method. Serialization is performed in constructor to congeal the input values.
 */
public class MethodParameters {

	// TODO essayer de faire disparaître l'utilisation des inputs params qui ne seraient plus à stocker (mais actuellement ils
	// st utilisés dans l'indexation des events, pr la résolution des casts et les casts.
	// essayer de gérer un enregistrement des class ici (cad pouvoir un truc a surcharger pr le unproxy avant le cast + factory).
	// ca veut aussi dire enregistrer le hash code et ainsi qu'une clé pr gérer le equals... (plus complexe).
	// private List<Class<?>> paramsClass;
	// FIXME Par ailleurs le equals de l'indexation est pas forcément top car il n'y a pas forcément une méthode équals sur chaque objet testé !!
	private List<Object> inputParams;

	// We serialize parameters on input to use with Mockito matchers.
	private String[] frozenParametersOnEnter;
	// We serialize input parameters on output to compare the final main test method result.
	private String[] frozenParametersOnExit;
	// We serialize differences between the parameters values on enter and on exit because parameters could be modified during method execution.
	private String[] frozenParameterDifferencesOnExit;

	public MethodParameters(Object[] inputParams) {
		this.inputParams = Lists.newArrayList(inputParams);
	}

	public List<Object> getInputParams() {
		return inputParams;
	}

	public void setInputParams(List<Object> inputParams) {
		this.inputParams = inputParams;
	}

	public void freezeDiffsExit() throws Exception {
		// Several listeners can ask for a frozen event, we freeze it only once!
		if (frozenParameterDifferencesOnExit == null) {
			List<Map<String, Object>> differencesWithEnter = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < frozenParametersOnEnter.length; i++) {
				// Modified values are updated on exit when test will be executed.
				differencesWithEnter.add(DeepDiff.diff(JsonSerialisationUtils.deserialize(frozenParametersOnEnter[i]), inputParams.get(i)).getDiffsAsMap());
			}
			frozenParameterDifferencesOnExit = asSerializedObjectArray(differencesWithEnter);
		}
	}

	public void freezeExit() throws IOException {
		// Several listeners can ask for a frozen event, we freeze it only once!
		if (frozenParametersOnExit == null) {
			frozenParametersOnExit = asSerializedObjectArray(this.inputParams);
		}
	}

	public void freezeEnter() throws IOException {
		// Several listeners can ask for a frozen event, we freeze it only once!
		if (frozenParametersOnEnter == null) {
			frozenParametersOnEnter = asSerializedObjectArray(this.inputParams);
		}
	}

	private static String[] asSerializedObjectArray(List<?> inputParams) throws IOException {
		if (inputParams != null && inputParams.size() > 0) {
			String[] serializedObjects = new String[inputParams.size()];
			for (int i = 0; i < inputParams.size(); i++) {
				serializedObjects[i] = JsonSerialisationUtils.writeObject(inputParams.get(i));
			}
			return serializedObjects;
		}
		return new String[] {};
	}

	public String[] getFrozenParametersOnEnter() {
		return frozenParametersOnEnter;
	}

	public String[] getFrozenParametersOnExit() {
		return frozenParametersOnExit;
	}

	public String[] getFrozenParameterDifferencesOnExit() {
		return frozenParameterDifferencesOnExit;
	}

}