package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.primitives.Primitives;

public class DeepDiff extends DeepAnalyser {

	private final List<Diff> diffs = new ArrayList<Diff>();
	private final boolean light;

	public static class Diff {
		public final String path;
		public final Object o1Value;
		public final Object o2Value;

		public Diff(String path, Object o1Value, Object o2Value) {
			super();
			this.path = path;
			this.o1Value = o1Value;
			this.o2Value = o2Value;
		}

	}

	public DeepDiff() {
		this(false);
	}

	public DeepDiff(boolean light) {
		this.light = light;
	}

	protected void saveDiff(Tree tree, Object o1, Object o2) {
		diffs.add(new Diff(tree.toString(), o1, o2));
	}

	protected boolean shouldVisit(Tree tree, Object o) {
		return super.shouldVisit(o) && (!light || tree.deep() == 0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected DeepDiff diffBranch(Tree tree, Object o1, Object o2) throws Exception {
		if (o1 == null) {
			if (o2 != null) {
				saveDiff(tree, o1, o2);
			}
		} else if (o2 == null) {
			saveDiff(tree, o1, o2);
		} else {
			Class<? extends Object> c = o1.getClass();
			if (c.isArray()) {
				diffBranchArray(tree, o1, o2);
			} else if (Collection.class.isAssignableFrom(c)) {
				diffBranchCollection(tree, (Collection) o1, (Collection) o2);
			} else if (Map.class.isAssignableFrom(c)) {
				diffBranchMap(tree, (Map) o1, (Map) o2);
			} else {
				diffBranchObject(tree, o1, o2);
			}
		}
		tree.topDown();
		return this;
	}

	private Object diffBranchObject(Tree tree, Object o1, Object o2) throws Exception {
		Class<?> type = Primitives.unwrap(o1.getClass());
		if (isBasicType(type)) {
			if (!o1.equals(o2)) {
				saveDiff(tree, o1, o2);
			}
		} else if (shouldVisit(tree, o1)) {
			for (Field field : getAllField(o1.getClass())) {
				diffField(tree, field, o1, o2);
			}
		}
		return o1;
	}

	private void diffField(Tree tree, Field field, Object o1, Object o2) throws Exception {
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible) {
			field.setAccessible(true);
		}

		Object v1 = field.get(o1);
		Object v2 = field.get(o2);
		tree.bottomUp(field.getName());
		diffBranch(tree, v1, v2);

		if (!wasAccessible) {
			field.setAccessible(false);
		}
	}

	private <T> void diffBranchCollection(Tree tree, Collection<T> c1, Collection<T> c2) throws Exception {
		if (shouldVisit(tree, c1)) {
			if (isNonDeterministCollection(c1.getClass()) || isNonDeterministCollection(c2.getClass())) {
				c1 = determine(c1);
				c2 = determine(c2);
			}

			int position = 0;
			Iterator<?> itC1 = c1.iterator();
			Iterator<?> itC2 = c2.iterator();
			while (itC1.hasNext()) {
				Object eltC1 = itC1.next();
				tree.bottomUp(String.valueOf(position++));

				if (!itC2.hasNext()) {
					saveDiff(tree, eltC1, null);
					tree.topDown();
				} else {
					diffBranch(tree, eltC1, itC2.next());
				}

			}
			while (itC2.hasNext()) {
				tree.bottomUp(String.valueOf(position++));
				saveDiff(tree, null, itC2.next());
				tree.topDown();
			}
		}
	}

	private void diffBranchMap(Tree tree, Map<?, ?> m1, Map<?, ?> m2) throws Exception {
		if (shouldVisit(tree, m1)) {
			// New set containing keys from m2 to avoid to remove m2 content when we are performing removing operations on keys2
			Set<?> keys2 = new HashSet<Object>(m2.keySet());
			for (Object key1 : m1.keySet()) {
				tree.bottomUp(String.valueOf(key1));

				if (!keys2.remove(key1)) {
					// Keys2 didn't contain key1
					saveDiff(tree, m1.get(key1), null);
					tree.topDown();
				} else {
					diffBranch(tree, m1.get(key1), m2.get(key1));
				}
			}
			// Missing keys2 in m1
			for (Object key2 : keys2) {
				tree.bottomUp(String.valueOf(key2));
				saveDiff(tree, null, m2.get(key2));
				tree.topDown();
			}
		}
	}

	private void diffBranchArray(Tree tree, Object array1, Object array2) throws Exception {
		int len1 = Array.getLength(array1);
		int len2 = Array.getLength(array2);

		if (len1 != len2) {
			saveDiff(tree, array1, array2);
		} else if (shouldVisit(tree, array1)) {
			for (int i = 0; i < len1; i++) {
				tree.bottomUp(String.valueOf(i));
				diffBranch(tree, Array.get(array1, i), Array.get(array2, i));
			}
		}
	}

	public DeepDiff diff(Object expected, Object actual) throws Exception {
		return diffBranch(new Tree(), expected, actual);
	}

	public List<Diff> getDiffs() {
		return diffs;
	}

	public Map<String, Object> getDiffsAsMap() throws Exception {
		Map<String, Object> valuesToBeUpdated = new HashMap<String, Object>();
		for (Diff diff : getDiffs()) {
			valuesToBeUpdated.put(diff.path, diff.o2Value);
		}
		return valuesToBeUpdated;
	}

	public List<String> prettyDiff() throws Exception {
		return toString(getDiffs());
	}

	private static List<String> toString(List<Diff> diffs) {
		List<String> diffAsString = new ArrayList<String>();
		for (Diff diff : diffs) {
			if (diff.o1Value != null && diff.o1Value.getClass().isArray() || diff.o2Value != null && diff.o2Value.getClass().isArray()) {
				diffAsString.add(diff.path + " length --> expected was <" + toArrayLength(diff.o1Value) + "> but is <" + toArrayLength(diff.o2Value) + ">");
			} else {
				diffAsString.add(diff.path + " --> expected was <" + diff.o1Value + "> but is <" + diff.o2Value + ">");
			}
		}
		return diffAsString;
	}

	private static String toArrayLength(Object o) {
		if (o == null) {
			return "null array";
		}
		return String.valueOf(Array.getLength(o));
	}

}