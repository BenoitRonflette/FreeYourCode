package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.google.common.primitives.Primitives;

public final class DeepResolver extends DeepAnalyser {
	// TODO merge with resolveer

	private DeepResolver() {
	}

	private boolean isResolve(Tree tree, String path) {
		return tree.toString().equals(path);
	}

	private Object resolveInBranchInit(Tree tree, String path, Object o) throws Exception {
		if (path == null) {
			throw new Exception("Cannot resolve a null path");
		}
		Object resolved = resolveInBranch(tree, path, o);
		if (resolved == null) {
			throw new Exception("Cannot resolve path " + path);
		}
		return resolved;
	}

	private Object resolveInBranch(Tree tree, String path, Object o) throws Exception {
		Object response = null;
		if (o != null) {
			if (isResolve(tree, path)) {
				return o;
			}

			Class<? extends Object> c = o.getClass();
			if (c.isArray()) {
				response = resolveInBranchArray(tree, path, o);
			} else if (Collection.class.isAssignableFrom(c)) {
				response = resolveInBranchCollection(tree, path, (Collection<?>) o);
			} else if (Map.class.isAssignableFrom(c)) {
				response = resolveInBranchMap(tree, path, (Map<?, ?>) o);
			} else {
				response = resolveInBranchObject(tree, path, o);
			}
		}
		tree.topDown();
		return response;
	}

	private Object resolveInBranchObject(Tree tree, String path, Object o) throws Exception {
		Class<?> type = Primitives.unwrap(o.getClass());
		if (!isBasicType(type) && shouldVisit(o)) {
			for (Field field : getAllField(o.getClass())) {
				Object response = resolveInField(tree, path, field, o);
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

	private Object resolveInField(Tree tree, String path, Field field, Object o) throws Exception {
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible) {
			field.setAccessible(true);
		}

		tree.bottomUp(field.getName());

		Object response = resolveInBranch(tree, path, field.get(o));

		if (!wasAccessible) {
			field.setAccessible(false);
		}

		return response;
	}

	private <T> Object resolveInBranchCollection(Tree tree, String path, Collection<T> c) throws Exception {
		if (shouldVisit(c)) {
			int position = 0;
			Iterator<?> it = c.iterator();
			while (it.hasNext()) {
				tree.bottomUp(String.valueOf(position++));
				Object response = resolveInBranch(tree, path, it.next());
				if (response != null) {
					return response;
				}
			}

		}
		return null;
	}

	private <K, V> Object resolveInBranchMap(Tree tree, String path, Map<K, V> m) throws Exception {
		if (shouldVisit(m)) {
			// While we haven't found a modified entry (directly), we don't modify Map values.
			for (K key : m.keySet()) {
				tree.bottomUp(String.valueOf(key));
				Object response = resolveInBranch(tree, path, m.get(key));
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

	private Object resolveInBranchArray(Tree tree, String path, Object array) throws Exception {
		if (shouldVisit(array)) {
			for (int i = 0; i < Array.getLength(array); i++) {
				tree.bottomUp(String.valueOf(i));
				Object response = resolveInBranch(tree, path, Array.get(array, i));
				if (response != null) {
					return response;
				}
			}
		}
		return null;
	}

	public static Object resolve(String path, Object object) throws Exception {
		return new DeepResolver().resolveInBranchInit(new Tree(), path, object);
	}

}