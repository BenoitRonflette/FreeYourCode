package com.freeyourcode.test.utils.deepanalyser;


public final class DeepCopy extends DeepAnalyser {
	// FIXME to be removed
	// private final Field modifiersField;
	//
	// private DeepCopy() throws NoSuchFieldException, SecurityException {
	// modifiersField = Field.class.getDeclaredField("modifiers");
	// modifiersField.setAccessible(true);
	// }
	//
	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// private Object updateBranch(Object o1, Object o2) throws Exception{
	// if(o1 == null){
	// return o2;
	// }
	// else if(o2 == null){
	// return null;
	// }
	// else{
	// Class<? extends Object> c = o1.getClass();
	// if(c.isArray()){
	// return updateBranchArray(o1, o2);
	// }
	// else if(Collection.class.isAssignableFrom(c)){
	// return updateBranchCollection((Collection)o1, (Collection)o2);
	// }
	// else if(Map.class.isAssignableFrom(c)){
	// return updateBranchMap((Map)o1, (Map)o2);
	// }
	// }
	// return updateBranchObject(o1, o2);
	// }
	//
	// private Object updateBranchObject(Object o1, Object o2) throws Exception{
	// Class<?> type = Primitives.unwrap(o1.getClass());
	// if(isBasicType(type)){
	// return o2;
	// }
	// else if(shouldVisit(o1)){
	// for(Field field : getAllField(o1.getClass())){
	// copyField(field, o1, o2);
	// }
	// }
	// return o1;
	// }
	//
	// private void copyField(Field field, Object o1, Object o2) throws Exception{
	// boolean wasAccessible = field.isAccessible();
	// boolean wasFinal = Modifier.isFinal(field.getModifiers());
	// if(!wasAccessible){
	// field.setAccessible(true);
	// }
	// if(wasFinal){
	// modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
	// }
	//
	// Object v1 = field.get(o1);
	// Object v2 = field.get(o2);
	// field.set(o1, updateBranch(v1, v2));
	//
	// if(!wasAccessible){
	// field.setAccessible(false);
	// }
	// if(wasFinal){
	// modifiersField.setInt(field, field.getModifiers() & Modifier.FINAL);
	// }
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <T> Collection<T> updateBranchCollection(Collection<T> c1, Collection<T> c2) throws Exception{
	// if(shouldVisit(c1)){
	// List<T> collecCopy = Lists.newArrayList(c1);
	// List<T> collecCopy2 = Lists.newArrayList(c2);
	//
	// //TODO cas hibernate, valeurs st les mêmes mais instanciation change
	//
	// //To preserve the potential order of Sets, we serialize with LinkedHashSet, if classes are not the same, we consider c1 as the real class.
	// if(c1.getClass().equals(c2.getClass()) ){
	// store(c1, c1);
	// if(c1.size()==c2.size()){
	// Iterator<T> itC1 = collecCopy.iterator();
	// Iterator<T> itC2 = collecCopy2.iterator();
	// boolean collectionHasBeenModified=false;
	// //Same class, same size, we are going to modify the underlying objects, not the list itself to avoid concurrent exception
	// while(itC1.hasNext()){
	// T eltC1 = itC1.next();
	// T newObject = (T)updateBranch(eltC1, itC2.next());
	// if(newObject!=eltC1){
	// //If direct underlying object hasn't got the same reference, we have to update it despite of the risk of concurrent exception
	// collectionHasBeenModified=true;
	// break;
	// }
	// }
	//
	// if(!collectionHasBeenModified){
	// //either there is no change or only the underlying objects with a lower level as -1 have been modified, we use the same list.
	// return c1;
	// }
	// //else, we are going to build a new collection
	// }
	//
	// //newInstance to avoid concurrent exceptions when the analyzed code is performing a loop
	// return modifyBranchCollection((Collection<T>)c1, collecCopy, collecCopy2);
	// }
	// else {//FIXME manage store everyWhere
	// store(c1, c2);
	// if(shouldVisit(c2)){
	// return modifyBranchCollection(c2, collecCopy, collecCopy2);
	// }
	// return c2;
	// }
	// }
	// return get(c1);
	// }
	//
	//
	// @SuppressWarnings("unchecked")
	// private <T> Collection<T> modifyBranchCollection(Collection<T> newCollection, List<T> c1, List<T> c2) throws Exception{
	// Iterator<T> itC1 = c1.iterator();
	// Iterator<T> itC2 = c2.iterator();
	// newCollection.clear();
	// while(itC1.hasNext()){
	// T eltC1 = itC1.next();
	// if(!itC2.hasNext()){
	// itC1.remove();
	// }
	// else{
	// newCollection.add((T)updateBranch(eltC1, itC2.next()));
	// }
	// }
	// while(itC2.hasNext()){
	// newCollection.add(itC2.next());
	// }
	// return newCollection;
	// }
	//
	// @SuppressWarnings("unchecked")
	// private <K,V> Map<K,V> updateBranchMap(Map<K,V> m1, Map<K,V> m2) throws Exception{
	// if(shouldVisit(m1)){
	// Set<K> keys2 = m2.keySet();
	// for(K key1 : m1.keySet()){
	// if(!keys2.remove(key1)){
	// //Keys2 didn't contain key1
	// m1.remove(key1);
	// }
	// else{
	// m1.put(key1, (V) updateBranch(m1.get(key1), m2.get(key1)));
	// }
	// }
	// //Missing keys2 in m1
	// for(K key2 : keys2){
	// m1.put(key2, m2.get(key2));
	// }
	// }
	// return m1;
	// }
	//
	// private Object updateBranchArray(Object array1, Object array2) throws Exception{
	// int len1 = Array.getLength(array1);
	// int len2 = Array.getLength(array2);
	// if(len1 != len2){
	// return array2;
	// }
	// else if(shouldVisit(array1)){
	// for(int i = 0; i < len1; i++){
	// Array.set(array1, i, updateBranch(Array.get(array1, i), Array.get(array2, i)));
	// }
	// }
	// return array1;
	// }
	//
	//
	// /**
	// * Perform a Deep copy keeping the 'to' references as soon as possible.
	// * If to is null or a basic type (primitive, String or Date), no copy
	// * is performed.
	// */
	// public static void copy(Object from, Object to) throws Exception{
	// if(to != null && !isBasicType(to.getClass())){
	// new DeepCopy().updateBranch(to, from);
	// }
	// }
	//
	// public static class ExampleObject{
	// String string;
	// int i;
	// Integer iObject;
	// Date date;
	// List<SubExampleObject> list;
	// int[] iArray;
	// Object[] oArray;
	// Map<String, Object> map;
	//
	// public ExampleObject(String string, int i, Integer iObject, Date date, List<SubExampleObject> list, int[] iArray, Object[] oArray, Map<String, Object> map) {
	// super();
	// this.string = string;
	// this.i = i;
	// this.iObject = iObject;
	// this.date = date;
	// this.list = list;
	// this.iArray = iArray;
	// this.oArray = oArray;
	// this.map = map;
	// }
	//
	// @Override
	// public String toString() {
	// return ToStringBuilder.reflectionToString(this);
	// }
	// }
	//
	// public static class SubExampleObject{
	// String name;
	// Object value;
	//
	// public SubExampleObject(String name, Object value) {
	// this.name = name;
	// this.value = value;
	// }
	//
	// @Override
	// public String toString() {
	// return ToStringBuilder.reflectionToString(this);
	// }
	// }
	//
	// public static void main(String[] args) {
	// try {
	// List<SubExampleObject> list = new ArrayList<SubExampleObject>();
	// List<SubExampleObject> listOnExit = new ArrayList<SubExampleObject>();
	//
	// SubExampleObject subO = new SubExampleObject("Hello", null);
	// list.add(subO);
	//
	// SubExampleObject subOOnlyOnExit1 = new SubExampleObject("Hello1", subO);
	// listOnExit.add(subOOnlyOnExit1);
	// SubExampleObject subOOnlyOnExit2 = new SubExampleObject("HelloOnExit", null);
	// listOnExit.add(subOOnlyOnExit2);
	//
	// Map<String, Object> map = new HashMap<String, Object>();
	//
	// ExampleObject input = new ExampleObject("myString", 0, 4, new Date(), list, new int[]{7,78,9}, new Object[]{subO}, null);
	// ExampleObject inputOnExit = new ExampleObject("myString2", 1, null, new Date(new Date().getTime()*2), listOnExit, new int[]{78,9}, null, map);
	//
	//
	// System.out.println(input);
	// System.out.println(inputOnExit);
	// copy(inputOnExit, input);
	// System.out.println(input);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

}