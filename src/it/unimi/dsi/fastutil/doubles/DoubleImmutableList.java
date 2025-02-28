/*
	* Copyright (C) 2020-2022 Sebastiano Vigna
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
import java.util.Collection;
import java.util.RandomAccess;
import java.util.NoSuchElementException;
/** A type-specific array-based immutable list; provides some additional methods that use polymorphism to avoid (un)boxing.
	*
	* <p>Instances of this class are immutable
	* and (contrarily to mutable array-based list implementations) the backing array is not exposed.
	* Instances can be built using a variety of methods, but note that constructors using
	* an array will not make a defensive copy.
	*
	* <p>This class implements the bulk method {@code getElements()} using
	* high-performance system calls (e.g., {@link
	* System#arraycopy(Object,int,Object,int,int) System.arraycopy()}) instead of
	* expensive loops.
	*/
public class DoubleImmutableList extends DoubleLists.ImmutableListBase implements DoubleList , RandomAccess, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 0L;

	static final DoubleImmutableList EMPTY = new DoubleImmutableList(DoubleArrays.EMPTY_ARRAY);
	/** The backing array; all elements are part of this list. */
	private final double a[];
	/** Creates a new immutable list using a given array.
	 *
	 * <p>Note that this constructor does not perform a defensive copy.
	 *
	 * @param a the array that will be used to back this immutable list.
	 */
	public DoubleImmutableList(final double a[]) {
	 this.a = a;
	}
	/** Creates a new immutable list and fills it with a given collection.
	 *
	 * @param c a collection that will be used to fill the immutable list.
	 */
	public DoubleImmutableList(final Collection<? extends Double> c) {
	 this(c.isEmpty() ? DoubleArrays.EMPTY_ARRAY : DoubleIterators.unwrap( DoubleIterators.asDoubleIterator(c.iterator())));
	}
	/** Creates a new immutable list and fills it with a given type-specific collection.
	 *
	 * @param c a type-specific collection that will be used to fill the immutable list.
	 */
	public DoubleImmutableList(final DoubleCollection c) {
	 this(c.isEmpty() ? DoubleArrays.EMPTY_ARRAY : DoubleIterators.unwrap(c.iterator()));
	}
	/** Creates a new immutable list and fills it with a given type-specific list.
	 *
	 * @param l a type-specific list that will be used to fill the immutable list.
	 */

	public DoubleImmutableList(final DoubleList l) {
	 this(l.isEmpty() ? DoubleArrays.EMPTY_ARRAY : new double[l.size()]);
	 l.getElements(0, a, 0, l.size());
	}
	/** Creates a new immutable list and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the immutable list.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 */

	public DoubleImmutableList(final double a[], final int offset, final int length) {
	 this(length == 0 ? DoubleArrays.EMPTY_ARRAY : new double[length]);
	 System.arraycopy(a, offset, this.a, 0, length);
	}
	/** Creates a new immutable list and fills it with the elements returned by a type-specific iterator..
	 *
	 * @param i a type-specific iterator whose returned elements will fill the immutable list.
	 */
	public DoubleImmutableList(final DoubleIterator i) {
	 this(i.hasNext() ? DoubleIterators.unwrap(i) : DoubleArrays.EMPTY_ARRAY);
	}
	/**
	 * Returns an empty immutable list.
	 * @return an immutable list (possibly shared) that is empty.
	 */
	
	 public static DoubleImmutableList of() {
	 return EMPTY;
	 }
	/** Creates an immutable list using a list of elements.
	 *
	 * <p>Note that this method does not perform a defensive copy.
	 *
	 * @param init a list of elements that will be used to initialize the list.
	 * @return a new immutable list containing the given elements.
	 */

	public static DoubleImmutableList of(final double... init) {
	 return init.length == 0 ? of() : new DoubleImmutableList (init);
	}
	private static DoubleImmutableList convertTrustedToImmutableList(DoubleArrayList arrayList) {
	 if (arrayList.isEmpty()) {
	  return of();
	 }
	 double backingArray[] = arrayList.elements();
	 if (arrayList.size() != backingArray.length) {
	  backingArray = java.util.Arrays.copyOf(backingArray, arrayList.size());
	 }
	 return new DoubleImmutableList (backingArray);
	 }
	/** Collects the result of a primitive {@code Stream} into a new ImmutableList.
	 *
	 * <p>This method performs a terminal operation on the given {@code Stream}
	 *
	 * @apiNote Taking a primitive stream instead of returning something like a
	 * {@link java.util.stream.Collector Collector} is necessary because there is no
	 * primitive {@code Collector} equivalent in the Java API.
	 */
	public static DoubleImmutableList toList(java.util.stream.DoubleStream stream) {
	 return convertTrustedToImmutableList(DoubleArrayList.toList(stream));
	}
	/** Collects the result of a primitive {@code Stream} into a new ImmutableList, potentially pre-allocated to handle the given size.
	 *
	 * <p>This method performs a terminal operation on the given {@code Stream}
	 *
	 * @apiNote Taking a primitive stream instead returning something like a
	 * {@link java.util.stream.Collector Collector} is necessary because there is no
	 * primitive {@code Collector} equivalent in the Java API.
	 */
	public static DoubleImmutableList toListWithExpectedSize(java.util.stream.DoubleStream stream, int expectedSize) {
	 return convertTrustedToImmutableList(DoubleArrayList.toListWithExpectedSize(stream, expectedSize));
	}
	@Override
	public double getDouble(final int index) {
	 if (index >= a.length) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + a.length + ")");
	 return a[index];
	}
	@Override
	public int indexOf(final double k) {
	 for(int i = 0, size = a.length; i < size; i++) if (( Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]) )) return i;
	 return -1;
	}
	@Override
	public int lastIndexOf(final double k) {
	 for(int i = a.length; i-- != 0;) if (( Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]) )) return i;
	 return -1;
	}
	@Override
	public int size() {
	 return a.length;
	}
	@Override
	public boolean isEmpty() {
	 return a.length == 0;
	}
	   /** Copies element of this type-specific list into the given array using optimized system calls.
	 *
	 * @param from the start index (inclusive).
	 * @param a the destination array.
	 * @param offset the offset into the destination array where to store the first element copied.
	 * @param length the number of elements to be copied.
	 */
	@Override
	public void getElements(final int from, final double[] a, final int offset, final int length) {
	 DoubleArrays.ensureOffsetLength(a, offset, length);
	 System.arraycopy(this.a, from, a, offset, length);
	}
	@Override
	public void forEach(final java.util.function.DoubleConsumer action) {
	 for (int i = 0; i < a.length; ++i) {
	  action.accept(a[i]);
	 }
	}
	@Override
	public double[] toDoubleArray() {
	 return a.clone();
	}
	@Override
	public double[] toArray(double a[]) {
	 if (a == null || a.length < size()) a = new double[this.a.length];
	 System.arraycopy(this.a, 0, a, 0, a.length);
	 return a;
	}
	@Override
	public DoubleListIterator listIterator(final int index) {
	 ensureIndex(index);
	 return new DoubleListIterator () {
	   int pos = index;
	   @Override
	   public boolean hasNext() { return pos < a.length; }
	   @Override
	   public boolean hasPrevious() { return pos > 0; }
	   @Override
	   public double nextDouble() { if (! hasNext()) throw new NoSuchElementException(); return a[pos++]; }
	   @Override
	   public double previousDouble() { if (! hasPrevious()) throw new NoSuchElementException(); return a[--pos]; }
	   @Override
	   public int nextIndex() { return pos; }
	   @Override
	   public int previousIndex() { return pos - 1; }
	   @Override
	   public void forEachRemaining(final java.util.function.DoubleConsumer action) {
	    while (pos < a.length) {
	     action.accept(a[pos++]);
	    }
	   }
	   @Override
	   public void add(double k) {
	    throw new UnsupportedOperationException();
	   }
	   @Override
	   public void set(double k) {
	    throw new UnsupportedOperationException();
	   }
	   @Override
	   public void remove() {
	    throw new UnsupportedOperationException();
	   }
	   @Override
	   public int back(int n) {
	    if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	    final int remaining = a.length - pos;
	    if (n < remaining) {
	     pos -= n;
	    } else {
	     n = remaining;
	     pos = 0;
	    }
	    return n;
	   }
	   @Override
	   public int skip(int n) {
	    if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	    final int remaining = a.length - pos;
	    if (n < remaining) {
	     pos += n;
	    } else {
	     n = remaining;
	     pos = a.length;
	    }
	    return n;
	   }
	  };
	}
	 private final class Spliterator implements DoubleSpliterator {
	 int pos, max;
	 public Spliterator() {
	  this(0, a.length);
	 }
	 private Spliterator(int pos, int max) {
	  assert pos <= max : "pos " + pos + " must be <= max " + max;
	  this.pos = pos;
	  this.max = max;
	 }
	 @Override
	 public int characteristics() { return DoubleSpliterators.LIST_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.IMMUTABLE; }
	 @Override
	 public long estimateSize() { return max - pos; }
	 @Override
	 public boolean tryAdvance(final java.util.function.DoubleConsumer action) {
	  if (pos >= max) return false;
	  action.accept(a[pos++]);
	  return true;
	 }
	 @Override
	 public void forEachRemaining(final java.util.function.DoubleConsumer action) {
	  for (; pos < max; ++pos) {
	   action.accept(a[pos]);
	  }
	 }
	 @Override
	 public long skip(long n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (pos >= max) return 0;
	  final int remaining = max - pos;
	  if (n < remaining) {
	   pos = it.unimi.dsi.fastutil.SafeMath.safeLongToInt(pos + n);
	   return n;
	  }
	  n = remaining;
	  pos = max;
	  return n;
	 }
	 @Override
	 public DoubleSpliterator trySplit() {
	  int retLen = (max - pos) >> 1;
	  if (retLen <= 1) return null;
	  int myNewPos = pos + retLen;
	  int retMax = myNewPos;
	  int oldPos = pos;
	  this.pos = myNewPos;
	  return new Spliterator(oldPos, retMax);
	 }
	}
	@Override
	public DoubleSpliterator spliterator() {
	 return new Spliterator();
	}
	private final static class ImmutableSubList extends DoubleLists.ImmutableListBase implements java.util.RandomAccess, java.io.Serializable {
	 private static final long serialVersionUID = 7054639518438982401L;
	 final DoubleImmutableList innerList;
	 final int from;
	 final int to;
	 /**
		 * An alias to {@code innerList}'s array to save some typing. Note that 0 in this array is actually
		 * the first element of the {@code innerList}, not this sublist. {@code a[from]} is the
		 * first element of this sublist.
		 */
	 final transient double a[];
	 /** No validation; callers must validate arguments. */
	 ImmutableSubList(DoubleImmutableList innerList, int from, int to) {
	  this.innerList = innerList;
	  this.from = from;
	  this.to = to;
	  this.a = innerList.a;
	 }
	 @Override
	 public double getDouble(final int index) {
	  ensureRestrictedIndex(index);
	  return a[index + from];
	 }
	 @Override
	 public int indexOf(final double k) {
	  for(int i = from; i < to; i++) if (( Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]) )) return i - from;
	  return -1;
	 }
	 @Override
	 public int lastIndexOf(final double k) {
	  for(int i = to; i-- != from;) if (( Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]) )) return i - from;
	  return -1;
	 }
	 @Override
	 public int size() {
	  return to - from;
	 }
	 @Override
	 public boolean isEmpty() {
	  return to <= from;
	 }
	 @Override
	 public void getElements(final int fromSublistIndex, final double[] a, final int offset, final int length) {
	  DoubleArrays.ensureOffsetLength(a, offset, length);
	  ensureRestrictedIndex(fromSublistIndex);
	  if (from + length > to)
	   throw new IndexOutOfBoundsException("Final index " + (from + length) + " (startingIndex: " + from + " + length: " + length + ") is greater then list length " + size());
	  System.arraycopy(this.a, fromSublistIndex + from, a, offset, length);
	 }
	 @Override
	 public void forEach(final java.util.function.DoubleConsumer action) {
	  for (int i = from; i < to; ++i) {
	   action.accept(a[i]);
	  }
	 }
	 @Override
	 public double[] toDoubleArray() {
	  return java.util.Arrays.copyOfRange(a, from, to);
	 }
	 @Override
	 public double[] toArray(double a[]) {
	  if (a == null || a.length < size()) a = new double[size()];
	  System.arraycopy(this.a, from, a, 0, size());
	  return a;
	 }
	 @Override
	 public DoubleListIterator listIterator(final int index) {
	  ensureIndex(index);
	  return new DoubleListIterator () {
	    int pos = index;
	    @Override
	    public boolean hasNext() { return pos < to; }
	    @Override
	    public boolean hasPrevious() { return pos > from; }
	    @Override
	    public double nextDouble() { if (! hasNext()) throw new NoSuchElementException(); return a[pos++ + from]; }
	    @Override
	    public double previousDouble() { if (! hasPrevious()) throw new NoSuchElementException(); return a[--pos + from]; }
	    @Override
	    public int nextIndex() { return pos; }
	    @Override
	    public int previousIndex() { return pos - 1; }
	    @Override
	    public void forEachRemaining(final java.util.function.DoubleConsumer action) {
	     while (pos < to) {
	      action.accept(a[pos++ + from]);
	     }
	    }
	    @Override
	    public void add(double k) {
	     throw new UnsupportedOperationException();
	    }
	    @Override
	    public void set(double k) {
	     throw new UnsupportedOperationException();
	    }
	    @Override
	    public void remove() {
	     throw new UnsupportedOperationException();
	    }
	    @Override
	    public int back(int n) {
	     if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	     final int remaining = to - pos;
	     if (n < remaining) {
	      pos -= n;
	     } else {
	      n = remaining;
	      pos = 0;
	     }
	     return n;
	    }
	    @Override
	    public int skip(int n) {
	     if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	     final int remaining = to - pos;
	     if (n < remaining) {
	      pos += n;
	     } else {
	      n = remaining;
	      pos = to;
	     }
	     return n;
	    }
	   };
	 }
	 private final class SubListSpliterator extends DoubleSpliterators.EarlyBindingSizeIndexBasedSpliterator {
	  // We are using pos == 0 to be 0 relative to real array 0
	  SubListSpliterator() {
	   super(from, to);
	  }
	  /** No validation; callers must validate arguments. */
	  private SubListSpliterator(int pos, int maxPos) {
	   super(pos, maxPos);
	  }
	  // Remember, the indexes we are getting is the real array's index, not our sublist relative index.
	   @Override
	  protected final double get(int i) { return a[i]; }
	  @Override
	  protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
	   return new SubListSpliterator(pos, maxPos);
	  }
	  @Override
	  public boolean tryAdvance(final java.util.function.DoubleConsumer action) {
	   if (pos >= maxPos) return false;
	   action.accept(a[pos++]);
	   return true;
	  }
	  @Override
	  public void forEachRemaining(final java.util.function.DoubleConsumer action) {
	   final int max = maxPos;
	   while(pos < max) {
	    action.accept(a[pos++]);
	   }
	  }
	  @Override
	  public int characteristics() { return DoubleSpliterators.LIST_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.IMMUTABLE; }
	 }
	 @Override
	 public DoubleSpliterator spliterator() {
	  return new SubListSpliterator();
	 }
	 boolean contentsEquals(double[] otherA, int otherAFrom, int otherATo) {
	  if (a == otherA && from == otherAFrom && to == otherATo) {
	   return true;
	  }
	  if (otherATo - otherAFrom != size()) {
	   return false;
	  }
	  int pos = from, otherPos = otherAFrom;
	  // We have already assured that the two ranges are the same size, so we only need to check one bound.
	  // TODO When minimum version of Java becomes Java 9, use the Arrays.equals which takes bounds, which is vectorized.
	  // Make sure to split out the reference equality case when you do this.
	  while(pos < to) if (a[pos++] != otherA[otherPos++]) return false;
	  return true;
	 }
	 @Override
	 public boolean equals(Object o) {
	  if (o == this) return true;
	  if (o == null) return false;
	  if (!(o instanceof java.util.List)) return false;
	  if (o instanceof DoubleImmutableList) {
	  
	   DoubleImmutableList other = (DoubleImmutableList ) o;
	   return contentsEquals(other.a, 0, other.size());
	  }
	  if (o instanceof ImmutableSubList) {
	  
	   ImmutableSubList other = (ImmutableSubList ) o;
	   return contentsEquals(other.a, other.from, other.to);
	  }
	  return super.equals(o);
	 }
	
	 int contentsCompareTo(double[] otherA, int otherAFrom, int otherATo) {
	  if (a == otherA && from == otherAFrom && to == otherATo) return 0;
	  // TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
	  double e1, e2;
	  int r, i, j;
	  for(i = from, j = otherAFrom; i < to && i < otherATo; i++, j++) {
	   e1 = a[i];
	   e2 = otherA[j];
	   if ((r = ( Double.compare((e1),(e2)) )) != 0) return r;
	  }
	  return i < otherATo ? -1 : (i < to ? 1 : 0);
	 }
	
	 @Override
	 public int compareTo(final java.util.List <? extends Double> l) {
	  if (l instanceof DoubleImmutableList) {
	  
	   DoubleImmutableList other = (DoubleImmutableList ) l;
	   return contentsCompareTo(other.a, 0, other.size());
	  }
	  if (l instanceof ImmutableSubList) {
	  
	   ImmutableSubList other = (ImmutableSubList ) l;
	   return contentsCompareTo(other.a, other.from, other.to);
	  }
	  return super.compareTo(l);
	 }
	 private Object readResolve() throws java.io.ObjectStreamException {
	  // We need to recheck the invariants of the subList and reestablish our "a" array alias.
	  // Easiest way to do this is to just make a subList anew.
	  // This will not cause a recopy of contents as subLists are a view, so this is all constant time.
	  try {
	   return innerList.subList(from, to);
	  } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
	   throw (java.io.InvalidObjectException) (new java.io.InvalidObjectException(ex.getMessage()).initCause(ex));
	  }
	 }
	
	 @Override
	 public DoubleList subList(int from, int to) {
	  // We don't need to worry about keeping all nested sublists up to date with bounds as we are immutable.
	  // So don't even nest; just return a sublist with the immediate parent of the root list.
	  ensureIndex(from);
	  ensureIndex(to);
	  if (from == to) return EMPTY;
	  if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
	  return new ImmutableSubList (innerList, from + this.from, to + this.from);
	 }
	}
	/**
	 * {@inheritDoc}
	 *
	 * @apiNote The returned list will be immutable, but is currently not declared to return an
	 * instance of {@code ImmutableList} due to complications of implementation details.
	 * This may change in a future version (in other words, do not consider the return type of
	 * this method to be stable if making a subclass of {@code ImmutableList}).
	 */

	@Override
	public DoubleList subList(int from, int to) {
	 if (from == 0 && to == size()) return this;
	 ensureIndex(from);
	 ensureIndex(to);
	 if (from == to) return EMPTY;
	 if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
	 return new ImmutableSubList (this, from, to);
	}
	@Override
	public DoubleImmutableList clone() {
	 return this;
	}
	/** Compares this type-specific immutable list to another one.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation
	 * inherited from the abstract implementation would already work.
	 *
	 * @param l a type-specific immutable list.
	 * @return true if the argument contains the same elements of this type-specific immutable list.
	 */
	public boolean equals(final DoubleImmutableList l) {
	 if (l == this) return true;
	 if (a == l.a) return true;
	 int s = size();
	 if (s != l.size()) return false;
	 final double[] a1 = a;
	 final double[] a2 = l.a;
	 return java.util.Arrays.equals(a1, a2);
	}
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean equals(final Object o) {
	 if (o == this) return true;
	 if (o == null) return false;
	 if (!(o instanceof java.util.List)) return false;
	 if (o instanceof DoubleImmutableList) {
	  return equals((DoubleImmutableList ) o);
	 }
	 if (o instanceof ImmutableSubList) {
	  // Sublist has an optimized sub-array based comparison, reuse that.
	  return ((ImmutableSubList )o).equals(this);
	 }
	 return super.equals(o);
	}
	/** Compares this immutable list to another immutable list.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation
	 * inherited from the abstract implementation would already work.
	 *
	 * @param l an immutable list.
	 * @return a negative integer,
	 * zero, or a positive integer as this list is lexicographically less than, equal
	 * to, or greater than the argument.
	 */

	public int compareTo(final DoubleImmutableList l) {
	 if (a == l.a) return 0;
	 // TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
	 final int s1 = size(), s2 = l.size();
	 final double a1[] = a, a2[] = l.a;
	 double e1, e2;
	 int r, i;
	 for(i = 0; i < s1 && i < s2; i++) {
	  e1 = a1[i];
	  e2 = a2[i];
	  if ((r = ( Double.compare((e1),(e2)) )) != 0) return r;
	 }
	 return i < s2 ? -1 : (i < s1 ? 1 : 0);
	}

	@Override
	public int compareTo(final java.util.List <? extends Double> l) {
	 if (l instanceof DoubleImmutableList) {
	  return compareTo((DoubleImmutableList )l);
	 }
	 if (l instanceof ImmutableSubList) {
	  // Safe to strip the "extends" because we will only ever take elements, never modify them (especially because it is immutable).
	  ImmutableSubList other = (ImmutableSubList )l;
	  // Must negate because we are inverting the order of the comparison.
	  return -other.compareTo(this);
	 }
	 return super.compareTo(l);
	}
}
