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
package it.unimi.dsi.fastutil.chars;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
/** An abstract class providing basic methods for sorted maps implementing a type-specific interface. */
public abstract class AbstractChar2ReferenceSortedMap <V> extends AbstractChar2ReferenceMap <V> implements Char2ReferenceSortedMap <V> {
	private static final long serialVersionUID = -1773560792952436569L;
	protected AbstractChar2ReferenceSortedMap() {}
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
	public CharSortedSet keySet() {
	 return new KeySet();
	}
	/** A wrapper exhibiting the keys of a map. */
	protected class KeySet extends AbstractCharSortedSet {
	 @Override
	 public boolean contains(final char k) { return containsKey(k); }
	 @Override
	 public int size() { return AbstractChar2ReferenceSortedMap.this.size(); }
	 @Override
	 public void clear() { AbstractChar2ReferenceSortedMap.this.clear(); }
	 @Override
	 public CharComparator comparator() { return AbstractChar2ReferenceSortedMap.this.comparator(); }
	 @Override
	 public char firstChar() { return firstCharKey(); }
	 @Override
	 public char lastChar() { return lastCharKey(); }
	 @Override
	 public CharSortedSet headSet(final char to) { return headMap(to).keySet(); }
	 @Override
	 public CharSortedSet tailSet(final char from) { return tailMap(from).keySet(); }
	 @Override
	 public CharSortedSet subSet(final char from, final char to) { return subMap(from, to).keySet(); }
	 @Override
	 public CharBidirectionalIterator iterator(final char from) { return new KeySetIterator <>(char2ReferenceEntrySet().iterator(new BasicEntry <>(from, (null)))); }
	 @Override
	 public CharBidirectionalIterator iterator() { return new KeySetIterator <>(Char2ReferenceSortedMaps.fastIterator(AbstractChar2ReferenceSortedMap.this)); }
	}
	/** A wrapper exhibiting a map iterator as an iterator on keys.
	 *
	 * <p>To provide an iterator on keys, just create an instance of this
	 * class using the corresponding iterator on entries.
	 */
	protected static class KeySetIterator <V> implements CharBidirectionalIterator {
	 protected final ObjectBidirectionalIterator<Char2ReferenceMap.Entry <V> > i;
	 public KeySetIterator(ObjectBidirectionalIterator<Char2ReferenceMap.Entry <V> > i) {
	  this.i = i;
	 }
	 @Override
	 public char nextChar() { return i.next().getCharKey(); };
	 @Override
	 public char previousChar() { return i.previous().getCharKey(); };
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
	public ReferenceCollection <V> values() {
	 return new ValuesCollection();
	}
	/** A wrapper exhibiting the values of a map. */
	protected class ValuesCollection extends AbstractReferenceCollection <V> {
	 @Override
	 public ObjectIterator <V> iterator() { return new ValuesIterator <>(Char2ReferenceSortedMaps.fastIterator(AbstractChar2ReferenceSortedMap.this)); }
	 @Override
	 public boolean contains(final Object k) { return containsValue(k); }
	 @Override
	 public int size() { return AbstractChar2ReferenceSortedMap.this.size(); }
	 @Override
	 public void clear() { AbstractChar2ReferenceSortedMap.this.clear(); }
	}
	/** A wrapper exhibiting a map iterator as an iterator on values.
	 *
	 * <p>To provide an iterator on values, just create an instance of this
	 * class using the corresponding iterator on entries.
	 */
	protected static class ValuesIterator <V> implements ObjectIterator <V> {
	 protected final ObjectBidirectionalIterator<Char2ReferenceMap.Entry <V> > i;
	 public ValuesIterator(ObjectBidirectionalIterator<Char2ReferenceMap.Entry <V> > i) {
	  this.i = i;
	 }
	 @Override
	 public V next() { return i.next().getValue(); };
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	}
}
