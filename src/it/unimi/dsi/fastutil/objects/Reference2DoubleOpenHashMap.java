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
package it.unimi.dsi.fastutil.objects;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import static it.unimi.dsi.fastutil.HashCommon.arraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;
import java.util.Map;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
/** A type-specific hash map with a fast, small-footprint implementation.
	*
	* <p>Instances of this class use a hash table to represent a map. The table is
	* filled up to a specified <em>load factor</em>, and then doubled in size to
	* accommodate new entries. If the table is emptied below <em>one fourth</em>
	* of the load factor, it is halved in size; however, the table is never reduced to a
	* size smaller than that at creation time: this approach makes it
	* possible to create maps with a large capacity in which insertions and
	* deletions do not cause immediately rehashing. Moreover, halving is
	* not performed when deleting entries from an iterator, as it would interfere
	* with the iteration process.
	*
	* <p>Note that {@link #clear()} does not modify the hash table size.
	* Rather, a family of {@linkplain #trim() trimming
	* methods} lets you control the size of the table; this is particularly useful
	* if you reuse instances of this class.
	*
	* <p>Entries returned by the type-specific {@link #entrySet()} method implement
	* the suitable type-specific {@link it.unimi.dsi.fastutil.Pair Pair} interface;
	* only values are mutable.
	*
	* @see Hash
	* @see HashCommon
	*/
