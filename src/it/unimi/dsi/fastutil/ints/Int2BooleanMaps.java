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
package it.unimi.dsi.fastutil.ints;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollections;
import it.unimi.dsi.fastutil.booleans.BooleanSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap.FastEntrySet;
/** A class providing static methods and objects that do useful things with type-specific maps.
	*
	* @see java.util.Collections
	*/
public final class Int2BooleanMaps {
	private Int2BooleanMaps() {}
	/** Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Int2BooleanMap.Entry > fastIterator(Int2BooleanMap map) {
	 final ObjectSet<Int2BooleanMap.Entry > entries = map.int2BooleanEntrySet();
	 return entries instanceof Int2BooleanMap.FastEntrySet ? ((Int2BooleanMap.FastEntrySet ) entries).fastIterator() : entries.iterator();
	}
	/** Iterates {@linkplain FastEntrySet#fastForEach(Consumer) quickly}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map on which we will try to iterate {@linkplain FastEntrySet#fastForEach(Consumer) quickly}.
	 * @param consumer the consumer that will be passed to  {@link FastEntrySet#fastForEach(Consumer)}, if possible, or to {@link Iterable#forEach(Consumer)}.
	 * @since 8.1.0
	 */

	public static void fastForEach(Int2BooleanMap map, final Consumer<? super Int2BooleanMap.Entry > consumer) {
	 final ObjectSet<Int2BooleanMap.Entry > entries = map.int2BooleanEntrySet();
	 if (entries instanceof Int2BooleanMap.FastEntrySet) ((Int2BooleanMap.FastEntrySet ) entries).fastForEach(consumer);
	 else entries.forEach(consumer);
	}
	/** Returns an iterable yielding an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * @param map a map from which we will try to extract an iterable yielding a (fast) iterator on the entry set.
	 * @return an iterable  yielding an iterator on the entry set of the given map that will be
	 * fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterable<Int2BooleanMap.Entry > fastIterable(Int2BooleanMap map) {
	 final ObjectSet<Int2BooleanMap.Entry > entries = map.int2BooleanEntrySet();
	 return entries instanceof Int2BooleanMap.FastEntrySet ? new ObjectIterable<Int2BooleanMap.Entry >() {
	  @Override
	  public ObjectIterator<Int2BooleanMap.Entry > iterator() { return ((Int2BooleanMap.FastEntrySet )entries).fastIterator(); }
	  @Override
	  public ObjectSpliterator<Int2BooleanMap.Entry > spliterator() { return entries.spliterator(); }
	  @Override
	  public void forEach(final Consumer<? super Int2BooleanMap.Entry > consumer) { ((Int2BooleanMap.FastEntrySet )entries).fastForEach(consumer); }
	 } : entries;
	}
	/** An immutable class representing an empty type-specific map.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific map.
	 */
	public static class EmptyMap extends Int2BooleanFunctions.EmptyFunction implements Int2BooleanMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected EmptyMap() {}
	 @Override
	 public boolean containsValue(final boolean v) { return false; }
	 @Deprecated
	 @Override
	 public Boolean getOrDefault(final Object key, final Boolean defaultValue) { return defaultValue; }
	 @Override
	 public boolean getOrDefault(final int key, final boolean defaultValue) { return defaultValue; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return false; }
	 @Override
	 public void putAll(final Map<? extends Integer, ? extends Boolean> m) { throw new UnsupportedOperationException(); }
	 @Override
	 public ObjectSet<Int2BooleanMap.Entry > int2BooleanEntrySet() { return ObjectSets.EMPTY_SET; }
	
	 @Override
	 public IntSet keySet() { return IntSets.EMPTY_SET; }
	
