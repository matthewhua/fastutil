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
package it.unimi.dsi.fastutil.shorts;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.shorts.Short2DoubleMap.FastEntrySet;
/** A class providing static methods and objects that do useful things with type-specific maps.
	*
	* @see java.util.Collections
	*/
public final class Short2DoubleMaps {
	private Short2DoubleMaps() {}
	/** Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Short2DoubleMap.Entry > fastIterator(Short2DoubleMap map) {
	 final ObjectSet<Short2DoubleMap.Entry > entries = map.short2DoubleEntrySet();
	 return entries instanceof Short2DoubleMap.FastEntrySet ? ((Short2DoubleMap.FastEntrySet ) entries).fastIterator() : entries.iterator();
	}
	/** Iterates {@linkplain FastEntrySet#fastForEach(Consumer) quickly}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map on which we will try to iterate {@linkplain FastEntrySet#fastForEach(Consumer) quickly}.
	 * @param consumer the consumer that will be passed to  {@link FastEntrySet#fastForEach(Consumer)}, if possible, or to {@link Iterable#forEach(Consumer)}.
	 * @since 8.1.0
	 */

	public static void fastForEach(Short2DoubleMap map, final Consumer<? super Short2DoubleMap.Entry > consumer) {
	 final ObjectSet<Short2DoubleMap.Entry > entries = map.short2DoubleEntrySet();
	 if (entries instanceof Short2DoubleMap.FastEntrySet) ((Short2DoubleMap.FastEntrySet ) entries).fastForEach(consumer);
	 else entries.forEach(consumer);
	}
	/** Returns an iterable yielding an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract an iterable yielding a (fast) iterator on the entry set.
	 * @return an iterable  yielding an iterator on the entry set of the given map that will be
	 * fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterable<Short2DoubleMap.Entry > fastIterable(Short2DoubleMap map) {
	 final ObjectSet<Short2DoubleMap.Entry > entries = map.short2DoubleEntrySet();
	 return entries instanceof Short2DoubleMap.FastEntrySet ? new ObjectIterable<Short2DoubleMap.Entry >() {
	  @Override
	  public ObjectIterator<Short2DoubleMap.Entry > iterator() { return ((Short2DoubleMap.FastEntrySet )entries).fastIterator(); }
	  @Override
	  public ObjectSpliterator<Short2DoubleMap.Entry > spliterator() { return entries.spliterator(); }
	  @Override
	  public void forEach(final Consumer<? super Short2DoubleMap.Entry > consumer) { ((Short2DoubleMap.FastEntrySet )entries).fastForEach(consumer); }
	 } : entries;
	}
	/** An immutable class representing an empty type-specific map.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific map.
	 */
	public static class EmptyMap extends Short2DoubleFunctions.EmptyFunction implements Short2DoubleMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected EmptyMap() {}
	 @Override
	 public boolean containsValue(final double v) { return false; }
	 @Deprecated
	 @Override
	 public Double getOrDefault(final Object key, final Double defaultValue) { return defaultValue; }
	 @Override
	 public double getOrDefault(final short key, final double defaultValue) { return defaultValue; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return false; }
	 @Override
	 public void putAll(final Map<? extends Short, ? extends Double> m) { throw new UnsupportedOperationException(); }
	 @Override
	 public ObjectSet<Short2DoubleMap.Entry > short2DoubleEntrySet() { return ObjectSets.EMPTY_SET; }
	
	 @Override
	 public ShortSet keySet() { return ShortSets.EMPTY_SET; }
	