public class Reference2DoubleOpenHashMap <K> extends AbstractReference2DoubleMap <K> implements java.io.Serializable, Cloneable, Hash {
	private static final long serialVersionUID = 0L;
	private static final boolean ASSERTS = false;
	/** The array of keys. */
	protected transient K[] key;
	/** The array of values. */
	protected transient double[] value;
	/** The mask for wrapping a position counter. */
	protected transient int mask;
	/** Whether this map contains the key zero. */
	protected transient boolean containsNullKey;
	/** The current table size. */
	protected transient int n;
	/** Threshold after which we rehash. It must be the table size times {@link #f}. */
	protected transient int maxFill;
	/** We never resize below this threshold, which is the construction-time {#n}. */
	protected final transient int minN;
	/** Number of entries in the set (including the key zero, if present). */
	protected int size;
	/** The acceptable load factor. */
	protected final float f;
	/** Cached set of entries. */
	protected transient FastEntrySet <K> entries;
	/** Cached set of keys. */
	protected transient ReferenceSet <K> keys;
	/** Cached collection of values. */
	protected transient DoubleCollection values;
	/** Creates a new hash map.
	 *
	 * <p>The actual table size will be the least power of two greater than {@code expected}/{@code f}.
	 *
	 * @param expected the expected number of elements in the hash map.
	 * @param f the load factor.
	 */
	@SuppressWarnings("unchecked")
	public Reference2DoubleOpenHashMap(final int expected, final float f) {
	 if (f <= 0 || f >= 1) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
	 if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
	 this.f = f;
	 minN = n = arraySize(expected, f);
	 mask = n - 1;
	 maxFill = maxFill(n, f);
	 key = (K[]) new Object[n + 1];
	 value = new double[n + 1];
	}
	/** Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 *
	 * @param expected the expected number of elements in the hash map.
	 */
	public Reference2DoubleOpenHashMap(final int expected) {
	 this(expected, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash map with initial expected {@link Hash#DEFAULT_INITIAL_SIZE} entries
	 * and {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 */
	public Reference2DoubleOpenHashMap() {
	 this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash map copying a given one.
	 *
	 * @param m a {@link Map} to be copied into the new hash map.
	 * @param f the load factor.
	 */
	public Reference2DoubleOpenHashMap(final Map<? extends K, ? extends Double> m, final float f) {
	 this(m.size(), f);
	 putAll(m);
	}
	/** Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given one.
	 *
	 * @param m a {@link Map} to be copied into the new hash map.
	 */
	public Reference2DoubleOpenHashMap(final Map<? extends K, ? extends Double> m) {
	 this(m, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash map copying a given type-specific one.
	 *
	 * @param m a type-specific map to be copied into the new hash map.
	 * @param f the load factor.
	 */
	public Reference2DoubleOpenHashMap(final Reference2DoubleMap <K> m, final float f) {
	 this(m.size(), f);
	 putAll(m);
	}
	/** Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given type-specific one.
	 *
	 * @param m a type-specific map to be copied into the new hash map.
	 */
	public Reference2DoubleOpenHashMap(final Reference2DoubleMap <K> m) {
	 this(m, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash map using the elements of two parallel arrays.
	 *
	 * @param k the array of keys of the new hash map.
	 * @param v the array of corresponding values in the new hash map.
	 * @param f the load factor.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Reference2DoubleOpenHashMap(final K[] k, final double[] v, final float f) {
	 this(k.length, f);
	 if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
	 for(int i = 0; i < k.length; i++) this.put(k[i], v[i]);
	}
	/** Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using the elements of two parallel arrays.
	 *
	 * @param k the array of keys of the new hash map.
	 * @param v the array of corresponding values in the new hash map.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Reference2DoubleOpenHashMap(final K[] k, final double[] v) {
	 this(k, v, DEFAULT_LOAD_FACTOR);
	}
	private int realSize() {
	 return containsNullKey ? size - 1 : size;
	}
	private void ensureCapacity(final int capacity) {
	 final int needed = arraySize(capacity, f);
	 if (needed > n) rehash(needed);
	}
	private void tryCapacity(final long capacity) {
	 final int needed = (int)Math.min(1 << 30, Math.max(2, HashCommon.nextPowerOfTwo((long)Math.ceil(capacity / f))));
	 if (needed > n) rehash(needed);
	}
	private double removeEntry(final int pos) {
	 final double oldValue = value[pos];
	 size--;
	 shiftKeys(pos);
	 if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
	 return oldValue;
	}
	private double removeNullEntry() {
	 containsNullKey = false;
	 key[n] = null;
	 final double oldValue = value[n];
	 size--;
	 if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
	 return oldValue;
	}
	@Override
	public void putAll(Map<? extends K,? extends Double> m) {
	 if (f <= .5) ensureCapacity(m.size()); // The resulting map will be sized for m.size() elements
	 else tryCapacity(size() + m.size()); // The resulting map will be tentatively sized for size() + m.size() elements
	 super.putAll(m);
	}
	@SuppressWarnings("unchecked")
	private int find(final K k) {
	 if (( (k) == (null) )) return containsNullKey ? n : -(n + 1);
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return -(pos + 1);
	 if (( (k) == (curr) )) return pos;
	 // There's always an unused entry.
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return -(pos + 1);
	  if (( (k) == (curr) )) return pos;
	 }
	}
	private void insert(final int pos, final K k, final double v) {
	 if (pos == n) containsNullKey = true;
	 key[pos] = k;
	 value[pos] = v;
	 if (size++ >= maxFill) rehash(arraySize(size + 1, f));
	 if (ASSERTS) checkTable();
	}
	@Override
	public double put(final K k, final double v) {
	 final int pos = find(k);
	 if (pos < 0) {
	  insert(-pos - 1, k, v);
	  return defRetValue;
	 }
	 final double oldValue = value[pos];
	 value[pos] = v;
	 return oldValue;
	}
	private double addToValue(final int pos, final double incr) {
	 final double oldValue = value[pos];
	 value[pos] = oldValue + incr;
	 return oldValue;
	}
	/** Adds an increment to value currently associated with a key.
	 *
	 * <p>Note that this method respects the {@linkplain #defaultReturnValue() default return value} semantics: when
	 * called with a key that does not currently appears in the map, the key
	 * will be associated with the default return value plus
	 * the given increment.
	 *
	 * @param k the key.
	 * @param incr the increment.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 */
	public double addTo(final K k, final double incr) {
	 int pos;
	 if (( (k) == (null) )) {
	  if (containsNullKey) return addToValue(n, incr);
	  pos = n;
	  containsNullKey = true;
	 }
	 else {
	  K curr;
	  final K[] key = this.key;
	  // The starting point.
	  if (! ( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) {
	   if (( (curr) == (k) )) return addToValue(pos, incr);
	   while(! ( (curr = key[pos = (pos + 1) & mask]) == (null) ))
	    if (( (curr) == (k) )) return addToValue(pos, incr);
	  }
	 }
	 key[pos] = k;
	 value[pos] = defRetValue + incr;
	 if (size++ >= maxFill) rehash(arraySize(size + 1, f));
	 if (ASSERTS) checkTable();
	 return defRetValue;
	}
	/** Shifts left entries with the specified hash code, starting at the specified position,
	 * and empties the resulting free entry.
	 *
	 * @param pos a starting position.
	 */
	protected final void shiftKeys(int pos) {
	 // Shift entries with the same hash.
	 int last, slot;
	 K curr;
	 final K[] key = this.key;
	 for(;;) {
	  pos = ((last = pos) + 1) & mask;
	  for(;;) {
	   if (( (curr = key[pos]) == (null) )) {
	    key[last] = (null);
	    return;
	   }
	   slot = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(curr) ) ) & mask;
	   if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
	   pos = (pos + 1) & mask;
	  }
	  key[last] = curr;
	  value[last] = value[pos];
	 }
	}
	@Override
	@SuppressWarnings("unchecked")
	public double removeDouble(final Object k) {
	 if (( ((K) k) == (null) )) {
	  if (containsNullKey) return removeNullEntry();
	  return defRetValue;
	 }
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return defRetValue;
	 if (( (k) == (curr) )) return removeEntry(pos);
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return defRetValue;
	  if (( (k) == (curr) )) return removeEntry(pos);
	 }
	}
	@Override
	@SuppressWarnings("unchecked")
	public double getDouble(final Object k) {
	 if (( ((K) k) == (null) )) return containsNullKey ? value[n] : defRetValue;
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return defRetValue;
	 if (( (k) == (curr) )) return value[pos];
	 // There's always an unused entry.
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return defRetValue;
	  if (( (k) == (curr) )) return value[pos];
	 }
	}
	@Override
	@SuppressWarnings("unchecked")
	public boolean containsKey(final Object k) {
	 if (( ((K) k) == (null) )) return containsNullKey;
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return false;
	 if (( (k) == (curr) )) return true;
	 // There's always an unused entry.
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return false;
	  if (( (k) == (curr) )) return true;
	 }
	}
	@Override
	public boolean containsValue(final double v) {
	 final double value[] = this.value;
	 final K key[] = this.key;
	 if (containsNullKey && ( Double.doubleToLongBits(value[n]) == Double.doubleToLongBits(v) )) return true;
	 for(int i = n; i-- != 0;) if (! ( (key[i]) == (null) ) && ( Double.doubleToLongBits(value[i]) == Double.doubleToLongBits(v) )) return true;
	 return false;
	}
	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public double getOrDefault(final Object k, final double defaultValue) {
	 if (( ((K) k) == (null) )) return containsNullKey ? value[n] : defaultValue;
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return defaultValue;
	 if (( (k) == (curr) )) return value[pos];
	 // There's always an unused entry.
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return defaultValue;
	  if (( (k) == (curr) )) return value[pos];
	 }
	}
	/** {@inheritDoc} */
	@Override
	public double putIfAbsent(final K k, final double v) {
	 final int pos = find(k);
	 if (pos >= 0) return value[pos];
	 insert(-pos - 1, k, v);
	 return defRetValue;
	}
	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(final Object k, final double v) {
	 if (( ((K) k) == (null) )) {
	  if (containsNullKey && ( Double.doubleToLongBits(v) == Double.doubleToLongBits(value[n]) )) {
	   removeNullEntry();
	   return true;
	  }
	  return false;
	 }
	 K curr;
	 final K[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return false;
	 if (( (k) == (curr) ) && ( Double.doubleToLongBits(v) == Double.doubleToLongBits(value[pos]) )) {
	  removeEntry(pos);
	  return true;
	 }
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return false;
	  if (( (k) == (curr) ) && ( Double.doubleToLongBits(v) == Double.doubleToLongBits(value[pos]) )) {
	   removeEntry(pos);
	   return true;
	  }
	 }
	}
	/** {@inheritDoc} */
	@Override
	public boolean replace(final K k, final double oldValue, final double v) {
	 final int pos = find(k);
	 if (pos < 0 || ! ( Double.doubleToLongBits(oldValue) == Double.doubleToLongBits(value[pos]) )) return false;
	 value[pos] = v;
	 return true;
	}
	/** {@inheritDoc} */
	@Override
	public double replace(final K k, final double v) {
	 final int pos = find(k);
	 if (pos < 0) return defRetValue;
	 final double oldValue = value[pos];
	 value[pos] = v;
	 return oldValue;
	}
	/** {@inheritDoc} */
	@Override
	public double computeIfAbsent(final K k, final java.util.function.ToDoubleFunction <? super K> mappingFunction) {
	 java.util.Objects.requireNonNull(mappingFunction);
	 final int pos = find(k);
	 if (pos >= 0) return value[pos];
	 final double newValue = mappingFunction.applyAsDouble(k);
	 insert(-pos -1, k, newValue);
	 return newValue;
	}
	/** {@inheritDoc} */
	@Override
	public double computeIfAbsent(final K key, final Reference2DoubleFunction <? super K> mappingFunction) {
	 java.util.Objects.requireNonNull(mappingFunction);
	 final int pos = find(key);
	 if (pos >= 0) return value[pos];
	 if (!mappingFunction.containsKey(key)) return defRetValue;
	 final double newValue = mappingFunction.getDouble(key);
	 insert(-pos -1, key, newValue);
	 return newValue;
	}
	/** {@inheritDoc} */
	@Override
	public double computeDoubleIfPresent(final K k, final java.util.function.BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
	 java.util.Objects.requireNonNull(remappingFunction);
	 final int pos = find(k);
	 if (pos < 0) return defRetValue;
	 final Double newValue = remappingFunction.apply((k), Double.valueOf(value[pos]));
	 if (newValue == null) {
	  if (( (k) == (null) )) removeNullEntry();
	  else removeEntry(pos);
	  return defRetValue;
	 }
	 return value[pos] = (newValue).doubleValue();
	}
	/** {@inheritDoc} */
	@Override
	public double computeDouble(final K k, final java.util.function.BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
	 java.util.Objects.requireNonNull(remappingFunction);
	 final int pos = find(k);
	 final Double newValue = remappingFunction.apply((k), pos >= 0 ? Double.valueOf(value[pos]) : null);
	 if (newValue == null) {
	  if (pos >= 0) {
	   if (( (k) == (null) )) removeNullEntry();
	   else removeEntry(pos);
	  }
	  return defRetValue;
	 }
	 double newVal = (newValue).doubleValue();
	 if (pos < 0) {
	  insert(-pos - 1, k, newVal);
	  return newVal;
	 }
	 return value[pos] = newVal;
	}
	/** {@inheritDoc} */
	@Override
	public double merge(final K k, final double v, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
	 java.util.Objects.requireNonNull(remappingFunction);
	
	 final int pos = find(k);
	 if (pos < 0) {
	  if (pos < 0) insert(-pos - 1, k, v);
	  else value[pos] = v;
	  return v;
	 }
	 final Double newValue = remappingFunction.apply(Double.valueOf(value[pos]), Double.valueOf(v));
	 if (newValue == null) {
	  if (( (k) == (null) )) removeNullEntry();
	  else removeEntry(pos);
	  return defRetValue;
	 }
	 return value[pos] = (newValue).doubleValue();
	}
	/* Removes all elements from this map.
	 *
	 * <p>To increase object reuse, this method does not change the table size.
	 * If you want to reduce the table size, you must use {@link #trim()}.
	 *
	 */
	@Override
	public void clear() {
	 if (size == 0) return;
	 size = 0;
	 containsNullKey = false;
	 Arrays.fill(key, (null));
	}
	@Override
	public int size() {
	 return size;
	}
	@Override
	public boolean isEmpty() {
	 return size == 0;
	}
	/** The entry class for a hash map does not record key and value, but
	 * rather the position in the hash table of the corresponding entry. This
	 * is necessary so that calls to {@link java.util.Map.Entry#setValue(Object)} are reflected in
	 * the map */
	final class MapEntry implements Reference2DoubleMap.Entry <K>, Map.Entry<K, Double>, ReferenceDoublePair <K> {
	 // The table index this entry refers to, or -1 if this entry has been deleted.
	 int index;
	 MapEntry(final int index) {
	  this.index = index;
	 }
	 MapEntry() {}
	 @Override
	 public K getKey() {
	     return key[index];
	 }
	 @Override
	 public K left() {
	     return key[index];
	 }
	 @Override
	 public double getDoubleValue() {
	  return value[index];
	 }
	 @Override
	 public double rightDouble() {
	  return value[index];
	 }
	 @Override
	 public double setValue(final double v) {
	  final double oldValue = value[index];
	  value[index] = v;
	  return oldValue;
	 }
	 @Override
	 public ReferenceDoublePair <K> right(final double v) {
	  value[index] = v;
	  return this;
	 }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double getValue() {
	  return Double.valueOf(value[index]);
	 }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Double setValue(final Double v) {
	  return Double.valueOf(setValue((v).doubleValue()));
	 }
	 @SuppressWarnings("unchecked")
	 @Override
	 public boolean equals(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  Map.Entry<K, Double> e = (Map.Entry<K, Double>)o;
	  return ( (key[index]) == ((e.getKey())) ) && ( Double.doubleToLongBits(value[index]) == Double.doubleToLongBits((e.getValue()).doubleValue()) );
	 }
	 @Override
	 public int hashCode() {
	  return ( System.identityHashCode(key[index]) ) ^ it.unimi.dsi.fastutil.HashCommon.double2int(value[index]);
	 }
	 @Override
	 public String toString() {
	  return key[index] + "=>" + value[index];
	 }
	}
	/** An iterator over a hash map. */
	private abstract class MapIterator<ConsumerType> {
	 /** The index of the last entry returned, if positive or zero; initially, {@link #n}. If negative, the last
			entry returned was that of the key of index {@code - pos - 1} from the {@link #wrapped} list. */
	 int pos = n;
	 /** The index of the last entry that has been returned (more precisely, the value of {@link #pos} if {@link #pos} is positive,
			or {@link Integer#MIN_VALUE} if {@link #pos} is negative). It is -1 if either
			we did not return an entry yet, or the last returned entry has been removed. */
	 int last = -1;
	 /** A downward counter measuring how many entries must still be returned. */
	 int c = size;
	 /** A boolean telling us whether we should return the entry with the null key. */
	 boolean mustReturnNullKey = Reference2DoubleOpenHashMap.this.containsNullKey;
	 /** A lazily allocated list containing keys of entries that have wrapped around the table because of removals. */
	 ReferenceArrayList <K> wrapped;
	 @SuppressWarnings("unused")
	 abstract void acceptOnIndex(final ConsumerType action, final int index);
	 public boolean hasNext() {
	  return c != 0;
	 }
	 public int nextEntry() {
	  if (! hasNext()) throw new NoSuchElementException();
	  c--;
	  if (mustReturnNullKey) {
	   mustReturnNullKey = false;
	   return last = n;
	  }
	  final K key[] = Reference2DoubleOpenHashMap.this.key;
	  for(;;) {
	   if (--pos < 0) {
	    // We are just enumerating elements from the wrapped list.
	    last = Integer.MIN_VALUE;
	    final K k = wrapped.get(- pos - 1);
	    int p = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask;
	    while (! ( (k) == (key[p]) )) p = (p + 1) & mask;
	    return p;
	   }
	   if (! ( (key[pos]) == (null) )) return last = pos;
	  }
	 }
	 public void forEachRemaining(final ConsumerType action) {
	  if (mustReturnNullKey) {
	   mustReturnNullKey = false;
	   acceptOnIndex(action, last = n);
	   c--;
	  }
	  final K key[] = Reference2DoubleOpenHashMap.this.key;
	  while (c != 0) {
	   if (--pos < 0) {
	    // We are just enumerating elements from the wrapped list.
	    last = Integer.MIN_VALUE;
	    final K k = wrapped.get(- pos - 1);
	    int p = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask;
	    while (! ( (k) == (key[p]) )) p = (p + 1) & mask;
	    acceptOnIndex(action, p);
	    c--;
	   } else if (! ( (key[pos]) == (null) )) {
	    acceptOnIndex(action, last = pos);
	    c--;
	   }
	  }
	 }
	 /** Shifts left entries with the specified hash code, starting at the specified position,
		 * and empties the resulting free entry.
		 *
		 * @param pos a starting position.
		 */
	 private void shiftKeys(int pos) {
	  // Shift entries with the same hash.
	  int last, slot;
	  K curr;
	  final K[] key = Reference2DoubleOpenHashMap.this.key;
	  for(;;) {
	   pos = ((last = pos) + 1) & mask;
	   for(;;) {
	    if (( (curr = key[pos]) == (null) )) {
	     key[last] = (null);
	     return;
	    }
	    slot = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(curr) ) ) & mask;
	    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
	    pos = (pos + 1) & mask;
	   }
	   if (pos < last) { // Wrapped entry.
	    if (wrapped == null) wrapped = new ReferenceArrayList <>(2);
	    wrapped.add(key[pos]);
	   }
	   key[last] = curr;
	   value[last] = value[pos];
	  }
	 }
	 public void remove() {
	  if (last == -1) throw new IllegalStateException();
	  if (last == n) {
	   containsNullKey = false;
	   key[n] = null;
	  }
	  else if (pos >= 0) shiftKeys(last);
	  else {
	   // We're removing wrapped entries.
	   Reference2DoubleOpenHashMap.this.removeDouble(wrapped.set(- pos - 1, null));
	   last = -1; // Note that we must not decrement size
	   return;
	  }
	  size--;
	  last = -1; // You can no longer remove this entry.
	  if (ASSERTS) checkTable();
	 }
	 public int skip(final int n) {
	  int i = n;
	  while(i-- != 0 && hasNext()) nextEntry();
	  return n - i - 1;
	 }
	}
	private final class EntryIterator extends MapIterator<Consumer<? super Reference2DoubleMap.Entry <K> >> implements ObjectIterator<Reference2DoubleMap.Entry <K> > {
	 private MapEntry entry;
	 @Override
	 public MapEntry next() {
	  return entry = new MapEntry(nextEntry());
	 }
	 // forEachRemaining inherited from MapIterator superclass.
	 @Override
	 final void acceptOnIndex(final Consumer<? super Reference2DoubleMap.Entry <K> > action, final int index) {
	  action.accept(entry = new MapEntry(index));
	 }
	 @Override
	 public void remove() {
	  super.remove();
	  entry.index = -1; // You cannot use a deleted entry.
	 }
	}
	private final class FastEntryIterator extends MapIterator<Consumer<? super Reference2DoubleMap.Entry <K> >> implements ObjectIterator<Reference2DoubleMap.Entry <K> > {
	 private final MapEntry entry = new MapEntry();
	 @Override
	 public MapEntry next() {
	  entry.index = nextEntry();
	  return entry;
	 }
	 // forEachRemaining inherited from MapIterator superclass.
	 @Override
	 final void acceptOnIndex(final Consumer<? super Reference2DoubleMap.Entry <K> > action, final int index) {
	  entry.index = index;
	  action.accept(entry);
	 }
	}
	private abstract class MapSpliterator<ConsumerType, SplitType extends MapSpliterator<ConsumerType, SplitType>> {
	 /** The index (which bucket) of the next item to give to the action.
		 * Unlike {@link SetIterator}, this counts up instead of down.
		 */
	 int pos = 0;
	 /** The maximum bucket (exclusive) to iterate to */
	 int max = n;
	 /** An upwards counter counting how many we have given */
	 int c = 0;
	 /** A boolean telling us whether we should return the null key. */
	 boolean mustReturnNull = Reference2DoubleOpenHashMap.this.containsNullKey;
	 boolean hasSplit = false;
	 MapSpliterator() {}
	 MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
	  this.pos = pos;
	  this.max = max;
	  this.mustReturnNull = mustReturnNull;
	  this.hasSplit = hasSplit;
	 }
	 abstract void acceptOnIndex(final ConsumerType action, final int index);
	 abstract SplitType makeForSplit(int pos, int max, boolean mustReturnNull);
	 public boolean tryAdvance(final ConsumerType action) {
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   ++c;
	   acceptOnIndex(action, n);
	   return true;
	  }
	  final K key[] = Reference2DoubleOpenHashMap.this.key;
	  while (pos < max) {
	   if (! ( (key[pos]) == (null) )) {
	    ++c;
	    acceptOnIndex(action, pos++);
	    return true;
	   }
	   ++pos;
	  }
	  return false;
	 }
	 public void forEachRemaining(final ConsumerType action) {
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   ++c;
	   acceptOnIndex(action, n);
	  }
	  final K key[] = Reference2DoubleOpenHashMap.this.key;
	  while (pos < max) {
	   if (! ( (key[pos]) == (null) )) {
	    acceptOnIndex(action, pos);
	    ++c;
	   }
	   ++pos;
	  }
	 }
	 public long estimateSize() {
	  if (!hasSplit) {
	   // Root spliterator; we know how many are remaining.
	   return size - c;
	  } else {
	   // After we split, we can no longer know exactly how many we have (or at least not efficiently).
	   // (size / n) * (max - pos) aka currentTableDensity * numberOfBucketsLeft seems like a good estimate.
	   return Math.min(size - c, (long)(((double)realSize() / n) * (max - pos)) + (mustReturnNull ? 1 : 0));
	  }
	 }
	 public SplitType trySplit() {
	  if (pos >= max - 1) return null;
	  int retLen = (max - pos) >> 1;
	  if (retLen <= 1) return null;
	  int myNewPos = pos + retLen;
	  int retPos = pos;
	  int retMax = myNewPos;
	  // Since null is returned first, and the convention is that the returned split is the prefix of elements,
	  // the split will take care of returning null (if needed), and we won't return it anymore.
	  SplitType split = makeForSplit(retPos, retMax, mustReturnNull);
	  this.pos = myNewPos;
	  this.mustReturnNull = false;
	  this.hasSplit = true;
	  return split;
	 }
	 public long skip(long n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n == 0) return 0;
	  long skipped = 0;
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   ++skipped;
	   --n;
	  }
	  final K key[] = Reference2DoubleOpenHashMap.this.key;
	  while (pos < max && n > 0) {
	   if (! ( (key[pos++]) == (null) )) {
	    ++skipped;
	    --n;
	   }
	  }
	  return skipped;
	 }
	}
	private final class EntrySpliterator extends MapSpliterator<Consumer<? super Reference2DoubleMap.Entry <K> >, EntrySpliterator> implements ObjectSpliterator<Reference2DoubleMap.Entry <K> > {
	 private static final int POST_SPLIT_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
	 EntrySpliterator() {}
	 EntrySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
	  super(pos, max, mustReturnNull, hasSplit);
	 }
	 @Override
	 public int characteristics() {
	  return hasSplit ? POST_SPLIT_CHARACTERISTICS : ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
	 }
	 @Override
	 final void acceptOnIndex(final Consumer<? super Reference2DoubleMap.Entry <K> > action, final int index) {
	  action.accept(new MapEntry(index));
	 }
	 @Override
	 final EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
	  return new EntrySpliterator(pos, max, mustReturnNull, true);
	 }
	}
	private final class MapEntrySet extends AbstractObjectSet<Reference2DoubleMap.Entry <K> > implements FastEntrySet <K> {
	 @Override
	 public ObjectIterator<Reference2DoubleMap.Entry <K> > iterator() { return new EntryIterator(); }
	 @Override
	 public ObjectIterator<Reference2DoubleMap.Entry <K> > fastIterator() { return new FastEntryIterator(); }
	 @Override
	 public ObjectSpliterator<Reference2DoubleMap.Entry <K> > spliterator() { return new EntrySpliterator(); }
	 // 
	 @Override
	 @SuppressWarnings("unchecked")
	 public boolean contains(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	  if (e.getValue() == null || ! (e.getValue() instanceof Double)) return false;
	  final K k = ((K) e.getKey());
	  final double v = ((Double)( e.getValue())).doubleValue();
	  if (( (k) == (null) )) return Reference2DoubleOpenHashMap.this.containsNullKey && ( Double.doubleToLongBits(value[n]) == Double.doubleToLongBits(v) );
	  K curr;
	  final K[] key = Reference2DoubleOpenHashMap.this.key;
	  int pos;
	  // The starting point.
	  if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return false;
	  if (( (k) == (curr) )) return ( Double.doubleToLongBits(value[pos]) == Double.doubleToLongBits(v) );
	  // There's always an unused entry.
	  while(true) {
	   if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return false;
	   if (( (k) == (curr) )) return ( Double.doubleToLongBits(value[pos]) == Double.doubleToLongBits(v) );
	  }
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public boolean remove(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	  if (e.getValue() == null || ! (e.getValue() instanceof Double)) return false;
	  final K k = ((K) e.getKey());
	  final double v = ((Double)( e.getValue())).doubleValue();
	  if (( (k) == (null) )) {
	   if (containsNullKey && ( Double.doubleToLongBits(value[n]) == Double.doubleToLongBits(v) )) {
	    removeNullEntry();
	    return true;
	   }
	   return false;
	  }
	  K curr;
	  final K[] key = Reference2DoubleOpenHashMap.this.key;
	  int pos;
	  // The starting point.
	  if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask]) == (null) )) return false;
	  if (( (curr) == (k) )) {
	   if (( Double.doubleToLongBits(value[pos]) == Double.doubleToLongBits(v) )) {
	    removeEntry(pos);
	    return true;
	   }
	   return false;
	  }
	  while(true) {
	   if (( (curr = key[pos = (pos + 1) & mask]) == (null) )) return false;
	   if (( (curr) == (k) )) {
	    if (( Double.doubleToLongBits(value[pos]) == Double.doubleToLongBits(v) )) {
	     removeEntry(pos);
	     return true;
	    }
	   }
	  }
	 }
	 @Override
	 public int size() {
	  return size;
	 }
	 @Override
	 public void clear() {
	  Reference2DoubleOpenHashMap.this.clear();
	 }
	 /** {@inheritDoc} */
	 @Override
	 public void forEach(final Consumer<? super Reference2DoubleMap.Entry <K> > consumer) {
	  if (containsNullKey) consumer.accept(new AbstractReference2DoubleMap.BasicEntry <K>(key[n], value[n]));
	  for(int pos = n; pos-- != 0;)
	   if (! ( (key[pos]) == (null) )) consumer.accept(new AbstractReference2DoubleMap.BasicEntry <K>(key[pos], value[pos]));
	 }
	 /** {@inheritDoc} */
	 @Override
	 public void fastForEach(final Consumer<? super Reference2DoubleMap.Entry <K> > consumer) {
	  final AbstractReference2DoubleMap.BasicEntry <K> entry = new AbstractReference2DoubleMap.BasicEntry <>();
	  if (containsNullKey) {
	   entry.key = key[n];
	   entry.value = value[n];
	   consumer.accept(entry);
	  }
	  for(int pos = n; pos-- != 0;)
	   if (! ( (key[pos]) == (null) )) {
	    entry.key = key[pos];
	    entry.value = value[pos];
	    consumer.accept(entry);
	   }
	 }
	}
	@Override
	public FastEntrySet <K> reference2DoubleEntrySet() {
	 if (entries == null) entries = new MapEntrySet();
	 return entries;
	}
	/** An iterator on keys.
	 *
	 * <p>We simply override the {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods
	 * (and possibly their type-specific counterparts) so that they return keys
	 * instead of entries.
	 */
	private final class KeyIterator extends MapIterator<Consumer <? super K> > implements ObjectIterator <K> {
	 public KeyIterator() { super(); }
	 // forEachRemaining inherited from MapIterator superclass.
	 // Despite the superclass declared with generics, the way Java inherits and generates bridge methods avoids the boxing/unboxing
	 @Override
	 final void acceptOnIndex(final Consumer <? super K> action, final int index) {
	  action.accept(key[index]);
	 }
	 @Override
	 public K next() { return key[nextEntry()]; }
	}
	private final class KeySpliterator extends MapSpliterator<Consumer <? super K>, KeySpliterator> implements ObjectSpliterator <K> {
	 private static final int POST_SPLIT_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
	 KeySpliterator() {}
	 KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
	  super(pos, max, mustReturnNull, hasSplit);
	 }
	 @Override
	 public int characteristics() {
	  return hasSplit ? POST_SPLIT_CHARACTERISTICS : ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
	 }
	 @Override
	 final void acceptOnIndex(final Consumer <? super K> action, final int index) {
	  action.accept(key[index]);
	 }
	 @Override
	 final KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
	  return new KeySpliterator(pos, max, mustReturnNull, true);
	 }
	}
	private final class KeySet extends AbstractReferenceSet <K> {
	 @Override
	 public ObjectIterator <K> iterator() { return new KeyIterator(); }
	 @Override
	 public ObjectSpliterator <K> spliterator() { return new KeySpliterator(); }
	 /** {@inheritDoc} */
	 @Override
	 public void forEach(final Consumer <? super K> consumer) {
	  if (containsNullKey) consumer.accept(key[n]);
	  for(int pos = n; pos-- != 0;) {
	   final K k = key[pos];
	   if (! ( (k) == (null) )) consumer.accept(k);
	  }
	 }
	 @Override
	 public int size() { return size; }
	 @Override
	 public boolean contains(Object k) { return containsKey(k); }
	 @Override
	 public boolean remove(Object k) {
	  final int oldSize = size;
	  Reference2DoubleOpenHashMap.this.removeDouble(k);
	  return size != oldSize;
	 }
	 @Override
	 public void clear() { Reference2DoubleOpenHashMap.this.clear();}
	}
	@Override
	public ReferenceSet <K> keySet() {
	 if (keys == null) keys = new KeySet();
	 return keys;
	}
	/** An iterator on values.
	 *
	 * <p>We simply override the {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods
	 * (and possibly their type-specific counterparts) so that they return values
	 * instead of entries.
	 */
	private final class ValueIterator extends MapIterator<java.util.function.DoubleConsumer> implements DoubleIterator {
	 public ValueIterator() { super(); }
	 // forEachRemaining inherited from MapIterator superclass.
	 // Despite the superclass declared with generics, the way Java inherits and generates bridge methods avoids the boxing/unboxing
	 @Override
	 final void acceptOnIndex(final java.util.function.DoubleConsumer action, final int index) {
	  action.accept(value[index]);
	 }
	 @Override
	 public double nextDouble() { return value[nextEntry()]; }
	}
	private final class ValueSpliterator extends MapSpliterator<java.util.function.DoubleConsumer, ValueSpliterator> implements DoubleSpliterator {
	 private static final int POST_SPLIT_CHARACTERISTICS = DoubleSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
	 ValueSpliterator() {}
	 ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
	  super(pos, max, mustReturnNull, hasSplit);
	 }
	 @Override
	 public int characteristics() {
	  return hasSplit ? POST_SPLIT_CHARACTERISTICS : DoubleSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS;
	 }
	 @Override
	 final void acceptOnIndex(final java.util.function.DoubleConsumer action, final int index) {
	  action.accept(value[index]);
	 }
	 @Override
	 final ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
	  return new ValueSpliterator(pos, max, mustReturnNull, true);
	 }
	}
	@Override
	public DoubleCollection values() {
	 if (values == null) values = new AbstractDoubleCollection () {
	   @Override
	   public DoubleIterator iterator() { return new ValueIterator(); }
	   @Override
	   public DoubleSpliterator spliterator() { return new ValueSpliterator(); }
	   /** {@inheritDoc} */
	   @Override
	   public void forEach(final java.util.function.DoubleConsumer consumer) {
	    if (containsNullKey) consumer.accept(value[n]);
	    for(int pos = n; pos-- != 0;)
	     if (! ( (key[pos]) == (null) )) consumer.accept(value[pos]);
	   }
	   @Override
	   public int size() { return size; }
	   @Override
	   public boolean contains(double v) { return containsValue(v); }
	   @Override
	   public void clear() { Reference2DoubleOpenHashMap.this.clear(); }
	  };
	 return values;
	}
	/** Rehashes the map, making the table as small as possible.
	 *
	 * <p>This method rehashes the table to the smallest size satisfying the
	 * load factor. It can be used when the set will not be changed anymore, so
	 * to optimize access speed and size.
	 *
	 * <p>If the table size is already the minimum possible, this method
	 * does nothing.
	 *
	 * @return true if there was enough memory to trim the map.
	 * @see #trim(int)
	 */
	public boolean trim() {
	 return trim(size);
	}
	/** Rehashes this map if the table is too large.
	 *
	 * <p>Let <var>N</var> be the smallest table size that can hold
	 * <code>max(n,{@link #size()})</code> entries, still satisfying the load factor. If the current
	 * table size is smaller than or equal to <var>N</var>, this method does
	 * nothing. Otherwise, it rehashes this map in a table of size
	 * <var>N</var>.
	 *
	 * <p>This method is useful when reusing maps.  {@linkplain #clear() Clearing a
	 * map} leaves the table size untouched. If you are reusing a map
	 * many times, you can call this method with a typical
	 * size to avoid keeping around a very large table just
	 * because of a few large transient maps.
	 *
	 * @param n the threshold for the trimming.
	 * @return true if there was enough memory to trim the map.
	 * @see #trim()
	 */
	public boolean trim(final int n) {
	 final int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / f));
	 if (l >= this.n || size > maxFill(l, f)) return true;
	 try {
	  rehash(l);
	 }
	 catch(OutOfMemoryError cantDoIt) { return false; }
	 return true;
	}
	/** Rehashes the map.
	 *
	 * <p>This method implements the basic rehashing strategy, and may be
	 * overridden by subclasses implementing different rehashing strategies (e.g.,
	 * disk-based rehashing). However, you should not override this method
	 * unless you understand the internal workings of this class.
	 *
	 * @param newN the new size
	 */
	@SuppressWarnings("unchecked")
	protected void rehash(final int newN) {
	 final K key[] = this.key;
	 final double value[] = this.value;
	 final int mask = newN - 1; // Note that this is used by the hashing macro
	 final K newKey[] = (K[]) new Object[newN + 1];
	 final double newValue[] = new double[newN + 1];
	 int i = n, pos;
	 for(int j = realSize(); j-- != 0;) {
	  while(( (key[--i]) == (null) ));
	  if (! ( (newKey[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(key[i]) ) ) & mask]) == (null) ))
	   while (! ( (newKey[pos = (pos + 1) & mask]) == (null) ));
	  newKey[pos] = key[i];
	  newValue[pos] = value[i];
	 }
	 newValue[newN] = value[n];
	 n = newN;
	 this.mask = mask;
	 maxFill = maxFill(n, f);
	 this.key = newKey;
	 this.value = newValue;
	}
	/** Returns a deep copy of this map.
	 *
	 * <p>This method performs a deep copy of this hash map; the data stored in the
	 * map, however, is not cloned. Note that this makes a difference only for object keys.
	 *
	 *  @return a deep copy of this map.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Reference2DoubleOpenHashMap <K> clone() {
	 Reference2DoubleOpenHashMap <K> c;
	 try {
	  c = (Reference2DoubleOpenHashMap <K>)super.clone();
	 }
	 catch(CloneNotSupportedException cantHappen) {
	  throw new InternalError();
	 }
	 c.keys = null;
	 c.values = null;
	 c.entries = null;
	 c.containsNullKey = containsNullKey;
	 c.key = key.clone();
	 c.value = value.clone();
	 return c;
	}
	/** Returns a hash code for this map.
	 *
	 * This method overrides the generic method provided by the superclass.
	 * Since {@code equals()} is not overriden, it is important
	 * that the value returned by this method is the same value as
	 * the one returned by the overriden method.
	 *
	 * @return a hash code for this map.
	 */
	@Override
	public int hashCode() {
	 int h = 0;
	 for(int j = realSize(), i = 0, t = 0; j-- != 0;) {
	  while(( (key[i]) == (null) )) i++;
	  if (this != key[i])
	   t = ( System.identityHashCode(key[i]) );
	   t ^= it.unimi.dsi.fastutil.HashCommon.double2int(value[i]);
	  h += t;
	  i++;
	 }
	 // Zero / null keys have hash zero.
	 if (containsNullKey) h += it.unimi.dsi.fastutil.HashCommon.double2int(value[n]);
	 return h;
	}
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	 final K key[] = this.key;
	 final double value[] = this.value;
	 final EntryIterator i = new EntryIterator();
	 s.defaultWriteObject();
	 for(int j = size, e; j-- != 0;) {
	  e = i.nextEntry();
	  s.writeObject(key[e]);
	  s.writeDouble(value[e]);
	 }
	}
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
	 s.defaultReadObject();
	 n = arraySize(size, f);
	 maxFill = maxFill(n, f);
	 mask = n - 1;
	 final K key[] = this.key = (K[]) new Object[n + 1];
	 final double value[] = this.value = new double[n + 1];
	 K k;
	 double v;
	 for(int i = size, pos; i-- != 0;) {
	  k = (K) s.readObject();
	  v = s.readDouble();
	  if (( (k) == (null) )) {
	   pos = n;
	   containsNullKey = true;
	  }
	  else {
	   pos = ( it.unimi.dsi.fastutil.HashCommon.mix( System.identityHashCode(k) ) ) & mask;
	   while (! ( (key[pos]) == (null) )) pos = (pos + 1) & mask;
	  }
	  key[pos] = k;
	  value[pos] = v;
	 }
	 if (ASSERTS) checkTable();
	}
	private void checkTable() {}
}
