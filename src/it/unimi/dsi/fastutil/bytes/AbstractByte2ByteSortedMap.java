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
package it.unimi.dsi.fastutil.bytes;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
/** An abstract class providing basic methods for sorted maps implementing a type-specific interface. */
public abstract class AbstractByte2ByteSortedMap extends AbstractByte2ByteMap implements Byte2ByteSortedMap {
	private static final long serialVersionUID = -1773560792952436569L;
	protected AbstractByte2ByteSortedMap() {}
	/** {@inheritDoc}
	 *
	 * <p>The view is backed by the sorted set returned by {@link java.util.Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would
	 * require adding some attributes that lightweight implementations would
	 * not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a sorted set view of the keys of this map; it may be safely cast to a type-specific interface.
	 */
	@Override
	public ByteSortedSet keySet() {
	 return new KeySet();
	}
	/** A wrapper exhibiting the keys of a map. */
	protected class KeySet extends AbstractByteSortedSet {
	 @Override
	 public boolean contains(final byte k) { return containsKey(k); }
	 @Override
	 public int size() { return AbstractByte2ByteSortedMap.this.size(); }
	 @Override
	 public void clear() { AbstractByte2ByteSortedMap.this.clear(); }
	 @Override
	 public ByteComparator comparator() { return AbstractByte2ByteSortedMap.this.comparator(); }
	 @Override
	 public byte firstByte() { return firstByteKey(); }
	 @Override
	 public byte lastByte() { return lastByteKey(); }
	 @Override
	 public ByteSortedSet headSet(final byte to) { return headMap(to).keySet(); }
	 @Override
	 public ByteSortedSet tailSet(final byte from) { return tailMap(from).keySet(); }
	 @Override
	 public ByteSortedSet subSet(final byte from, final byte to) { return subMap(from, to).keySet(); }
	 @Override
	 public ByteBidirectionalIterator iterator(final byte from) { return new KeySetIterator (byte2ByteEntrySet().iterator(new BasicEntry (from, ((byte)0)))); }
	 @Override
	 public ByteBidirectionalIterator iterator() { return new KeySetIterator (Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this)); }
	}
	/** A wrapper exhibiting a map iterator as an iterator on keys.
	 *
	 * <p>To provide an iterator on keys, just create an instance of this
	 * class using the corresponding iterator on entries.
	 */
	protected static class KeySetIterator implements ByteBidirectionalIterator {
	 protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry > i;
	 public KeySetIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry > i) {
	  this.i = i;
	 }
	 @Override
	 public byte nextByte() { return i.next().getByteKey(); };
	 @Override
	 public byte previousByte() { return i.previous().getByteKey(); };
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public boolean hasPrevious() { return i.hasPrevious(); }
	}
	/** {@inheritDoc}
	 *
	 * <p>The view is backed by the sorted set returned by {@link java.util.Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would
	 * require adding some attributes that lightweight implementations would
	 * not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a type-specific collection view of the values contained in this map.
	 */
	@Override
	public ByteCollection values() {
	 return new ValuesCollection();
	}
	/** A wrapper exhibiting the values of a map. */
	protected class ValuesCollection extends AbstractByteCollection {
	 @Override
	 public ByteIterator iterator() { return new ValuesIterator (Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this)); }
	 @Override
	 public boolean contains(final byte k) { return containsValue(k); }
	 @Override
	 public int size() { return AbstractByte2ByteSortedMap.this.size(); }
	 @Override
	 public void clear() { AbstractByte2ByteSortedMap.this.clear(); }
	}
	/** A wrapper exhibiting a map iterator as an iterator on values.
	 *
	 * <p>To provide an iterator on values, just create an instance of this
	 * class using the corresponding iterator on entries.
	 */
	protected static class ValuesIterator implements ByteIterator {
	 protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry > i;
	 public ValuesIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry > i) {
	  this.i = i;
	 }
	 @Override
	 public byte nextByte() { return i.next().getByteValue(); };
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	}
}
