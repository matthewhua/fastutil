/*
	* Copyright (C) 2002-2022 Sebastiano Vigna
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	*     http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
package it.unimi.dsi.fastutil.doubles;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.doubles.Double2FloatSortedMap.FastSortedEntrySet;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.NoSuchElementException;
/** A class providing static methods and objects that do useful things with type-specific sorted maps.
	*
	* @see java.util.Collections
	*/
public final class Double2FloatSortedMaps {
	private Double2FloatSortedMaps() {}
	/** Returns a comparator for entries based on a given comparator on keys.
	 *
	 * @param comparator a comparator on keys.
	 * @return the associated comparator on entries.
	 */
	public static Comparator<? super Map.Entry<Double, ?>> entryComparator(final DoubleComparator comparator) {
	 return (Comparator<Map.Entry<Double, ?>>) (x, y) -> comparator.compare((x.getKey()).doubleValue(), (y.getKey()).doubleValue());
	}
	/** Returns a bidirectional iterator that will be {@linkplain FastSortedEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract a (fast) bidirectional iterator on the entry set.
	 * @return a bidirectional iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectBidirectionalIterator<Double2FloatMap.Entry > fastIterator(Double2FloatSortedMap map) {
	 final ObjectSortedSet<Double2FloatMap.Entry > entries = map.double2FloatEntrySet();
	 return entries instanceof Double2FloatSortedMap.FastSortedEntrySet ? ((Double2FloatSortedMap.FastSortedEntrySet ) entries).fastIterator() : entries.iterator();
	}
	/** Returns an iterable yielding a bidirectional iterator that will be {@linkplain FastSortedEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract an iterable yielding a (fast) bidirectional iterator on the entry set.
	 * @return an iterable yielding a bidirectional iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectBidirectionalIterable<Double2FloatMap.Entry > fastIterable(Double2FloatSortedMap map) {
	 final ObjectSortedSet<Double2FloatMap.Entry > entries = map.double2FloatEntrySet();
	 return entries instanceof Double2FloatSortedMap.FastSortedEntrySet ? ((Double2FloatSortedMap.FastSortedEntrySet )entries)::fastIterator : entries;
	}
	/** An immutable class representing an empty type-specific sorted map.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific sorted map.
	 */
	public static class EmptySortedMap extends Double2FloatMaps.EmptyMap implements Double2FloatSortedMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected EmptySortedMap() {}
	 @Override
	 public DoubleComparator comparator() { return null; }
	 @Override
	 public ObjectSortedSet<Double2FloatMap.Entry > double2FloatEntrySet() { return ObjectSortedSets.EMPTY_SET; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() { return ObjectSortedSets.EMPTY_SET; }
	
	 @Override
	 public DoubleSortedSet keySet() { return DoubleSortedSets.EMPTY_SET; }
	
	 @Override
	 public Double2FloatSortedMap subMap(final double from, final double to) { return EMPTY_MAP; }
	
	 @Override
	 public Double2FloatSortedMap headMap(final double to) { return EMPTY_MAP; }
	
	 @Override
	 public Double2FloatSortedMap tailMap(final double from) { return EMPTY_MAP; }
	 @Override
	 public double firstDoubleKey() { throw new NoSuchElementException(); }
	 @Override
	 public double lastDoubleKey() { throw new NoSuchElementException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap headMap(Double oto) { return headMap((oto).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap tailMap(Double ofrom) { return tailMap((ofrom).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap subMap(Double ofrom, Double oto) { return subMap((ofrom).doubleValue(), (oto).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double firstKey() { return Double.valueOf(firstDoubleKey()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double lastKey() { return Double.valueOf(lastDoubleKey()); }
	}
	/** An empty sorted map (immutable). It is serializable and cloneable.
	 */

	public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();
	/** An immutable class representing a type-specific singleton sorted map.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific sorted map.
	 */
	public static class Singleton extends Double2FloatMaps.Singleton implements Double2FloatSortedMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final DoubleComparator comparator;
	 protected Singleton(final double key, final float value, DoubleComparator comparator) {
	  super(key, value);
	  this.comparator = comparator;
	 }
	 protected Singleton(final double key, final float value) {
	  this(key, value, null);
	 }
	
	 final int compare(final double k1, final double k2) {
	  return comparator == null ? ( Double.compare((k1),(k2)) ) : comparator.compare(k1, k2);
	 }
	 @Override
	 public DoubleComparator comparator() { return comparator; }
	
	 @Override
	 public ObjectSortedSet<Double2FloatMap.Entry > double2FloatEntrySet() { if (entries == null) entries = ObjectSortedSets.singleton(new AbstractDouble2FloatMap.BasicEntry (key, value), entryComparator(comparator)); return (ObjectSortedSet<Double2FloatMap.Entry >)entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() { return (ObjectSortedSet)double2FloatEntrySet(); }
	 @Override
	 public DoubleSortedSet keySet() { if (keys == null) keys = DoubleSortedSets.singleton(key, comparator); return (DoubleSortedSet )keys; }
	
	 @Override
	 public Double2FloatSortedMap subMap(final double from, final double to) { if (compare(from, key) <= 0 && compare(key, to) < 0) return this; return EMPTY_MAP; }
	
	 @Override
	 public Double2FloatSortedMap headMap(final double to) { if (compare(key, to) < 0) return this; return EMPTY_MAP; }
	
	 @Override
	 public Double2FloatSortedMap tailMap(final double from) { if (compare(from, key) <= 0) return this; return EMPTY_MAP; }
	 @Override
	 public double firstDoubleKey() { return key; }
	 @Override
	 public double lastDoubleKey() { return key; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap headMap(Double oto) { return headMap((oto).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap tailMap(Double ofrom) { return tailMap((ofrom).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap subMap(Double ofrom, Double oto) { return subMap((ofrom).doubleValue(), (oto).doubleValue()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double firstKey() { return Double.valueOf(firstDoubleKey()); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double lastKey() { return Double.valueOf(lastDoubleKey()); }
	}
	/** Returns a type-specific immutable sorted map containing only the specified pair. The returned sorted map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Double2FloatSortedMap singleton(final Double key, Float value) { return new Singleton ((key).doubleValue(), (value).floatValue());}
	/** Returns a type-specific immutable sorted map containing only the specified pair. The returned sorted map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @param comparator the comparator to use in the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Double2FloatSortedMap singleton(final Double key, Float value, DoubleComparator comparator) { return new Singleton ((key).doubleValue(), (value).floatValue(), comparator); }
	/** Returns a type-specific immutable sorted map containing only the specified pair. The returned sorted map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Double2FloatSortedMap singleton(final double key, final float value) {
	 return new Singleton (key, value);
	}
	/** Returns a type-specific immutable sorted map containing only the specified pair. The returned sorted map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @param comparator the comparator to use in the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Double2FloatSortedMap singleton(final double key, final float value, DoubleComparator comparator) {
	 return new Singleton (key, value, comparator);
	}
	 /** A synchronized wrapper class for sorted maps. */
	public static class SynchronizedSortedMap extends Double2FloatMaps.SynchronizedMap implements Double2FloatSortedMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Double2FloatSortedMap sortedMap;
	 protected SynchronizedSortedMap(final Double2FloatSortedMap m, final Object sync) {
	  super(m, sync);
	  sortedMap = m;
	 }
	 protected SynchronizedSortedMap(final Double2FloatSortedMap m) {
	  super(m);
	  sortedMap = m;
	 }
	 @Override
	 public DoubleComparator comparator() { synchronized(sync) { return sortedMap.comparator(); } }
	 @Override
	 public ObjectSortedSet<Double2FloatMap.Entry > double2FloatEntrySet() { if (entries == null) entries = ObjectSortedSets.synchronize(sortedMap.double2FloatEntrySet(), sync); return (ObjectSortedSet<Double2FloatMap.Entry >)entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() { return (ObjectSortedSet)double2FloatEntrySet(); }
	 @Override
	 public DoubleSortedSet keySet() { if (keys == null) keys = DoubleSortedSets.synchronize(sortedMap.keySet(), sync); return (DoubleSortedSet )keys; }
	 @Override
	 public Double2FloatSortedMap subMap(final double from, final double to) { return new SynchronizedSortedMap (sortedMap.subMap(from, to), sync); }
	 @Override
	 public Double2FloatSortedMap headMap(final double to) { return new SynchronizedSortedMap (sortedMap.headMap(to), sync); }
	 @Override
	 public Double2FloatSortedMap tailMap(final double from) { return new SynchronizedSortedMap (sortedMap.tailMap(from), sync); }
	 @Override
	 public double firstDoubleKey() { synchronized(sync) { return sortedMap.firstDoubleKey(); } }
	 @Override
	 public double lastDoubleKey() { synchronized(sync) { return sortedMap.lastDoubleKey(); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double firstKey() { synchronized(sync) { return sortedMap.firstKey(); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double lastKey() { synchronized(sync) { return sortedMap.lastKey(); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap subMap(final Double from, final Double to) { return new SynchronizedSortedMap (sortedMap.subMap(from, to), sync); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap headMap(final Double to) { return new SynchronizedSortedMap (sortedMap.headMap(to), sync); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap tailMap(final Double from) { return new SynchronizedSortedMap (sortedMap.tailMap(from), sync); }
	}
	/** Returns a synchronized type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in a synchronized sorted map.
	 * @return a synchronized view of the specified sorted map.
	 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
	 */
	public static Double2FloatSortedMap synchronize(final Double2FloatSortedMap m) { return new SynchronizedSortedMap (m); }
	/** Returns a synchronized type-specific sorted map backed by the given type-specific sorted map, using an assigned object to synchronize.
	 *
	 * @param m the sorted map to be wrapped in a synchronized sorted map.
	 * @param sync an object that will be used to synchronize the access to the sorted sorted map.
	 * @return a synchronized view of the specified sorted map.
	 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
	 */
	public static Double2FloatSortedMap synchronize(final Double2FloatSortedMap m, final Object sync) { return new SynchronizedSortedMap (m, sync); }
	/** An unmodifiable wrapper class for sorted maps. */
	public static class UnmodifiableSortedMap extends Double2FloatMaps.UnmodifiableMap implements Double2FloatSortedMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Double2FloatSortedMap sortedMap;
	 protected UnmodifiableSortedMap(final Double2FloatSortedMap m) {
	  super(m);
	  sortedMap = m;
	 }
	 @Override
	 public DoubleComparator comparator() { return sortedMap.comparator(); }
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 @Override
	 public ObjectSortedSet<Double2FloatMap.Entry > double2FloatEntrySet() { if (entries == null) entries = ObjectSortedSets.unmodifiable((ObjectSortedSet)sortedMap.double2FloatEntrySet()); return (ObjectSortedSet<Double2FloatMap.Entry >)entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ObjectSortedSet<Map.Entry<Double, Float>> entrySet() { return (ObjectSortedSet)double2FloatEntrySet(); }
	 @Override
	 public DoubleSortedSet keySet() { if (keys == null) keys = DoubleSortedSets.unmodifiable(sortedMap.keySet()); return (DoubleSortedSet )keys; }
	 @Override
	 public Double2FloatSortedMap subMap(final double from, final double to) { return new UnmodifiableSortedMap (sortedMap.subMap(from, to)); }
	 @Override
	 public Double2FloatSortedMap headMap(final double to) { return new UnmodifiableSortedMap (sortedMap.headMap(to)); }
	 @Override
	 public Double2FloatSortedMap tailMap(final double from) { return new UnmodifiableSortedMap (sortedMap.tailMap(from)); }
	 @Override
	 public double firstDoubleKey() { return sortedMap.firstDoubleKey(); }
	 @Override
	 public double lastDoubleKey() { return sortedMap.lastDoubleKey(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double firstKey() { return sortedMap.firstKey(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double lastKey() { return sortedMap.lastKey(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap subMap(final Double from, final Double to) { return new UnmodifiableSortedMap (sortedMap.subMap(from, to)); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap headMap(final Double to) { return new UnmodifiableSortedMap (sortedMap.headMap(to)); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double2FloatSortedMap tailMap(final Double from) { return new UnmodifiableSortedMap (sortedMap.tailMap(from)); }
	}
	/** Returns an unmodifiable type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in an unmodifiable sorted map.
	 * @return an unmodifiable view of the specified sorted map.
	 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
	 */
	public static Double2FloatSortedMap unmodifiable(final Double2FloatSortedMap m) { return new UnmodifiableSortedMap (m); }
}
