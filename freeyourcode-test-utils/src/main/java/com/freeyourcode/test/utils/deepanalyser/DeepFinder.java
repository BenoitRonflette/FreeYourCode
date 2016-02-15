package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.primitives.Primitives;

public class DeepFinder extends DeepAnalyser {

	public DeepFinder() {
	}

	private boolean isFound(Object searchedObject, Object o) {
		return searchedObject == o;
	}

	public String find(Object searchedObject, Object o) throws Exception {
		if (searchedObject == null) {
			return null;
		}
		return findInBranch(new Tree(), searchedObject, o);
	}

	private String findInBranch(Tree tree, Object searchedObject, Object o) throws Exception {
		String response = null;
		if (o != null) {
			if (isFound(searchedObject, o)) {
				return tree.toString();
			}

			Class<? extends Object> c = o.getClass();
			if (c.isArray()) {
				response = findInBranchArray(tree, searchedObject, o);
			} else if (Collection.class.isAssignableFrom(c)) {
				response = findInBranchCollection(tree, searchedObject, (Collection<?>) o);
			} else if (Map.class.isAssignableFrom(c)) {
				response = findInBranchMap(tree, searchedObject, (Map<?, ?>) o);
			} else {
				response = findInBranchObject(tree, searchedObject, o);
			}
		}
		tree.topDown();
		return response;
	}

	private String findInBranchObject(Tree tree, Object searchedObject, Object o) throws Exception {
		Class<?> type = Primitives.unwrap(o.getClass());
		if (!isBasicType(type) && shouldVisit(o)) {
			for (Field field : getAllField(o.getClass())) {
				String response = findInField(tree, searchedObject, field, o);
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

	private String findInField(Tree tree, Object searchedObject, Field field, Object o) throws Exception {
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible) {
			field.setAccessible(true);
		}

		tree.bottomUp(field.getName());

		String response = findInBranch(tree, searchedObject, field.get(o));

		if (!wasAccessible) {
			field.setAccessible(false);
		}

		return response;
	}

	private <T> String findInBranchCollection(Tree tree, Object searchedObject, Collection<T> c) throws Exception {
		if (shouldVisit(c)) {
			if (isNonDeterministCollection(c.getClass())) {
				c = determine(c);
			}

			int position = 0;
			Iterator<?> it = c.iterator();
			while (it.hasNext()) {
				tree.bottomUp(String.valueOf(position++));
				String response = findInBranch(tree, searchedObject, it.next());
				if (response != null) {
					return response;
				}
			}

		}
		return null;
	}

	private <K, V> String findInBranchMap(Tree tree, Object searchedObject, Map<K, V> m) throws Exception {
		if (shouldVisit(m)) {
			// While we haven't found a modified entry (directly), we don't modify Map values.
			for (K key : m.keySet()) {
				tree.bottomUp(String.valueOf(key));
				String response = findInBranch(tree, searchedObject, m.get(key));
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

	private String findInBranchArray(Tree tree, Object searchedObject, Object array) throws Exception {
		if (shouldVisit(array)) {
			for (int i = 0; i < Array.getLength(array); i++) {
				tree.bottomUp(String.valueOf(i));
				String response = findInBranch(tree, searchedObject, Array.get(array, i));
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

}