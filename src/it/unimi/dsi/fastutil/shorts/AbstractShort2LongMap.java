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
import static it.unimi.dsi.fastutil.Size64.sizeOf;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
import it.unimi.dsi.fastutil.longs.LongSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import java.util.Iterator;
import java.util.Map;
/** An abstract class providing basic methods for maps implementing a type-specific interface.
	*
	* <p>Optional operations just throw an {@link
	* UnsupportedOperationException}. Generic versions of accessors delegate to
	* the corresponding type-specific counterparts following the interface rules
	* (they take care of returning {@code null} on a missing key).
	*
	* <p>As a further help, this class provides a {@link BasicEntry BasicEntry} inner class
	* that implements a type-specific version of {@link java.util.Map.Entry}; it
	* is particularly useful for those classes that do not implement their own
	* entries (e.g., most immutable maps).
	*/
public abstract class AbstractShort2LongMap extends AbstractShort2LongFunction implements Short2LongMap , java.io.Serializable {
	private static final long serialVersionUID = -4940583368468432370L;
	protected AbstractShort2LongMap() {}
	/**
	 * {@inheritDoc}
	 * @implSpec This implementation does a linear search over the entry set, finding an entry that has the key specified.
	 *   <p>If you override {@link #keySet()}, you should probably override this method too
	 *   to take advantage of the (presumably) faster {@linkplain java.util.Set#contains key membership test} your {@link #keySet()} provides.
	 *   <p>If you override this method but not {@link #keySet()}, then the returned key set will take advantage of this method.
	 */
	@Override
	public boolean containsKey(final short k) {
	 final ObjectIterator<Short2LongMap.Entry > i = short2LongEntrySet().iterator();
	 while(i.hasNext())
	  if (i.next().getShortKey() == k)
	   return true;
	 return false;
	}
	/**
	 * {@inheritDoc}
	 * @implSpec This implementation does a linear search over the entry set, finding an entry that has the value specified.
	 *   <p>If you override {@link #values()}, you should probably override this method too
	 *   to take advantage of the (presumably) faster {@linkplain java.util.Collection#contains value membership test} your {@link #values()} provides.
	 *   <p>If you override this method but not {@link #values()}, then the returned values collection will take advantage of this method.
	 */
	@Override
	public boolean containsValue(final long v) {
	 final ObjectIterator<Short2LongMap.Entry > i = short2LongEntrySet().iterator();
	 while(i.hasNext())
	  if (i.next().getLongValue() == v)
	   return true;
	 return false;
	}
	@Override
	public boolean isEmpty() {
	 return size() == 0;
	}
	/** {@inheritDoc} 
	 * @implSpec This method just delegates to the interface default method,
	 * as the default method, but it is final, so it cannot be overridden.
	 */
	@Override
	public final long mergeLong(final short key, final long value, final it.unimi.dsi.fastutil.longs.LongBinaryOperator remappingFunction) {
	 return mergeLong(key, value, (java.util.function.LongBinaryOperator)remappingFunction);
	}
	/** This class provides a basic but complete type-specific entry class for all those maps implementations
	 * that do not have entries on their own (e.g., most immutable maps).
	 *
	 * <p>This class does not implement {@link java.util.Map.Entry#setValue(Object) setValue()}, as the modification
	 * would not be reflected in the base map.
	 */
	public static class BasicEntry implements Short2LongMap.Entry {
	 protected short key;
	 protected long value;
	 public BasicEntry() {}
	 public BasicEntry(final Short key, final Long value) {
	  this.key = (key).shortValue();
	  this.value = (value).longValue();
	 }
	 public BasicEntry(final short key, final long value) {
	  this.key = key;
	  this.value = value;
	 }
	 @Override
	 public short getShortKey() {
	  return key;
	 }
	 @Override
	 public long getLongValue() {
	  return value;
	 }
	 @Override
	 public long setValue(final long value) {
	  throw new UnsupportedOperationException();
	 }
	
