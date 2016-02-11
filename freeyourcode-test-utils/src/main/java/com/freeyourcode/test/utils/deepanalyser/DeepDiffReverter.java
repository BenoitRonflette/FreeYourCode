package com.freeyourcode.test.utils.deepanalyser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;

public final class DeepDiffReverter extends DeepAnalyser {

	private Map<String, Object> diffsToBeApplied;

	private DeepDiffReverter(Map<String, Object> diffsToBeApplied) {
		this.diffsToBeApplied = diffsToBeApplied;
	}

	private boolean shouldCollectionBeReorganized(Tree tree) {
		Pattern pattern = Pattern.compile(tree.toString() + "\\.\\d+");
		for (String key : diffsToBeApplied.keySet()) {
			if (pattern.matcher(key).matches()) {
				// at least one element has been modified
				return true;
			}
		}
		return false;
	}

	private <K, V> void addRemainingMapEntry(Tree tree, Map<K, V> m) {
		String mapPath = tree.toString();

		for (String key : diffsToBeApplied.keySet()) {
			if (key.startsWith(mapPath)) {
				@SuppressWarnings("unchecked")
				V value = (V) diffsToBeApplied.remove(key);
				String mKey = key.replaceFirst(mapPath + ".", "");
				m.put(convert(mKey, m), value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <K> K convert(String s, Map<K, ?> m) {
		if (m.size() == 0) {
			// TODO improvement to try to retrieve the map key type if there is no value
			return (K) (s);
		}

		Class<K> c = (Class<K>) m.keySet().iterator().next().getClass();
		if (String.class.isAssignableFrom(c)) {
			return (K) (s);
		} else if (Integer.class.isAssignableFrom(c)) {
			return (K) ((Object) Integer.parseInt(s));
		} else if (Double.class.isAssignableFrom(c)) {
			return (K) ((Object) Double.parseDouble(s));
		} else {
			// TODO improvement to try to retrieve the map key type if there is no value
			throw new RuntimeException("Unsupported map key type: " + c + ". Please, ask for an improvement to support it !");
		}
	}

	@Override
	protected boolean shouldVisit(Object o) {
		return diffsToBeApplied.size() > 0 && super.shouldVisit(o);
	}

	private boolean isDiff(Tree tree) {
		return diffsToBeApplied.containsKey(tree.toString());
	}

	private Object applyDiff(Tree tree) {
		String key = tree.toString();
		tree.topDown();
		return diffsToBeApplied.remove(key);
	}

	private Object revertDiffBranchInit(Tree tree, Object o) throws Exception {
		if (diffsToBeApplied == null || diffsToBeApplied.size() == 0) {
			return o;
		}

		if (isDiff(tree)) {
			return applyDiff(tree);
		}

		revertDiffBranch(tree, o);
		return o;
	}

	private void revertDiffBranch(Tree tree, Object o) throws Exception {
		if (o != null) {
			Class<? extends Object> c = o.getClass();
			if (c.isArray()) {
				revertDiffBranchArray(tree, o);
			} else if (Collection.class.isAssignableFrom(c)) {
				revertDiffBranchCollection(tree, (Collection<?>) o);
			} else if (Map.class.isAssignableFrom(c)) {
				revertDiffBranchMap(tree, (Map<?, ?>) o);
			} else {
				revertDiffBranchObject(tree, o);
			}
		}
		tree.topDown();
	}

	private void revertDiffBranchObject(Tree tree, Object o) throws Exception {
		Class<?> type = Primitives.unwrap(o.getClass());
		if (!isBasicType(type) && shouldVisit(o)) {
			for (Field field : getAllField(o.getClass())) {
				revertDiffField(tree, field, o);
			}
		}
	}

	private void revertDiffField(Tree tree, Field field, Object o) throws Exception {
		boolean wasAccessible = field.isAccessible();
		if (!wasAccessible) {
			field.setAccessible(true);
		}

		Object v = field.get(o);
		tree.bottomUp(field.getName());
		if (isDiff(tree)) {
			field.set(o, applyDiff(tree));
		} else {
			revertDiffBranch(tree, v);
		}

		if (!wasAccessible) {
			field.setAccessible(false);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void revertDiffBranchCollection(Tree tree, Collection<T> c) throws Exception {
		if (shouldVisit(c)) {
			int position = 0;
			if (shouldCollectionBeReorganized(tree)) {
				List<T> collecCopy = Lists.newArrayList(c);
				c.clear();
				Iterator<T> it = collecCopy.iterator();
				while (it.hasNext()) {
					T elt = it.next();
					tree.bottomUp(String.valueOf(position++));
					if (isDiff(tree)) {
						c.add((T) applyDiff(tree));
					} else {
						c.add(elt);
						revertDiffBranch(tree, elt);
					}
				}

				// Maybe the other list was bigger, i.e. it has got other diffs.
				tree.bottomUp(String.valueOf(position++));
				while (isDiff(tree)) {
					c.add((T) applyDiff(tree));
					tree.bottomUp(String.valueOf(position++));
				}
				tree.topDown();
			} else {
				Iterator<?> it = c.iterator();
				while (it.hasNext()) {
					tree.bottomUp(String.valueOf(position++));
					revertDiffBranch(tree, it.next());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <K, V> void revertDiffBranchMap(Tree tree, Map<K, V> m) throws Exception {
		if (shouldVisit(m)) {
			// While we haven't found a modified entry (directly), we don't modify Map values.
			for (K key : m.keySet()) {
				tree.bottomUp(String.valueOf(key));

				if (isDiff(tree)) {
					m.put(key, (V) applyDiff(tree));
				} else {
					revertDiffBranch(tree, m.get(key));
				}
			}

			// If there are remaining diffs beginning with the same tree path, it's new map objects, we insert them:
			addRemainingMapEntry(tree, m);
		}
	}

	private void revertDiffBranchArray(Tree tree, Object array) throws Exception {
		if (shouldVisit(array)) {
			for (int i = 0; i < Array.getLength(array); i++) {
				tree.bottomUp(String.valueOf(i));
				if (isDiff(tree)) {
					Array.set(array, i, applyDiff(tree));
				} else {
					revertDiffBranch(tree, Array.get(array, i));
				}
			}
		}
	}

	public static Object revertDiffs(Object object, Map<String, Object> diffsToBeApplied) throws Exception {
		return new DeepDiffReverter(diffsToBeApplied).revertDiffBranchInit(new Tree(), object);
	}

}