	 @Override
	 public BooleanCollection values() { return BooleanSets.EMPTY_SET; }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Boolean> consumer) { }
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
	public static class Singleton extends Int2BooleanFunctions.Singleton implements Int2BooleanMap , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected transient ObjectSet<Int2BooleanMap.Entry > entries;
	 protected transient IntSet keys;
	 protected transient BooleanCollection values;
	 protected Singleton(final int key, final boolean value) {
	  super(key, value);
	 }
	 @Override
	 public boolean containsValue(final boolean v) { return ( (value) == (v) ); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return ( (((Boolean)(ov)).booleanValue()) == (value) ); }
	 @Override
	 public void putAll(final Map<? extends Integer, ? extends Boolean> m) { throw new UnsupportedOperationException(); }
	 @Override
	 public ObjectSet<Int2BooleanMap.Entry > int2BooleanEntrySet() { if (entries == null) entries = ObjectSets.singleton(new AbstractInt2BooleanMap.BasicEntry (key, value)); return entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	 public ObjectSet<Map.Entry<Integer, Boolean>> entrySet() { return (ObjectSet)int2BooleanEntrySet(); }
	 @Override
	 public IntSet keySet() { if (keys == null) keys = IntSets.singleton(key); return keys; }
	 @Override
	 public BooleanCollection values() { if (values == null) values = BooleanSets.singleton(value); return values; }
	 @Override
	 public boolean isEmpty() { return false; }
	 @Override
	 public int hashCode() { return (key) ^ (value ? 1231 : 1237); }
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
	public static Int2BooleanMap singleton(final int key, boolean value) { return new Singleton (key, value); }
	/** Returns a type-specific immutable map containing only the specified pair. The returned map is serializable and cloneable.
	 *
	 * <p>Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned map.
	 * @param value the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Int2BooleanMap singleton(final Integer key, final Boolean value) { return new Singleton ((key).intValue(), (value).booleanValue()); }
	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Int2BooleanFunctions.SynchronizedFunction implements Int2BooleanMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Int2BooleanMap map;
	 protected transient ObjectSet<Int2BooleanMap.Entry > entries;
	 protected transient IntSet keys;
	 protected transient BooleanCollection values;
	 protected SynchronizedMap(final Int2BooleanMap m, final Object sync) {
	  super(m, sync);
	  this.map = m;
	 }
	 protected SynchronizedMap(final Int2BooleanMap m) {
	  super(m);
	  this.map = m;
	 }
	 @Override
	 public boolean containsValue(final boolean v) { synchronized(sync) { return map.containsValue(v); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { synchronized(sync) { return map.containsValue(ov); } }
	 @Override
	 public void putAll(final Map<? extends Integer, ? extends Boolean> m) { synchronized(sync) { map.putAll(m); } }
	 @Override
	 public ObjectSet<Int2BooleanMap.Entry > int2BooleanEntrySet() { synchronized(sync) { if (entries == null) entries = ObjectSets.synchronize(map.int2BooleanEntrySet(), sync); return entries; } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public ObjectSet<Map.Entry<Integer, Boolean>> entrySet() { return (ObjectSet)int2BooleanEntrySet(); }
	 @Override
	 public IntSet keySet() {
	  synchronized(sync) { if (keys == null) keys = IntSets.synchronize(map.keySet(), sync); return keys; }
	 }
	 @Override
	 public BooleanCollection values() {
	  synchronized(sync) { if (values == null) values = BooleanCollections.synchronize(map.values(), sync); return values; }
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
	 public boolean getOrDefault(final int key, final boolean defaultValue) { synchronized(sync) { return map.getOrDefault(key, defaultValue); } }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Boolean> action) { synchronized (sync) { map.forEach(action); } }
	 @Override
	 public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> function) { synchronized (sync) { map.replaceAll(function); } }
	 @Override
	 public boolean putIfAbsent(final int key, final boolean value) { synchronized(sync) { return map.putIfAbsent(key, value); } }
	 @Override
	 public boolean remove(final int key, final boolean value) { synchronized(sync) { return map.remove(key, value); } }
	 @Override
	 public boolean replace(final int key, final boolean value) { synchronized(sync) { return map.replace(key, value); } }
	 @Override
	 public boolean replace(final int key, final boolean oldValue, final boolean newValue) { synchronized(sync) { return map.replace(key, oldValue, newValue); } }
	 @Override
	 public boolean computeIfAbsent(final int key, final java.util.function.IntPredicate mappingFunction) { synchronized(sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 @Override
	 public boolean computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Boolean> mappingFunction) { synchronized(sync) { return map.computeIfAbsentNullable(key, mappingFunction); } }
	 @Override
	 public boolean computeIfAbsent(final int key, final Int2BooleanFunction mappingFunction) { synchronized(sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 @Override
	 public boolean computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
	  synchronized (sync) { return map.computeIfPresent(key, remappingFunction); }
	 }
	 @Override
	 public boolean compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
	  synchronized (sync) { return map.compute(key, remappingFunction); }
	 }
	 @Override
	 public boolean merge(final int key, final boolean value, final java.util.function.BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
	  synchronized (sync) { return map.merge(key, value, remappingFunction); }
	 }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean getOrDefault(final Object key, final Boolean defaultValue) { synchronized (sync) { return map.getOrDefault(key, defaultValue); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean remove(final Object key, final Object value) { synchronized (sync) { return map.remove(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean replace(final Integer key, final Boolean value) { synchronized (sync) { return map.replace(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean replace(final Integer key, final Boolean oldValue, final Boolean newValue) { synchronized (sync) { return map.replace(key, oldValue, newValue); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean putIfAbsent(final Integer key, final Boolean value) { synchronized (sync) { return map.putIfAbsent(key, value); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Boolean> mappingFunction) { synchronized (sync) { return map.computeIfAbsent(key, mappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { synchronized (sync) { return map.computeIfPresent(key, remappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { synchronized (sync) { return map.compute(key, remappingFunction); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean merge(final Integer key, final Boolean value, final java.util.function.BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) { synchronized (sync) { return map.merge(key, value, remappingFunction); } }
	}
	/** Returns a synchronized type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Int2BooleanMap synchronize(final Int2BooleanMap m) { return new SynchronizedMap (m); }
	/** Returns a synchronized type-specific map backed by the given type-specific map, using an assigned object to synchronize.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @param sync an object that will be used to synchronize the access to the map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Int2BooleanMap synchronize(final Int2BooleanMap m, final Object sync) { return new SynchronizedMap (m, sync); }
	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Int2BooleanFunctions.UnmodifiableFunction implements Int2BooleanMap , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final Int2BooleanMap map;
	 protected transient ObjectSet<Int2BooleanMap.Entry > entries;
	 protected transient IntSet keys;
	 protected transient BooleanCollection values;
	 protected UnmodifiableMap(final Int2BooleanMap m) {
	  super(m);
	  this.map = m;
	 }
	 @Override
	 public boolean containsValue(final boolean v) { return map.containsValue(v); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean containsValue(final Object ov) { return map.containsValue(ov); }
	 @Override
	 public void putAll(final Map<? extends Integer, ? extends Boolean> m) { throw new UnsupportedOperationException(); }
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override
	 public ObjectSet<Int2BooleanMap.Entry > int2BooleanEntrySet() { if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.int2BooleanEntrySet()); return entries; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 @Override
	 public ObjectSet<Map.Entry<Integer, Boolean>> entrySet() { return (ObjectSet)int2BooleanEntrySet(); }
	 @Override
	 public IntSet keySet() { if (keys == null) keys = IntSets.unmodifiable(map.keySet()); return keys; }
	 @Override
	 public BooleanCollection values() { if (values == null) values = BooleanCollections.unmodifiable(map.values()); return values; }
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
	 public boolean getOrDefault(final int key, final boolean defaultValue) { return map.getOrDefault(key, defaultValue); }
	 @Override
	 public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Boolean> action) { map.forEach(action); }
	 @Override
	 public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> function) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean putIfAbsent(final int key, final boolean value) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean remove(final int key, final boolean value) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean replace(final int key, final boolean value) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean replace(final int key, final boolean oldValue, final boolean newValue) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean computeIfAbsent(final int key, final java.util.function.IntPredicate mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Boolean> mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean computeIfAbsent(final int key, final Int2BooleanFunction mappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean merge(final int key, final boolean value, final java.util.function.BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean getOrDefault(final Object key, final Boolean defaultValue) { return map.getOrDefault(key, defaultValue); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean remove(final Object key, final Object value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean replace(final Integer key, final Boolean value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean replace(final Integer key, final Boolean oldValue, final Boolean newValue) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean putIfAbsent(final Integer key, final Boolean value) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Boolean> mappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Boolean merge(final Integer key, final Boolean value, final java.util.function.BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) { throw new UnsupportedOperationException(); }
	}
	/** Returns an unmodifiable type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in an unmodifiable map.
	 * @return an unmodifiable view of the specified map.
	 * @see java.util.Collections#unmodifiableMap(Map)
	 */
	public static Int2BooleanMap unmodifiable(final Int2BooleanMap m) { return new UnmodifiableMap (m); }
}
