package com.freeyourcode.test.utils.deepanalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.testng.annotations.Test;

public class DeepTest {

	@Test
	public void testDeepDiff_sameValues() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);
		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);
		ExampleObject input2 = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);

		DeepDiff diffs = DeepDiff.diff(input, input2);

		Assert.assertEquals(diffs.getDiffs().size(), 0);
	}

	@Test
	public void testDeepDiff_sameInstance() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);
		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);

		DeepDiff diffs = DeepDiff.diff(input, input);
		Assert.assertEquals(diffs.getDiffs().size(), 0);
	}

	@Test
	public void testDeepDiff_sameSubInstances() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);
		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);
		ExampleObject input2 = new ExampleObject(input.string, input.i, input.iObject, input.date, list, input.iArray, input.oArray, input.map);

		DeepDiff diffs = DeepDiff.diff(input, input2);
		Assert.assertEquals(diffs.getDiffs().size(), 0);
	}

	@Test
	public void testDeepDiff_secondIsNull() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);
		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);

		DeepDiff diffs = DeepDiff.diff(input, null);
		Assert.assertEquals(diffs.getDiffs().size(), 1);
		Assert.assertEquals(diffs.getDiffs().get(0).path, "");
	}

	@Test
	public void testDeepDiff_firstIsNull() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);
		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);

		DeepDiff diffs = DeepDiff.diff(null, input);
		Assert.assertEquals(diffs.getDiffs().size(), 1);
		Assert.assertEquals(diffs.getDiffs().get(0).path, "");
	}

	@Test
	public void testDeepDiff() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		List<SubExampleObject> listOnExit = new ArrayList<SubExampleObject>();

		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);

		SubExampleObject subOOnlyOnExit1 = new SubExampleObject("Hello1", new SubExampleObject("Hello", null));
		listOnExit.add(subOOnlyOnExit1);
		SubExampleObject subOOnlyOnExit2 = new SubExampleObject("HelloOnExit", null);
		listOnExit.add(subOOnlyOnExit2);

		Map<String, Object> map = new HashMap<String, Object>();

		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);
		ExampleObject inputOnExit = new ExampleObject("myString2", 1, null, new Date(new Date().getTime() * 2), listOnExit, new int[] { 78, 9 }, null, map);

		DeepDiff diffs = DeepDiff.diff(input, inputOnExit);
		Assert.assertEquals(diffs.getDiffs().size(), 10);

		System.out.println("=================");
		System.out.println(" Initial object: " + input);
		System.out.println("Compared object: " + inputOnExit);
		for (String d : diffs.prettyDiff()) {
			System.out.println(d);
		}
		System.out.println("=================");

		Assert.assertEquals(diffs.getDiffs().get(0).path, "string");
		Assert.assertEquals(diffs.getDiffs().get(0).o1Value, "myString");
		Assert.assertEquals(diffs.getDiffs().get(0).o2Value, "myString2");

		Assert.assertEquals(diffs.getDiffs().get(1).path, "i");
		Assert.assertEquals(diffs.getDiffs().get(1).o1Value, 0);
		Assert.assertEquals(diffs.getDiffs().get(1).o2Value, 1);

		Assert.assertEquals(diffs.getDiffs().get(2).path, "iObject");
		Assert.assertEquals(diffs.getDiffs().get(2).o1Value, 4);
		Assert.assertEquals(diffs.getDiffs().get(2).o2Value, null);

		Assert.assertEquals(diffs.getDiffs().get(3).path, "date");

		Assert.assertEquals(diffs.getDiffs().get(4).path, "list.0.name");
		Assert.assertEquals(diffs.getDiffs().get(4).o1Value, "Hello");
		Assert.assertEquals(diffs.getDiffs().get(4).o2Value, "Hello1");

		Assert.assertEquals(diffs.getDiffs().get(5).path, "list.0.value");

		Assert.assertEquals(diffs.getDiffs().get(6).path, "list.1");
		Assert.assertEquals(diffs.getDiffs().get(6).o1Value, null);
		Assert.assertNotNull(diffs.getDiffs().get(6).o2Value);

		Assert.assertEquals(diffs.getDiffs().get(7).path, "iArray");
		Assert.assertEquals(((int[]) diffs.getDiffs().get(7).o1Value).length, 3);
		Assert.assertEquals(((int[]) diffs.getDiffs().get(7).o2Value).length, 2);

		Assert.assertEquals(diffs.getDiffs().get(8).path, "oArray");
		Assert.assertEquals(((Object[]) diffs.getDiffs().get(8).o1Value).length, 1);
		Assert.assertNull(diffs.getDiffs().get(8).o2Value);

		Assert.assertEquals(diffs.getDiffs().get(9).path, "map");
		Assert.assertEquals(diffs.getDiffs().get(9).o1Value, null);
		Assert.assertNotNull(diffs.getDiffs().get(9).o2Value);
	}

	@Test
	public void testDeepDiffReverter() throws Exception {
		List<SubExampleObject> list = new ArrayList<SubExampleObject>();
		List<SubExampleObject> listOnExit = new ArrayList<SubExampleObject>();

		SubExampleObject subO = new SubExampleObject("Hello", null);
		list.add(subO);

		SubExampleObject subOOnlyOnExit1 = new SubExampleObject("Hello1", new SubExampleObject("Hello", null));
		listOnExit.add(subOOnlyOnExit1);
		SubExampleObject subOOnlyOnExit2 = new SubExampleObject("HelloOnExit", null);
		listOnExit.add(subOOnlyOnExit2);

		Map<String, Object> map = new HashMap<String, Object>();

		ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[] { 7, 78, 9 }, new Object[] { subO }, null);
		ExampleObject inputOnExit = new ExampleObject("myString2", 1, null, new Date(new Date().getTime() * 2), listOnExit, new int[] { 78, 9 }, null, map);

		Map<String, Object> diffs = DeepDiff.diff(input, inputOnExit).getDiffsAsMap();
		Object inputModifiedWithInputOnExitDiff = DeepDiffReverter.revertDiffs(input, diffs);

		System.out.println(" Initial object: " + input);
		System.out.println("Compared object: " + inputOnExit);
		System.out.println("=================");
		System.out.println("Differences: ");

		for (Entry<String, Object> entry : diffs.entrySet()) {
			System.out.println(entry);
		}
		System.out.println("=================");
		System.out.println("Initial object with diffs: " + inputModifiedWithInputOnExitDiff);
		System.out.println("          Compared object: " + inputOnExit);

		Assert.assertEquals(inputOnExit, inputModifiedWithInputOnExitDiff);

		// There are no more diffs between the modified initial input and the inputOnExit after applying diffs!
		Assert.assertEquals(DeepDiff.diff(inputModifiedWithInputOnExitDiff, inputOnExit).getDiffs().size(), 0);
	}

	@Test
	public void testDeepFinder_inSubObject() throws Exception {
		SubExampleObject searchedObject = new SubExampleObject("Hello", null);

		List<SubExampleObject> listOnExit = new ArrayList<SubExampleObject>();
		SubExampleObject subOOnlyOnExit1 = new SubExampleObject("Hello1", searchedObject);
		listOnExit.add(subOOnlyOnExit1);
		SubExampleObject subOOnlyOnExit2 = new SubExampleObject("HelloOnExit", null);
		listOnExit.add(subOOnlyOnExit2);

		Map<String, Object> map = new HashMap<String, Object>();
		ExampleObject input = new ExampleObject("myString2", 1, null, new Date(new Date().getTime() * 2), listOnExit, new int[] { 78, 9 }, null, map);

		Assert.assertEquals("list.0.value", DeepFinder.find(searchedObject, input));
	}

	@Test
	public void testDeepFinder_sameObject() throws Exception {
		SubExampleObject searchedObject = new SubExampleObject("Hello", null);
		Assert.assertEquals("", DeepFinder.find(searchedObject, searchedObject));
	}

	@Test
	public void testDeepFinder_nullSearchedObject() throws Exception {
		SubExampleObject object = new SubExampleObject("Hello", null);
		Assert.assertEquals(null, DeepFinder.find(null, object));
	}

	@Test
	public void testDeepFinder_nullInput() throws Exception {
		SubExampleObject searchedObject = new SubExampleObject("Hello", null);
		Assert.assertEquals(null, DeepFinder.find(searchedObject, null));
	}

	public static class ExampleObject {
		String string;
		int i;
		Integer iObject;
		Date date;
		List<SubExampleObject> list;
		int[] iArray;
		Object[] oArray;
		Map<String, Object> map;

		public ExampleObject(String string, int i, Integer iObject, Date date, List<SubExampleObject> list, int[] iArray, Object[] oArray, Map<String, Object> map) {
			super();
			this.string = string;
			this.i = i;
			this.iObject = iObject;
			this.date = date;
			this.list = list;
			this.iArray = iArray;
			this.oArray = oArray;
			this.map = map;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			result = prime * result + i;
			result = prime * result + Arrays.hashCode(iArray);
			result = prime * result + ((iObject == null) ? 0 : iObject.hashCode());
			result = prime * result + ((list == null) ? 0 : list.hashCode());
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			result = prime * result + Arrays.hashCode(oArray);
			result = prime * result + ((string == null) ? 0 : string.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ExampleObject other = (ExampleObject) obj;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			if (i != other.i)
				return false;
			if (!Arrays.equals(iArray, other.iArray))
				return false;
			if (iObject == null) {
				if (other.iObject != null)
					return false;
			} else if (!iObject.equals(other.iObject))
				return false;
			if (list == null) {
				if (other.list != null)
					return false;
			} else if (!list.equals(other.list))
				return false;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			if (!Arrays.equals(oArray, other.oArray))
				return false;
			if (string == null) {
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}

	}

	public static class SubExampleObject {
		String name;
		Object value;

		public SubExampleObject(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubExampleObject other = (SubExampleObject) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

}
