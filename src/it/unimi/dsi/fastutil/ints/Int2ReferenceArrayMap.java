/*
	* Copyright (C) 2007-2022 Sebastiano Vigna
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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
/** A simple, brute-force implementation of a map based on two parallel backing arrays.
	*
	* <p>The main purpose of this
	* implementation is that of wrapping cleanly the brute-force approach to the storage of a very
	* small number of pairs: just put them into two parallel arrays and scan linearly to find an item.
	*/
public class Int2ReferenceArrayMap <V> extends AbstractInt2ReferenceMap <V> implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/** The keys (valid up to {@link #size}, excluded). */
	private transient int[] key;
	/** The values (parallel to {@link #key}). */
	private transient Object[] value;
	/** The number of valid entries in {@link #key} and {@link #value}. */
	private int size;
	/** Cached set of entries. */
	private transient FastEntrySet <V> entries;
	/** Cached set of keys. */
	private transient IntSet keys;
	/** Cached collection of values. */
	private transient ReferenceCollection <V> values;
	/** Creates a new empty array map with given key and value backing arrays. The resulting map will have as many entries as the given arrays.
	 *
	 * <p>It is responsibility of the caller that the elements of {@code key} are distinct.
	 *
	 * @param key the key array.
	 * @param value the value array (it <em>must</em> have the same length as {@code key}).
	 */
	public Int2ReferenceArrayMap(final int[] key, final Object[] value) {
	 this.key = key;
	 this.value = value;
	 size = key.length;
	 if(key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
	}
	/** Creates a new empty array map.
	 */
	public Int2ReferenceArrayMap() {
	 this.key = IntArrays.EMPTY_ARRAY;
	 this.value = ObjectArrays.EMPTY_ARRAY;
	}
	/** Creates a new empty array map of given capacity.
	 *
	 * @param capacity the initial capacity.
	 */
	public Int2ReferenceArrayMap(final int capacity) {
	 this.key = new int[capacity];
	 this.value = new Object[capacity];
	}
	/** Creates a new empty array map copying the entries of a given map.
	 *
	 * @param m a map.
	 */
	public Int2ReferenceArrayMap(final Int2ReferenceMap <V> m) {
	 this(m.size());
	 int i = 0;
	 for(Int2ReferenceMap.Entry <V> e : m.int2ReferenceEntrySet()) {
	  key[i] = e.getIntKey();
	  value[i] = e.getValue();
	  i++;
	 }
	 size = i;
	}
	/** Creates a new empty array map copying the entries of a given map.
	 *
	 * @param m a map.
	 */
	public Int2ReferenceArrayMap(final Map<? extends Integer, ? extends V> m) {
	 this(m.size());
	 int i = 0;
	 for(Map.Entry<? extends Integer, ? extends V> e : m.entrySet()) {
	  key[i] = (e.getKey()).intValue();
	  value[i] = (e.getValue());
	  i++;
	 }
	 size = i;
	}
	/** Creates a new array map with given key and value backing arrays, using the given number of elements.
	 *
	 * <p>It is responsibility of the caller that the first {@code size} elements of {@code key} are distinct.
	 *
	 * @param key the key array.
	 * @param value the value array (it <em>must</em> have the same length as {@code key}).
	 * @param size the number of valid elements in {@code key} and {@code value}.
	 */
	public Int2ReferenceArrayMap(final int[] key, final Object[] value, final int size) {
	 this.key = key;
	 this.value = value;
	 this.size = size;
	 if(key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
	 if (size > key.length) throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
	}
	private final class EntrySet extends AbstractObjectSet<Int2ReferenceMap.Entry <V> > implements FastEntrySet <V> {
	 // TODO Maybe make this return a list-iterator like the LinkedXHashMaps do? (same for other collection view types)
	 @Override
	 public ObjectIterator<Int2ReferenceMap.Entry <V> > iterator() {
	  return new ObjectIterator<Int2ReferenceMap.Entry <V> >() {
	   int curr = -1, next = 0;
	   @Override
	   public boolean hasNext() { return next < size; }
	   @Override
	   @SuppressWarnings("unchecked")
	   public Entry <V> next() {
	    if (! hasNext()) throw new NoSuchElementException();
	    return new AbstractInt2ReferenceMap.BasicEntry <>( key[curr = next], (V) value[next++]);
	   }
	   @Override
	   public void remove() {
	    if (curr == -1) throw new IllegalStateException();
	    curr = -1;
	    final int tail = size-- - next--;
	    System.arraycopy(key, next + 1, key, next, tail);
	    System.arraycopy(value, next + 1, value, next, tail);
	    value[size] = null;
	   }
	   @Override
	   @SuppressWarnings("unchecked")
	   public void forEachRemaining(final Consumer<? super Int2ReferenceMap.Entry <V> > action) {
	    // Hoist containing class field ref into local
	    final int max = size;
	    while (next < max) {
	     action.accept(new AbstractInt2ReferenceMap.BasicEntry <>( key[curr = next], (V) value[next++]));
	    }
	   }
	  };
	 }
	 @Override
	 public ObjectIterator<Int2ReferenceMap.Entry <V> > fastIterator() {
	  return new ObjectIterator<Int2ReferenceMap.Entry <V> >() {
	   int next = 0, curr = -1;
	   final BasicEntry <V> entry = new BasicEntry <> ();
	   @Override
	   public boolean hasNext() { return next < size; }
	   @Override
	   @SuppressWarnings("unchecked")
	   public Entry <V> next() {
	    if (! hasNext()) throw new NoSuchElementException();
	    entry.key = key[curr = next];
	    entry.value = (V) value[next++];
	    return entry;
	   }
	   @Override
	   public void remove() {
	    if (curr == -1) throw new IllegalStateException();
	    curr = -1;
	    final int tail = size-- - next--;
	    System.arraycopy(key, next + 1, key, next, tail);
	    System.arraycopy(value, next + 1, value, next, tail);
	    value[size] = null;
	   }
	   @Override
	   @SuppressWarnings("unchecked")
	   public void forEachRemaining(final Consumer<? super Int2ReferenceMap.Entry <V> > action) {
	    // Hoist containing class field ref into local
	    final int max = size;
	    while (next < max) {
	     entry.key = key[curr = next];
	     entry.value = (V) value[next++];
	     action.accept(entry);
	    }
	   }
	  };
	 }
	 // We already have to create an Entry object for each iteration, so the overhead from having
	 // skeletal implementations isn't significant.
	 final class EntrySetSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Int2ReferenceMap.Entry <V> >
	   implements ObjectSpliterator<Int2ReferenceMap.Entry <V> > {
	  EntrySetSpliterator(int pos, int maxPos) {
	   super(pos, maxPos);
	  }
	  @Override
	  public int characteristics() {
	   return ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
	  }
	  @Override
	  @SuppressWarnings("unchecked")
	  protected final Int2ReferenceMap.Entry <V> get(int location) {
	   return new AbstractInt2ReferenceMap.BasicEntry <>( key[location], (V) value[location]);
	  }
	  @Override
	  protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
	   return new EntrySetSpliterator(pos, maxPos);
	  }
	 }
	 @Override
	 public ObjectSpliterator<Int2ReferenceMap.Entry <V> > spliterator() {
	  return new EntrySetSpliterator(0, size);
	 }
	 /** {@inheritDoc} */
	 @Override
	 @SuppressWarnings("unchecked")
	 public void forEach(final Consumer<? super Int2ReferenceMap.Entry <V> > action) {
	  // Hoist containing class field ref into local
	  for (int i = 0, max = size; i < max; ++i) {
	   action.accept(new AbstractInt2ReferenceMap.BasicEntry <>( key[i], (V) value[i]));
	  }
	 }
	 /** {@inheritDoc} */
	 @Override
	 @SuppressWarnings("unchecked")
	 public void fastForEach(final Consumer<? super Int2ReferenceMap.Entry <V> > action) {
	  final BasicEntry <V> entry = new BasicEntry <> ();
	  // Hoist containing class field ref into local
	  for (int i = 0, max = size; i < max; ++i) {
	   entry.key = key[i];
	   entry.value = (V) value[i];
	   action.accept(entry);
	  }
	 }
	 @Override
	 public int size() { return size; }
	 @Override
	
	 public boolean contains(Object o) {
	  if (! (o instanceof Map.Entry)) return false;
	  final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	  if (e.getKey() == null || ! (e.getKey() instanceof Integer)) return false;
	  final int k = ((Integer)( e.getKey())).intValue();
	  return Int2ReferenceArrayMap.this.containsKey(k) && ( (Int2ReferenceArrayMap.this.get(k)) == ((e.getValue())) );
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public boolean remove(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	  if (e.getKey() == null || ! (e.getKey() instanceof Integer)) return false;
	  final int k = ((Integer)( e.getKey())).intValue();
	  final V v = ((V) e.getValue());
	  final int oldPos = Int2ReferenceArrayMap.this.findKey(k);
	  if (oldPos == -1 || ! ( (v) == (Int2ReferenceArrayMap.this.value[oldPos]) )) return false;
	  final int tail = size - oldPos - 1;
	  System.arraycopy(Int2ReferenceArrayMap.this.key, oldPos + 1, Int2ReferenceArrayMap.this.key, oldPos, tail);
	  System.arraycopy(Int2ReferenceArrayMap.this.value, oldPos + 1, Int2ReferenceArrayMap.this.value, oldPos, tail);
	  Int2ReferenceArrayMap.this.size--;
	  Int2ReferenceArrayMap.this.value[size] = null;
	  return true;
	 }
	}
	@Override
	public FastEntrySet <V> int2ReferenceEntrySet() {
	 if (entries == null) entries = new EntrySet();
	 return entries;
	}
	private int findKey(final int k) {
	 final int[] key = this.key;
	 for(int i = size; i-- != 0;) if (( (key[i]) == (k) )) return i;
	 return -1;
	}
	@Override
	@SuppressWarnings("unchecked")
	public V get(final int k) {
	 final int[] key = this.key;
	 for(int i = size; i-- != 0;) if (( (key[i]) == (k) )) return (V) value[i];
	 return defRetValue;
	}
	@Override
	public int size() { return size; }
	@Override
	public void clear() {
	 for(int i = size; i-- != 0;) {
	  value[i] = null;
	 }
	 size = 0;
	}
	@Override
	public boolean containsKey(final int k) { return findKey(k) != -1; }
	@Override
	public boolean containsValue(Object v) {
	 for(int i = size; i-- != 0;) if (( (value[i]) == (v) )) return true;
	 return false;
	}
	@Override
	public boolean isEmpty() { return size == 0; }
	@Override
	@SuppressWarnings("unchecked")
	public V put(int k, V v) {
	 final int oldKey = findKey(k);
	 if (oldKey != -1) {
	  final V oldValue = (V) value[oldKey];
	  value[oldKey] = v;
	  return oldValue;
	 }
	 if (size == key.length) {
	  final int[] newKey = new int[size == 0 ? 2 : size * 2];
	  final Object[] newValue = new Object[size == 0 ? 2 : size * 2];
	  for(int i = size; i-- != 0;) {
	   newKey[i] = key[i];
	   newValue[i] = value[i];
	  }
	  key = newKey;
	  value = newValue;
	 }
	 key[size] = k;
	 value[size] = v;
	 size++;
	 return defRetValue;
	}
	@Override
	@SuppressWarnings("unchecked")
	public V remove(final int k) {
	 final int oldPos = findKey(k);
	 if (oldPos == -1) return defRetValue;
	 final V oldValue = (V) value[oldPos];
	 final int tail = size - oldPos - 1;
	 System.arraycopy(key, oldPos + 1, key, oldPos, tail);
	 System.arraycopy(value, oldPos + 1, value, oldPos, tail);
	 size--;
	 value[size] = null;
	 return oldValue;
	}
	private final class KeySet extends AbstractIntSet {
	 @Override
	 public boolean contains(final int k) {
	  return findKey(k) != -1;
	 }
	 @Override
	 public boolean remove(final int k) {
	   final int oldPos = findKey(k);
	   if (oldPos == -1) return false;
	   final int tail = size - oldPos - 1;
	   System.arraycopy(key, oldPos + 1, key, oldPos, tail);
	   System.arraycopy(value, oldPos + 1, value, oldPos, tail);
	   size--;
	  Int2ReferenceArrayMap.this.value[size] = null;
	   return true;
	 }
	 @Override
	 public IntIterator iterator() {
	  return new IntIterator () {
	   int pos = 0;
	   @Override
	   public boolean hasNext() {
	    return pos < size;
	   }
	   @Override
	  
	   public int nextInt() {
	    if (! hasNext()) throw new NoSuchElementException();
	    return key[pos++];
	   }
	   @Override
	   public void remove() {
	    if (pos == 0) throw new IllegalStateException();
	    final int tail = size - pos;
	    System.arraycopy(key, pos, key, pos - 1, tail);
	    System.arraycopy(value, pos, value, pos - 1, tail);
	    size--;
	    pos--;
	    Int2ReferenceArrayMap.this.value[size] = null;
	   }
	   @Override
	  
	   public void forEachRemaining(final java.util.function.IntConsumer action) {
	    // Hoist containing class field ref into local
	    final int max = size;
	    while (pos < max) {
	     action.accept( key[pos++]);
	    }
	   }
	   // TODO either override skip or extend from AbstractIndexBasedIterator.
	  };
	 }
	 final class KeySetSpliterator extends IntSpliterators.EarlyBindingSizeIndexBasedSpliterator
	   implements IntSpliterator {
	  KeySetSpliterator(int pos, int maxPos) {
	   super(pos, maxPos);
	  }
	  @Override
	  public int characteristics() {
	   return IntSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
	  }
	  @Override
	 
	  protected final int get(int location) { return key[location]; }
	  @Override
	  protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
	   return new KeySetSpliterator(pos, maxPos);
	  }
	  @Override
	 
	  public void forEachRemaining(final java.util.function.IntConsumer action) {
	   // Hoist containing class field ref into local
	   final int max = size;
	   while (pos < max) {
	    action.accept( key[pos++]);
	   }
	  }
	 }
	 @Override
	 public IntSpliterator spliterator() {
	  return new KeySetSpliterator(0, size);
	 }
	 @Override
	
	 public void forEach(java.util.function.IntConsumer action) {
	  // Hoist containing class field ref into local
	  for (int i = 0, max = size; i < max; ++i) {
	   action.accept( key[i]);
	  }
	 }
	 @Override
	 public int size() {
	  return size;
	 }
	 @Override
	 public void clear() {
	  Int2ReferenceArrayMap.this.clear();
	 }
	}
	@Override
	public IntSet keySet() {
	 if (keys == null) keys = new KeySet();
	 return keys;
	}
	private final class ValuesCollection extends AbstractReferenceCollection <V> {
	 @Override
	 public boolean contains(final Object v) {
	  return containsValue(v);
	 }
	 @Override
	 public it.unimi.dsi.fastutil.objects.ObjectIterator <V> iterator() {
	  return new it.unimi.dsi.fastutil.objects.ObjectIterator <V>() {
	   int pos = 0;
	   @Override
	   public boolean hasNext() {
	    return pos < size;
	   }
	   @Override
	   @SuppressWarnings("unchecked")
	   public V next() {
	    if (! hasNext()) throw new NoSuchElementException();
	    return (V) value[pos++];
	   }
	   @Override
	   public void remove() {
	    if (pos == 0) throw new IllegalStateException();
	    final int tail = size - pos;
	    System.arraycopy(key, pos, key, pos - 1, tail);
	    System.arraycopy(value, pos, value, pos - 1, tail);
	    size--;
	    pos--;
	    Int2ReferenceArrayMap.this.value[size] = null;
	   }
	   @Override
	   @SuppressWarnings("unchecked")
	   public void forEachRemaining(final Consumer <? super V> action) {
	    // Hoist containing class field ref into local
	    final int max = size;
	    while (pos < max) {
	     action.accept((V) value[pos++]);
	    }
	   }
	   // TODO either override skip or extend from AbstractIndexBasedIterator.
	  };
	 }
	 final class ValuesSpliterator extends it.unimi.dsi.fastutil.objects.ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator <V>
	   implements it.unimi.dsi.fastutil.objects.ObjectSpliterator <V> {
	  ValuesSpliterator(int pos, int maxPos) {
	   super(pos, maxPos);
	  }
	  @Override
	  public int characteristics() {
	   return it.unimi.dsi.fastutil.objects.ObjectSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS
	    | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
	  }
	  @Override
	  @SuppressWarnings("unchecked")
	  protected final V get(int location) { return (V) value[location]; }
	  @Override
	  protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
	   return new ValuesSpliterator(pos, maxPos);
	  }
	  @Override
	  @SuppressWarnings("unchecked")
	  public void forEachRemaining(final Consumer <? super V> action) {
	   // Hoist containing class field ref into local
	   final int max = size;
	   while (pos < max) {
	    action.accept((V) value[pos++]);
	   }
	  }
	 }
	 @Override
	 public it.unimi.dsi.fastutil.objects.ObjectSpliterator <V> spliterator() {
	  return new ValuesSpliterator(0, size);
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public void forEach(Consumer <? super V> action) {
	  // Hoist containing class field ref into local
	  for (int i = 0, max = size; i < max; ++i) {
	   action.accept((V) value[i]);
	  }
	 }
	 @Override
	 public int size() {
	  return size;
	 }
	 @Override
	 public void clear() {
	  Int2ReferenceArrayMap.this.clear();
	 }
	}
	@Override
	public ReferenceCollection <V> values() {
	 if (values == null) values = new ValuesCollection();
	 return values;
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
	public Int2ReferenceArrayMap <V> clone() {
	 Int2ReferenceArrayMap <V> c;
	 try {
	  c = (Int2ReferenceArrayMap <V>)super.clone();
	 }
	 catch(CloneNotSupportedException cantHappen) {
	  throw new InternalError();
	 }
	 c.key = key.clone();
	 c.value = value.clone();
	 c.entries = null;
	 c.keys = null;
	 c.values = null;
	 return c;
	}
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	 s.defaultWriteObject();
	 for(int i = 0, max = size; i < max; i++) {
	  s.writeInt(key[i]);
	  s.writeObject(value[i]);
	 }
	}
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
	 s.defaultReadObject();
	 key = new int[size];
	 value = new Object[size];
	 for(int i = 0; i < size; i++) {
	  key[i] = s.readInt();
	  value[i] = s.readObject();
	 }
	}
}
