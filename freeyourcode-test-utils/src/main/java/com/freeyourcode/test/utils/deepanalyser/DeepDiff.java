package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;

public final class DeepDiff extends DeepAnalyser {

	private final List<String> diffs = new ArrayList<String>();
	
	private DeepDiff() {
	}

	public List<String> getDiffs() {
		return diffs;
	}

	private void saveDiff(Tree tree, Object o1, Object o2){
		diffs.add(tree.toString()+" --> expected was <"+o1+"> but is <"+o2+">");
	}
	
	private void saveDiffLength(Tree tree, int i1, int i2){
		diffs.add(tree.toString()+" length --> expected was <"+i1+"> but is <"+i2+">");
	}
	
	@SuppressWarnings({ "rawtypes" })
	private DeepDiff diffBranch(Tree tree, Object o1, Object o2) throws Exception{
		
		if(o1 == null){
			if(o2 != null){
				saveDiff(tree, o1, o2);
			}
		}
		else if(o2 == null){
			saveDiff(tree, o1, o2);
		}
		else{
			Class<? extends Object> c = o1.getClass();
			if(c.isArray()){
				diffBranchArray(tree,o1, o2);
			}
			else if(Collection.class.isAssignableFrom(c)){
				diffBranchCollection(tree,(Collection)o1, (Collection)o2);
			}
			else if(Map.class.isAssignableFrom(c)){
				diffBranchMap(tree,(Map)o1, (Map)o2);
			}
			else{
				diffBranchObject(tree, o1, o2);
			}
		}
		tree.topDown();
		return this;
	}
	
	private Object diffBranchObject(Tree tree, Object o1, Object o2) throws Exception{
			Class<?> type = Primitives.unwrap(o1.getClass());
			if(isBasicType(type)){
				if(!o1.equals(o2)){
					saveDiff(tree, o1, o2);	
				}
			}
			else if(shouldVisit(o1)){
				for(Field field : getAllField(o1.getClass())){
					diffField(tree, field, o1, o2);
				}
			}
			return o1;
	}
	
	private void diffField(Tree tree, Field field, Object o1, Object o2) throws Exception{
		boolean wasAccessible = field.isAccessible();
		if(!wasAccessible){
			field.setAccessible(true);
		}

		Object v1 = field.get(o1);
		Object v2 = field.get(o2);
		tree.bottomUp(field.getName());
		diffBranch(tree, v1, v2);

		if(!wasAccessible){
			field.setAccessible(false);
		}
	}
	
	private void diffBranchCollection(Tree tree, Collection<?> c1, Collection<?> c2) throws Exception{
		if(shouldVisit(c1)){
			List<Object> collecCopy = Lists.newArrayList(c1);
			c1.clear();
			int position = 0;
			Iterator<?> itC1 = collecCopy.iterator();
			Iterator<?> itC2 = c2.iterator();
			while(itC1.hasNext()){
				Object eltC1 = itC1.next();
				tree.bottomUp(String.valueOf(position++));
				
				if(!itC2.hasNext()){
					saveDiff(tree, eltC1, null);
					tree.topDown();
				}
				else{
				  diffBranch(tree, eltC1, itC2.next());
				}
				
			}
			while(itC2.hasNext()){
				tree.bottomUp(String.valueOf(position++));
				saveDiff(tree, null, itC2.next());
				tree.topDown();
			}
		}
	}
	
	private void diffBranchMap(Tree tree, Map<?,?> m1, Map<?,?> m2) throws Exception{
		if(shouldVisit(m1)){
			//New set containing keys from m2 to avoid to remove m2 content when we are performing removing operations on keys2
			Set<?> keys2 = new HashSet<Object>(m2.keySet());
			for(Object key1 : m1.keySet()){
				tree.bottomUp(String.valueOf(key1));
				
				if(!keys2.remove(key1)){
					//Keys2 didn't contain key1
					saveDiff(tree, m1.get(key1), null);
					tree.topDown();
				} 
				else{
					diffBranch(tree, m1.get(key1), m2.get(key1));
				}
			}
			//Missing keys2 in m1
			for(Object key2 : keys2){
				tree.bottomUp(String.valueOf(key2));
				saveDiff(tree, null, m2.get(key2));
				tree.topDown();
			}
		}
	}
	
	private void diffBranchArray(Tree tree, Object array1, Object array2) throws Exception{
		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);

		if(len1 != len2){
			saveDiffLength(tree, len1, len2);
		}
		else if(shouldVisit(array1)){
			for(int i = 0; i < len1; i++){
				tree.bottomUp(String.valueOf(i));
				diffBranch(tree, Array.get(array1, i), Array.get(array2, i));
			}
		}
	}
	

	public static List<String> diff(Object expected, Object actual) throws Exception{
		return new DeepDiff().diffBranch(new Tree(), expected, actual).getDiffs();
	}
	
	public static class ExampleObject{
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
	}
	
	public static class SubExampleObject{
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
	}
	
	public static void main(String[] args) {
		try {
			List<SubExampleObject> list = new ArrayList<SubExampleObject>();
			List<SubExampleObject> listOnExit = new ArrayList<SubExampleObject>();
			
			SubExampleObject subO = new SubExampleObject("Hello", null);
			list.add(subO);
			
			SubExampleObject subOOnlyOnExit1 = new SubExampleObject("Hello1", subO);
			listOnExit.add(subOOnlyOnExit1);
			SubExampleObject subOOnlyOnExit2 = new SubExampleObject("HelloOnExit", null);
			listOnExit.add(subOOnlyOnExit2);
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[]{7,78,9}, new Object[]{subO}, null);
			ExampleObject inputOnExit = new ExampleObject("myString2", 1, null, new Date(new Date().getTime()*2), listOnExit, new int[]{78,9}, null, map);
			
			
			System.out.println(input);
			System.out.println(inputOnExit);
			for(String s : diff(input, inputOnExit)){
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}