	 @Override
	 public boolean equals(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  if (o instanceof Short2LongMap.Entry) {
	   final Short2LongMap.Entry e = (Short2LongMap.Entry ) o;
	   return ( (key) == (e.getShortKey()) ) && ( (value) == (e.getLongValue()) );
	  }
	  final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	  final Object key = e.getKey();
	  if (key == null || !(key instanceof Short)) return false;
	  final Object value = e.getValue();
	  if (value == null || !(value instanceof Long)) return false;
	  return ( (this.key) == (((Short)(key)).shortValue()) ) && ( (this.value) == (((Long)(value)).longValue()) );
	 }
	 @Override
	 public int hashCode() {
	  return (key) ^ it.unimi.dsi.fastutil.HashCommon.long2int(value);
	 }
	 @Override
	 public String toString() {
	  return key + "->" + value;
	 }
	}
	/** This class provides a basic implementation for an Entry set which forwards some queries to the map.
	 */
	public abstract static class BasicEntrySet extends AbstractObjectSet<Entry > {
	 protected final Short2LongMap map;
	 public BasicEntrySet(final Short2LongMap map) {
	  this.map = map;
	 }
	
	 @Override
	 public boolean contains(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  if (o instanceof Short2LongMap.Entry) {
	   final Short2LongMap.Entry e = (Short2LongMap.Entry ) o;
	   final short k = e.getShortKey();
	   return map.containsKey(k) && ( (map.get(k)) == (e.getLongValue()) );
	  }
	  final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
	  final Object key = e.getKey();
	  if (key == null || !(key instanceof Short)) return false;
	  final short k = ((Short)(key)).shortValue();
	  final Object value = e.getValue();
	  if (value == null || !(value instanceof Long)) return false;
	  return map.containsKey(k) && ( (map.get(k)) == (((Long)(value)).longValue()) );
	 }
	