	 @Override
	 public DoubleCollection values() { return DoubleSets.EMPTY_SET; }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Short, ? super Double> consumer) { }
	 @Override
	 public Object clone() { return EMPTY_MAP; }
	 @Override
	 public boolean isEmpty() { return true; }
	 @Override
	 public int hashCode() { return 0; }
	 @Override
	 public boolean equals(final Object o) {
	  if (! (o instanceof Map)) return false;
	  return ((Map<?,?>)o).isEmpty();
	 }
	 @Override
	 public String toString() { return "{}"; }
	}
	/** An empty type-specific map (immutable). It is serializable and cloneable.
	 */

	public static final EmptyMap EMPTY_MAP = new EmptyMap();
	/** An immutable class representing a type-specific singleton map.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific map.
	 */
	public static class Singleton extends Short2DoubleFunctions.Singleton implements Short2DoubleMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected transient ObjectSet<Short2DoubleMap.Entry > entries;
	 protected transient ShortSet keys;
	 protected transient DoubleCollection values;
	 protected Singleton(final short key, final double value) {
	  super(key, value);
	 }
	 @Override
	 public boolean containsValue(final double v) { return ( Double.doubleToLongBits(value) == Double.doubleToLongBits(v) ); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return ( Double.doubleToLongBits(((Double)(ov)).doubleValue()) == Double.doubleToLongBits(value) ); }
	 @Override
	 public void putAll(final Map<? extends Short, ? extends Double> m) { throw new UnsupportedOperationException(); }
	 @Override
	 public ObjectSet<Short2DoubleMap.Entry > short2DoubleEntrySet() { if (entries == null) entries = ObjectSets.singleton(new AbstractShort2DoubleMap.BasicEntry (key, value)); return entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ObjectSet<Map.Entry<Short, Double>> entrySet() { return (ObjectSet)short2DoubleEntrySet(); }
	 @Override
	 public ShortSet keySet() { if (keys == null) keys = ShortSets.singleton(key); return keys; }
	 @Override
	 public DoubleCollection values() { if (values == null) values = DoubleSets.singleton(value); return values; }
	 @Override
	 public boolean isEmpty() { return false; }
	 @Override
	 public int hashCode() { return (key) ^ it.unimi.dsi.fastutil.HashCommon.double2int(value); }
	 @Override
	 public boolean equals(final Object o) {
	  if (o == this) return true;
	  if (! (o instanceof Map)) return false;
	  Map<?,?> m = (Map<?,?>)o;
	  if (m.size() != 1) return false;
	  return m.entrySet().iterator().next().equals(entrySet().iterator().next());
	 }
	 @Override
	 public String toString() { return "{" + key + "=>" + value + "}"; }
	}
	/** Returns a type-specific immutable map containing only the specified pair. The returned map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned map.
	 * @param value the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Short2DoubleMap singleton(final short key, double value) { return new Singleton (key, value); }
	/** Returns a type-specific immutable map containing only the specified pair. The returned map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned map.
	 * @param value the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Short2DoubleMap singleton(final Short key, final Double value) { return new Singleton ((key).shortValue(), (value).doubleValue()); }
	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Short2DoubleFunctions.SynchronizedFunction implements Short2DoubleMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Short2DoubleMap map;
	 protected transient ObjectSet<Short2DoubleMap.Entry > entries;
	 protected transient ShortSet keys;
	 protected transient DoubleCollection values;
	 protected SynchronizedMap(final Short2DoubleMap m, final Object sync) {
	  super(m, sync);
	  this.map = m;
	 }
	 protected SynchronizedMap(final Short2DoubleMap m) {
	  super(m);
	  this.map = m;
	 }
	 @Override
	 public boolean containsValue(final double v) { synchronized(sync) { return map.containsValue(v); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { synchronized(sync) { return map.containsValue(ov); } }
	 @Override
	 public void putAll(final Map<? extends Short, ? extends Double> m) { synchronized(sync) { map.putAll(m); } }
	 @Override
	 public ObjectSet<Short2DoubleMap.Entry > short2DoubleEntrySet() { synchronized(sync) { if (entries == null) entries = ObjectSets.synchronize(map.short2DoubleEntrySet(), sync); return entries; } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public ObjectSet<Map.Entry<Short, Double>> entrySet() { return (ObjectSet)short2DoubleEntrySet(); }
	 @Override
	 public ShortSet keySet() {
	  synchronized(sync) { if (keys == null) keys = ShortSets.synchronize(map.keySet(), sync); return keys; }
	 }
	 @Override
	 public DoubleCollection values() {
	  synchronized(sync) { if (values == null) values = DoubleCollections.synchronize(map.values(), sync); return values; }
	 }
	 @Override
	 public boolean isEmpty() { synchronized(sync) { return map.isEmpty(); } }
	 @Override
	 public int hashCode() { synchronized(sync) { return map.hashCode(); } }
	 @Override
	 public boolean equals(final Object o) {
	  if (o == this) return true;
	  synchronized(sync) { return map.equals(o); }
	 }
	 private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	  synchronized(sync) { s.defaultWriteObject(); }
	 }
	 // Defaultable methods
	 @Override
	 public double getOrDefault(final short key, final double defaultValue) { synchronized(sync) { return map.getOrDefault(key, defaultValue); } }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Short, ? super Double> action) { synchronized (sync) { map.forEach(action); } }
	 @Override
	 public void replaceAll(final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> function) { synchronized (sync) { map.replaceAll(function); } }
	 @Override
	 public double putIfAbsent(final short key, final double value) { synchronized(sync) { return map.putIfAbsent(key, value); } }
	 @Override
	 public boolean remove(final short key, final double value) { synchronized(sync) { return map.remove(key, value); } }
	 @Override
	 public double replace(final short key, final double value) { synchronized(sync) { return map.replace(key, value); } }
	 @Override
	 public boolean replace(final short key, final double oldValue, final double newValue) { synchronized(sync) { return map.replace(key, oldValue, newValue); } }
	 @Override
	 public double computeIfAbsent(final short key, final java.util.function.IntToDoubleFunction mappingFunction) { synchronized(sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 @Override
	 public double computeIfAbsentNullable(final short key, final java.util.function.IntFunction<? extends Double> mappingFunction) { synchronized(sync) { return map.computeIfAbsentNullable(key, mappingFunction); } }
	 @Override
	 public double computeIfAbsent(final short key, final Short2DoubleFunction mappingFunction) { synchronized(sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 @Override
	 public double computeIfPresent(final short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
	  synchronized (sync) { return map.computeIfPresent(key, remappingFunction); }
	 }
	 @Override
	 public double compute(final short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) {
	  synchronized (sync) { return map.compute(key, remappingFunction); }
	 }
	 @Override
	 public double merge(final short key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
	  synchronized (sync) { return map.merge(key, value, remappingFunction); }
	 }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double getOrDefault(final Object key, final Double defaultValue) { synchronized (sync) { return map.getOrDefault(key, defaultValue); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean remove(final Object key, final Object value) { synchronized (sync) { return map.remove(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double replace(final Short key, final Double value) { synchronized (sync) { return map.replace(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean replace(final Short key, final Double oldValue, final Double newValue) { synchronized (sync) { return map.replace(key, oldValue, newValue); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double putIfAbsent(final Short key, final Double value) { synchronized (sync) { return map.putIfAbsent(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double computeIfAbsent(final Short key, final java.util.function.Function<? super Short, ? extends Double> mappingFunction) { synchronized (sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double computeIfPresent(final Short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { synchronized (sync) { return map.computeIfPresent(key, remappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double compute(final Short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { synchronized (sync) { return map.compute(key, remappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double merge(final Short key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) { synchronized (sync) { return map.merge(key, value, remappingFunction); } }
	}
	/** Returns a synchronized type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Short2DoubleMap synchronize(final Short2DoubleMap m) { return new SynchronizedMap (m); }
	/** Returns a synchronized type-specific map backed by the given type-specific map, using an assigned object to synchronize.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @param sync an object that will be used to synchronize the access to the map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Short2DoubleMap synchronize(final Short2DoubleMap m, final Object sync) { return new SynchronizedMap (m, sync); }
	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Short2DoubleFunctions.UnmodifiableFunction implements Short2DoubleMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Short2DoubleMap map;
	 protected transient ObjectSet<Short2DoubleMap.Entry > entries;
	 protected transient ShortSet keys;
	 protected transient DoubleCollection values;
	 protected UnmodifiableMap(final Short2DoubleMap m) {
	  super(m);
	  this.map = m;
	 }
	 @Override
	 public boolean containsValue(final double v) { return map.containsValue(v); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return map.containsValue(ov); }
	 @Override
	 public void putAll(final Map<? extends Short, ? extends Double> m) { throw new UnsupportedOperationException(); }
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override
	 public ObjectSet<Short2DoubleMap.Entry > short2DoubleEntrySet() { if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.short2DoubleEntrySet()); return entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override
	 public ObjectSet<Map.Entry<Short, Double>> entrySet() { return (ObjectSet)short2DoubleEntrySet(); }
	 @Override
	 public ShortSet keySet() { if (keys == null) keys = ShortSets.unmodifiable(map.keySet()); return keys; }
	 @Override
	 public DoubleCollection values() { if (values == null) values = DoubleCollections.unmodifiable(map.values()); return values; }
	 @Override
	 public boolean isEmpty() { return map.isEmpty(); }
	 @Override
	 public int hashCode() { return map.hashCode(); }
	 @Override
	 public boolean equals(final Object o) {
	  if (o == this) return true;
	  return map.equals(o);
	 }
	 // Defaultable methods
	 @Override
	 public double getOrDefault(final short key, final double defaultValue) { return map.getOrDefault(key, defaultValue); }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Short, ? super Double> action) { map.forEach(action); }
	 @Override
	 public void replaceAll(final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> function) { throw new UnsupportedOperationException(); }
	 @Override
	 public double putIfAbsent(final short key, final double value) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean remove(final short key, final double value) { throw new UnsupportedOperationException(); }
	 @Override
	 public double replace(final short key, final double value) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean replace(final short key, final double oldValue, final double newValue) { throw new UnsupportedOperationException(); }
	 @Override
	 public double computeIfAbsent(final short key, final java.util.function.IntToDoubleFunction mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public double computeIfAbsentNullable(final short key, final java.util.function.IntFunction<? extends Double> mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public double computeIfAbsent(final short key, final Short2DoubleFunction mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public double computeIfPresent(final short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public double compute(final short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public double merge(final short key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double getOrDefault(final Object key, final Double defaultValue) { return map.getOrDefault(key, defaultValue); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean remove(final Object key, final Object value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double replace(final Short key, final Double value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean replace(final Short key, final Double oldValue, final Double newValue) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double putIfAbsent(final Short key, final Double value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double computeIfAbsent(final Short key, final java.util.function.Function<? super Short, ? extends Double> mappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double computeIfPresent(final Short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double compute(final Short key, final java.util.function.BiFunction<? super Short, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double merge(final Short key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) { throw new UnsupportedOperationException(); }
	}
	/** Returns an unmodifiable type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in an unmodifiable map.
	 * @return an unmodifiable view of the specified map.
	 * @see java.util.Collections#unmodifiableMap(Map)
	 */
	public static Short2DoubleMap unmodifiable(final Short2DoubleMap m) { return new UnmodifiableMap (m); }
}
