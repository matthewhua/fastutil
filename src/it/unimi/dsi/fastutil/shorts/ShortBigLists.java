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
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigArrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
/** A class providing static methods and objects that do useful things with type-specific big lists.
	*
	* @see java.util.Collections
	* @see it.unimi.dsi.fastutil.BigList
	*/
public final class ShortBigLists {
	private ShortBigLists() {}
	/** Shuffles the specified big list using the specified pseudorandom number generator.
	 *
	 * @param l the big list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static ShortBigList shuffle(final ShortBigList l, final Random random) {
	 for(long i = l.size64(); i-- != 0;) {
	  final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
	  final short t = l.getShort(i);
	  l.set(i, l.getShort(p));
	  l.set(p, t);
	 }
	 return l;
	}
	/** An immutable class representing an empty type-specific big list.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific list.
	 */
	public static class EmptyBigList extends ShortCollections.EmptyCollection implements ShortBigList , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected EmptyBigList() {}
	 @Override
	 public short getShort(long i) { throw new IndexOutOfBoundsException(); }
	 @Override
	 public boolean rem(short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public short removeShort(long i) { throw new UnsupportedOperationException(); }
	 @Override
	 public void add(final long index, final short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public short set(final long index, final short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public long indexOf(short k) { return -1; }
	 @Override
	 public long lastIndexOf(short k) { return -1; }
	 @Override
	 public boolean addAll(long i, Collection<? extends Short> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(ShortBigList c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(long i, ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(long i, ShortBigList c) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public void add(final long index, final Short k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public boolean add(final Short k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short get(long i) { throw new IndexOutOfBoundsException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short set(final long index, final Short k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short remove(long k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long indexOf(Object k) { return -1; }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long lastIndexOf(Object k) { return -1; }
	 @Override
	
	 public ShortBigListIterator listIterator() { return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR; }
	 @Override
	
	 public ShortBigListIterator iterator() { return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR; }
	 @Override
	
	 public ShortBigListIterator listIterator(long i) { if (i == 0) return ShortBigListIterators.EMPTY_BIG_LIST_ITERATOR; throw new IndexOutOfBoundsException(String.valueOf(i)); }
	 @Override
	
	 public ShortSpliterator spliterator() { return ShortSpliterators.EMPTY_SPLITERATOR; }
	 @Override
	 public ShortBigList subList(long from, long to) { if (from == 0 && to == 0) return this; throw new IndexOutOfBoundsException(); }
	 @Override
	 public void getElements(long from, short[][] a, long offset, long length) { BigArrays.ensureOffsetLength(a, offset, length); if (from != 0) throw new IndexOutOfBoundsException(); }
	 @Override
	 public void removeElements(long from, long to) { throw new UnsupportedOperationException(); }
	 @Override
	 public void addElements(long index, final short a[][], long offset, long length) { throw new UnsupportedOperationException(); }
	 @Override
	 public void addElements(long index, final short a[][]) { throw new UnsupportedOperationException(); }
	 @Override
	 public void size(long s) { throw new UnsupportedOperationException(); }
	 @Override
	 public long size64() { return 0; }
	 @Override
	 public int compareTo(final BigList<? extends Short> o) {
	  if (o == this) return 0;
	  return ((BigList<?>)o).isEmpty() ? 0 : -1;
	 }
	 @Override
	 public Object clone() { return EMPTY_BIG_LIST; }
	 @Override
	 public int hashCode() { return 1; }
	 @Override
	 @SuppressWarnings("rawtypes")
	 public boolean equals(Object o) { return o instanceof BigList && ((BigList)o).isEmpty(); }
	 @Override
	 public String toString() { return "[]"; }
	 private Object readResolve() { return EMPTY_BIG_LIST; }
	}
	/** An empty big list (immutable). It is serializable and cloneable.
	 */

	public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();
	/** An immutable class representing a type-specific singleton big list.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific big list.
	 */
	public static class Singleton extends AbstractShortBigList implements java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 private final short element;
	 protected Singleton(final short element) { this.element = element; }
	 @Override
	 public short getShort(final long i) { if (i == 0) return element; throw new IndexOutOfBoundsException(); }
	 @Override
	 public boolean rem(short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public short removeShort(final long i) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean contains(final short k) { return ( (k) == (element) ); }
	 @Override
	 public long indexOf(final short k) { return ( (k) == (element) ) ? 0 : -1; }
	 /* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
	 @Override
	 public short[] toShortArray() {
	  short a[] = new short[1];
	  a[0] = element;
	  return a;
	 }
	 @Override
	 public ShortBigListIterator listIterator() { return ShortBigListIterators.singleton(element); }
	 @Override
	 public ShortBigListIterator listIterator(long i) {
	  if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
	  ShortBigListIterator l = listIterator();
	  if (i == 1) l.nextShort();
	  return l;
	 }
	 @Override
	 public ShortSpliterator spliterator() { return ShortSpliterators.singleton(element); }
	 @Override
	
	 public ShortBigList subList(final long from, final long to) {
	  ensureIndex(from);
	  ensureIndex(to);
	  if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
	  if (from != 0 || to != 1) return EMPTY_BIG_LIST;
	  return this;
	 }
	 @Override
	 public boolean addAll(long i, Collection<? extends Short> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(final Collection<? extends Short> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean removeAll(final Collection<?> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean retainAll(final Collection<?> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(ShortBigList c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(long i, ShortBigList c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(long i, ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(final ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean removeAll(final ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean retainAll(final ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public void clear() { throw new UnsupportedOperationException(); }
	 @Override
	 public long size64() { return 1; }
	 @Override
	 public Object clone() { return this; }
	}
	/** Returns a type-specific immutable big list containing only the specified element. The returned big list is serializable and cloneable.
	 *
	 * @param element the only element of the returned big list.
	 * @return a type-specific immutable big list containing just {@code element}.
	 */
	public static ShortBigList singleton(final short element) { return new Singleton (element); }
	/** Returns a type-specific immutable big list containing only the specified element. The returned big list is serializable and cloneable.
	 *
	 * @param element the only element of the returned big list.
	 * @return a type-specific immutable big list containing just {@code element}.
	 */
	public static ShortBigList singleton(final Object element) { return new Singleton (((Short)(element)).shortValue()); }
	/** A synchronized wrapper class for big lists. */
	public static class SynchronizedBigList extends ShortCollections.SynchronizedCollection implements ShortBigList , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final ShortBigList list; // Due to the large number of methods that are not in COLLECTION, this is worth caching.
	 protected SynchronizedBigList(final ShortBigList l, final Object sync) {
	  super(l, sync);
	  this.list = l;
	 }
	 protected SynchronizedBigList(final ShortBigList l) {
	  super(l);
	  this.list = l;
	 }
	 @Override
	 public short getShort(final long i) { synchronized(sync) { return list.getShort(i); } }
	 @Override
	 public short set(final long i, final short k) { synchronized(sync) { return list.set(i, k); } }
	 @Override
	 public void add(final long i, final short k) { synchronized(sync) { list.add(i, k); } }
	 @Override
	 public short removeShort(final long i) { synchronized(sync) { return list.removeShort(i); } }
	 @Override
	 public long indexOf(final short k) { synchronized(sync) { return list.indexOf(k); } }
	 @Override
	 public long lastIndexOf(final short k) { synchronized(sync) { return list.lastIndexOf(k); } }
	 @Override
	 public boolean addAll(final long index, final Collection<? extends Short> c) { synchronized(sync) { return list.addAll(index, c); } }
	 @Override
	 public void getElements(final long from, final short a[][], final long offset, final long length) { synchronized(sync) { list.getElements(from, a, offset, length); } }
	 @Override
	 public void removeElements(final long from, final long to) { synchronized(sync) { list.removeElements(from, to); } }
	 @Override
	 public void addElements(long index, final short a[][], long offset, long length) { synchronized(sync) { list.addElements(index, a, offset, length); } }
	 @Override
	 public void addElements(long index, final short a[][]) { synchronized(sync) { list.addElements(index, a); } }
	 /* {@inheritDoc}
		 * @deprecated Use {@link #size64()} instead.
		 */
	 @Deprecated
	 @Override
	 public void size(final long size) { synchronized(sync) { list.size(size); } }
	 @Override
	 public long size64() { synchronized(sync) { return list.size64(); } }
	 @Override
	 public ShortBigListIterator iterator() { return list.listIterator(); }
	 @Override
	 public ShortBigListIterator listIterator() { return list.listIterator(); }
	 @Override
	 public ShortBigListIterator listIterator(final long i) { return list.listIterator(i); }
	 @Override
	 public ShortBigList subList(final long from, final long to) { synchronized(sync) { return synchronize(list.subList(from, to), sync); } }
	 @Override
	 public boolean equals(final Object o) { if (o == this) return true; synchronized(sync) { return list.equals(o); } }
	 @Override
	 public int hashCode() { synchronized(sync) { return list.hashCode(); } }
	 @Override
	 public int compareTo(final BigList<? extends Short> o) { synchronized(sync) { return list.compareTo(o); } }
	 @Override
	 public boolean addAll(final long index, final ShortCollection c) { synchronized(sync) { return list.addAll(index, c); } }
	 @Override
	 public boolean addAll(final long index, ShortBigList l) { synchronized(sync) { return list.addAll(index, l); } }
	 @Override
	 public boolean addAll(ShortBigList l) { synchronized(sync) { return list.addAll(l); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public void add(final long i, Short k) { synchronized(sync) { list.add(i, k); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short get(final long i) { synchronized(sync) { return list.get(i); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short set(final long index, Short k) { synchronized(sync) { return list.set(index, k); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short remove(final long i) { synchronized(sync) { return list.remove(i); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long indexOf(final Object o) { synchronized(sync) { return list.indexOf(o); } }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long lastIndexOf(final Object o) { synchronized(sync) { return list.lastIndexOf(o); } }
	}
	/** Returns a synchronized type-specific big list backed by the given type-specific big list.
	 *
	 * @param l the big list to be wrapped in a synchronized big list.
	 * @return a synchronized view of the specified big list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static ShortBigList synchronize(final ShortBigList l) { return new SynchronizedBigList (l); }
	/** Returns a synchronized type-specific big list backed by the given type-specific big list, using an assigned object to synchronize.
	 *
	 * @param l the big list to be wrapped in a synchronized big list.
	 * @param sync an object that will be used to synchronize the access to the big list.
	 * @return a synchronized view of the specified big list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static ShortBigList synchronize(final ShortBigList l, final Object sync) { return new SynchronizedBigList (l, sync); }
	/** An unmodifiable wrapper class for big lists. */
	public static class UnmodifiableBigList extends ShortCollections.UnmodifiableCollection implements ShortBigList , java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected final ShortBigList list; // Due to the large number of methods that are not in COLLECTION, this is worth caching.
	 protected UnmodifiableBigList(final ShortBigList l) {
	  super(l);
	  this.list = l;
	 }
	 @Override
	 public short getShort(final long i) { return list.getShort(i); }
	 @Override
	 public short set(final long i, final short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public void add(final long i, final short k) { throw new UnsupportedOperationException(); }
	 @Override
	 public short removeShort(final long i) { throw new UnsupportedOperationException(); }
	 @Override
	 public long indexOf(final short k) { return list.indexOf(k); }
	 @Override
	 public long lastIndexOf(final short k) { return list.lastIndexOf(k); }
	 @Override
	 public boolean addAll(final long index, final Collection<? extends Short> c) { throw new UnsupportedOperationException(); }
	 @Override
	 public void getElements(final long from, final short a[][], final long offset, final long length) { list.getElements(from, a, offset, length); }
	 @Override
	 public void removeElements(final long from, final long to) { throw new UnsupportedOperationException(); }
	 @Override
	 public void addElements(long index, final short a[][], long offset, long length) { throw new UnsupportedOperationException(); }
	 @Override
	 public void addElements(long index, final short a[][]) { throw new UnsupportedOperationException(); }
	 /* {@inheritDoc}
		 * @deprecated Use {@link #size64()} instead.
		 */
	 @Deprecated
	 @Override
	 public void size(final long size) { list.size(size); }
	 @Override
	 public long size64() { return list.size64(); }
	 @Override
	 public ShortBigListIterator iterator() { return listIterator(); }
	 @Override
	 public ShortBigListIterator listIterator() { return ShortBigListIterators.unmodifiable(list.listIterator()); }
	 @Override
	 public ShortBigListIterator listIterator(final long i) { return ShortBigListIterators.unmodifiable(list.listIterator(i)); }
	 @Override
	 public ShortBigList subList(final long from, final long to) { return unmodifiable(list.subList(from, to)); }
	 @Override
	 public boolean equals(final Object o) { if (o == this) return true; return list.equals(o); }
	 @Override
	 public int hashCode() { return list.hashCode(); }
	 @Override
	 public int compareTo(final BigList<? extends Short> o) { return list.compareTo(o); }
	 @Override
	 public boolean addAll(final long index, final ShortCollection c) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(final ShortBigList l) { throw new UnsupportedOperationException(); }
	 @Override
	 public boolean addAll(final long index, final ShortBigList l) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short get(final long i) { return list.get(i); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public void add(final long i, Short k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short set(final long index, Short k) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public Short remove(final long i) { throw new UnsupportedOperationException(); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long indexOf(final Object o) { return list.indexOf(o); }
	 /** {@inheritDoc}
		 * @deprecated Please use the corresponding type-specific method instead. */
	 @Deprecated
	 @Override
	 public long lastIndexOf(final Object o) { return list.lastIndexOf(o); }
	}
	/** Returns an unmodifiable type-specific big list backed by the given type-specific big list.
	 *
	 * @param l the big list to be wrapped in an unmodifiable big list.
	 * @return an unmodifiable view of the specified big list.
	 * @see java.util.Collections#unmodifiableList(List)
	 */
	public static ShortBigList unmodifiable(final ShortBigList l) { return new UnmodifiableBigList (l); }
	/** A class exposing a list as a big list. */
	public static class ListBigList extends AbstractShortBigList implements java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 private final ShortList list;
	 protected ListBigList(final ShortList list) {
	  this.list = list;
	 }
	 private int intIndex(long index) {
	  if (index >= Integer.MAX_VALUE) throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
	  return (int)index;
	 }
	 @Override
	 public long size64() { return list.size(); }
	 @Override
	 public void size(final long size) { list.size(intIndex(size)); }
	 @Override
	 public ShortBigListIterator iterator() { return ShortBigListIterators.asBigListIterator(list.iterator()); }
	 @Override
	 public ShortBigListIterator listIterator() { return ShortBigListIterators.asBigListIterator(list.listIterator()); }
	 @Override
	 public ShortBigListIterator listIterator(final long index) { return ShortBigListIterators.asBigListIterator(list.listIterator(intIndex(index))); }
	 @Override
	 public boolean addAll(final long index, final Collection<? extends Short> c) { return list.addAll(intIndex(index), c); }
	 @Override
	 public ShortBigList subList(long from, long to) { return new ListBigList (list.subList(intIndex(from), intIndex(to))); }
	 @Override
	 public boolean contains(final short key) { return list.contains(key); }
	 @Override
	 public short[] toShortArray() { return list.toShortArray(); }
	 @Override
	 public void removeElements(final long from, final long to) { list.removeElements(intIndex(from), intIndex(to)); }
	 /* {@inheritDoc}
		 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be removed in the future.
		 */
	 @Deprecated
	 @Override
	 public short[] toShortArray(short[] a) { return list.toArray(a); }
	 @Override
	 public boolean addAll(long index, ShortCollection c) { return list.addAll(intIndex(index), c); }
	 @Override
	 public boolean addAll(ShortCollection c) { return list.addAll(c); }
	 @Override
	 public boolean addAll(long index, ShortBigList c) { return list.addAll(intIndex(index), c); }
	 @Override
	 public boolean addAll(ShortBigList c) { return list.addAll(c); }
	 @Override
	 public boolean containsAll(ShortCollection c) { return list.containsAll(c); }
	 @Override
	 public boolean removeAll(ShortCollection c) { return list.removeAll(c); }
	 @Override
	 public boolean retainAll(ShortCollection c) { return list.retainAll(c); }
	 @Override
	 public void add(long index, short key) { list.add(intIndex(index), key); }
	 @Override
	 public boolean add(short key) { return list.add(key); }
	 @Override
	 public short getShort(long index) { return list.getShort(intIndex(index)); }
	 @Override
	 public long indexOf(short k) { return list.indexOf(k); }
	 @Override
	 public long lastIndexOf(short k) { return list.lastIndexOf(k); }
	 @Override
	 public short removeShort(long index) { return list.removeShort(intIndex(index)); }
	 @Override
	 public short set(long index, short k) { return list.set(intIndex(index), k); }
	 @Override
	 public boolean isEmpty() { return list.isEmpty(); }
	 @Override
	 public <T> T[] toArray(T[] a) { return list.toArray(a); }
	 @Override
	 public boolean containsAll(Collection<?> c) { return list.containsAll(c); }
	 @Override
	 public boolean addAll(Collection<? extends Short> c) { return list.addAll(c); }
	 @Override
	 public boolean removeAll(Collection<?> c) { return list.removeAll(c); }
	 @Override
	 public boolean retainAll(Collection<?> c) { return list.retainAll(c); }
	 @Override
	 public void clear() { list.clear(); }
	 @Override
	 public int hashCode() { return list.hashCode(); }
	}
	/** Returns a big list backed by the specified list.
	*
	* @param list a list.
	* @return a big list backed by the specified list.
	*/
	public static ShortBigList asBigList(final ShortList list) { return new ListBigList (list); }
}