	 @Override
	 public boolean remove(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  if (o instanceof Short2LongMap.Entry) {
	   final Short2LongMap.Entry e = (Short2LongMap.Entry ) o;
	   return map.remove(e.getShortKey(), e.getLongValue());
	  }
	  Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
	  final Object key = e.getKey();
	  if (key == null || !(key instanceof Short)) return false;
	  final short k = ((Short)(key)).shortValue();
	  final Object value = e.getValue();
	  if (value == null || !(value instanceof Long)) return false;
	  final long v = ((Long)(value)).longValue();
	  return map.remove(k, v);
	 }
	 @Override
	 public int size() {
	  return map.size();
	 }
	 @Override
	 public ObjectSpliterator<Entry > spliterator() {
	  return ObjectSpliterators.asSpliterator(
	   iterator(), sizeOf(map), ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
	 }
	}
	/** Returns a type-specific-set view of the keys of this map.
	 *
	 * <p>The view is backed by the set returned by {@link Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would
	 * require adding some attributes that lightweight implementations would
	 * not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a set view of the keys of this map; it may be safely cast to a type-specific interface.
	 */
	@Override
	public ShortSet keySet() {
	 return new AbstractShortSet () {
	   @Override
	   public boolean contains(final short k) { return containsKey(k); }
	   @Override
	   public int size() { return AbstractShort2LongMap.this.size(); }
	   @Override
	   public void clear() { AbstractShort2LongMap.this.clear(); }
	   @Override
	   public ShortIterator iterator() {
	    return new ShortIterator () {
	      private final ObjectIterator<Short2LongMap.Entry > i = Short2LongMaps.fastIterator(AbstractShort2LongMap.this);
	      @Override
	      public short nextShort() { return i.next().getShortKey(); }
	      @Override
	      public boolean hasNext() { return i.hasNext(); }
	      @Override
	      public void remove() { i.remove(); }
	      @Override
	      public void forEachRemaining(final ShortConsumer action) {
	       i.forEachRemaining(entry -> action.accept(entry.getShortKey()));
	      }
	     };
	   }
	   @Override
	   public ShortSpliterator spliterator() {
	    return ShortSpliterators.asSpliterator(
	     iterator(), sizeOf(AbstractShort2LongMap.this), ShortSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
	   }
	  };
	}
	/** Returns a type-specific-set view of the values of this map.
	 *
	 * <p>The view is backed by the set returned by {@link Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would
	 * require adding some attributes that lightweight implementations would
	 * not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a set view of the values of this map; it may be safely cast to a type-specific interface.
	 */
	@Override
	public LongCollection values() {
	 return new AbstractLongCollection () {
	   @Override
	   public boolean contains(final long k) { return containsValue(k); }
	   @Override
	   public int size() { return AbstractShort2LongMap.this.size(); }
	   @Override
	   public void clear() { AbstractShort2LongMap.this.clear(); }
	   @Override
	   public LongIterator iterator() {
	    return new LongIterator () {
	      private final ObjectIterator<Short2LongMap.Entry > i = Short2LongMaps.fastIterator(AbstractShort2LongMap.this);
	      @Override
	      public long nextLong() { return i.next().getLongValue(); }
	      @Override
	      public boolean hasNext() { return i.hasNext(); }
	      @Override
	      public void remove() { i.remove(); }
	      @Override
	      public void forEachRemaining(final java.util.function.LongConsumer action) {
	       i.forEachRemaining(entry -> action.accept(entry.getLongValue()));
	      }
	     };
	   }
	   @Override
	   public LongSpliterator spliterator() {
	    return LongSpliterators.asSpliterator(
	     iterator(), sizeOf(AbstractShort2LongMap.this), LongSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
	   }
	  };
	}
	/** {@inheritDoc} */
	@SuppressWarnings({"unchecked", "deprecation"})
	@Override
	public void putAll(final Map<? extends Short,? extends Long> m) {
	 if (m instanceof Short2LongMap) {
	  ObjectIterator<Short2LongMap.Entry > i = Short2LongMaps.fastIterator((Short2LongMap ) m);
	  while (i.hasNext()) {
	   final Short2LongMap.Entry e = i.next();
	   put(e.getShortKey(), e.getLongValue());
	  }
	 } else {
	  int n = m.size();
	  final Iterator<? extends Map.Entry<? extends Short,? extends Long>> i = m.entrySet().iterator();
	  Map.Entry<? extends Short,? extends Long> e;
	  while (n-- != 0) {
	   e = i.next();
	   put(e.getKey(), e.getValue());
	  }
	 }
	}
	/** Returns a hash code for this map.
	 *
	 * The hash code of a map is computed by summing the hash codes of its entries.
	 *
	 * @return a hash code for this map.
	 */
	@Override
	public int hashCode() {
	 int h = 0, n = size();
	 final ObjectIterator<Short2LongMap.Entry > i = Short2LongMaps.fastIterator(this);
	 while(n-- != 0) h += i.next().hashCode();
	 return h;
	}
	@Override
	public boolean equals(Object o) {
	 if (o == this) return true;
	 if (! (o instanceof Map)) return false;
	 final Map<?,?> m = (Map<?,?>)o;
	 if (m.size() != size()) return false;
	 return short2LongEntrySet().containsAll(m.entrySet());
	}
	@Override
	public String toString() {
	 final StringBuilder s = new StringBuilder();
	 final ObjectIterator<Short2LongMap.Entry > i = Short2LongMaps.fastIterator(this);
	 int n = size();
	 Short2LongMap.Entry e;
	 boolean first = true;
	 s.append("{");
	 while(n-- != 0) {
	  if (first) first = false;
	  else s.append(", ");
	  e = i.next();
	   s.append(String.valueOf(e.getShortKey()));
	  s.append("=>");
	   s.append(String.valueOf(e.getLongValue()));
	 }
	 s.append("}");
	 return s.toString();
	}
}
