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
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import static it.unimi.dsi.fastutil.HashCommon.arraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**  A type-specific hash set with with a fast, small-footprint implementation.
	*
	* <p>Instances of this class use a hash table to represent a set. The table is
	* filled up to a specified <em>load factor</em>, and then doubled in size to
	* accommodate new entries. If the table is emptied below <em>one fourth</em>
	* of the load factor, it is halved in size; however, the table is never reduced to a
	* size smaller than that at creation time: this approach makes it
	* possible to create sets with a large capacity in which insertions and
	* deletions do not cause immediately rehashing. Moreover, halving is
	* not performed when deleting entries from an iterator, as it would interfere
	* with the iteration process.
	*
	* <p>Note that {@link #clear()} does not modify the hash table size.
	* Rather, a family of {@linkplain #trim() trimming
	* methods} lets you control the size of the table; this is particularly useful
	* if you reuse instances of this class.
	*
	* @see Hash
	* @see HashCommon
	*/
public class CharOpenHashSet extends AbstractCharSet implements java.io.Serializable, Cloneable, Hash {
	private static final long serialVersionUID = 0L;
	private static final boolean ASSERTS = false;
	/** The array of keys. */
	protected transient char[] key;
	/** The mask for wrapping a position counter. */
	protected transient int mask;
	/** Whether this set contains the null key. */
	protected transient boolean containsNull;
	/** The current table size. Note that an additional element is allocated for storing the null key. */
	protected transient int n;
	/** Threshold after which we rehash. It must be the table size times {@link #f}. */
	protected transient int maxFill;
	/** We never resize below this threshold, which is the construction-time {#n}. */
	protected final transient int minN;
	/** Number of entries in the set (including the null key, if present). */
	protected int size;
	/** The acceptable load factor. */
	protected final float f;
	/** Creates a new hash set.
	 *
	 * <p>The actual table size will be the least power of two greater than {@code expected}/{@code f}.
	 *
	 * @param expected the expected number of elements in the hash set.
	 * @param f the load factor.
	 */

	public CharOpenHashSet(final int expected, final float f) {
	 if (f <= 0 || f >= 1) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
	 if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
	 this.f = f;
	 minN = n = arraySize(expected, f);
	 mask = n - 1;
	 maxFill = maxFill(n, f);
	 key = new char[n + 1];
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 *
	 * @param expected the expected number of elements in the hash set.
	 */
	public CharOpenHashSet(final int expected) {
	 this(expected, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set with initial expected {@link Hash#DEFAULT_INITIAL_SIZE} elements
	 * and {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 */
	public CharOpenHashSet() {
	 this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set copying a given collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash set.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final Collection<? extends Character> c, final float f) {
	 this(c.size(), f);
	 addAll(c);
	}
	/** Creates a new hash set  with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * copying a given collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash set.
	 */
	public CharOpenHashSet(final Collection<? extends Character> c) {
	 this(c, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set copying a given type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash set.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final CharCollection c, final float f) {
	 this(c.size(), f);
	 addAll(c);
	}
	/** Creates a new hash set  with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * copying a given type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash set.
	 */
	public CharOpenHashSet(final CharCollection c) {
	 this(c, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set using elements provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the set.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final CharIterator i, final float f) {
	 this(DEFAULT_INITIAL_SIZE, f);
	 while(i.hasNext()) add(i.nextChar());
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using elements provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the set.
	 */
	public CharOpenHashSet(final CharIterator i) {
	 this(i, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set using elements provided by an iterator.
	 *
	 * @param i an iterator whose elements will fill the set.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final Iterator<?> i, final float f) {
	 this(CharIterators.asCharIterator(i), f);
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using elements provided by an iterator.
	 *
	 * @param i an iterator whose elements will fill the set.
	 */
	public CharOpenHashSet(final Iterator<?> i) {
	 this(CharIterators.asCharIterator(i));
	}
	/** Creates a new hash set and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final char[] a, final int offset, final int length, final float f) {
	 this(length < 0 ? 0 : length, f);
	 CharArrays.ensureOffsetLength(a, offset, length);
	 for(int i = 0; i < length; i++) add(a[offset + i]);
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 */
	public CharOpenHashSet(final char[] a, final int offset, final int length) {
	 this(a, offset, length, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new hash set copying the elements of an array.
	 *
	 * @param a an array to be copied into the new hash set.
	 * @param f the load factor.
	 */
	public CharOpenHashSet(final char[] a, final float f) {
	 this(a, 0, a.length, f);
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * copying the elements of an array.
	 *
	 * @param a an array to be copied into the new hash set.
	 */
	public CharOpenHashSet(final char[] a) {
	 this(a, DEFAULT_LOAD_FACTOR);
	}
	/** Creates a new empty hash set.
	 *
	 * @return a new empty hash set.
	 */
	public static CharOpenHashSet of() {
	 return new CharOpenHashSet ();
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * using the given element.
	 *
	 * @param e the element that the returned set will contain.
	 * @return a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor containing {@code e}.
	 */
	public static CharOpenHashSet of(final char e) {
	 CharOpenHashSet result = new CharOpenHashSet (1, DEFAULT_LOAD_FACTOR);
	 result.add(e);
	 return result;
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * using the elements given.
	 *
	 * @param e0 the first element.
	 * @param e1 the second element.
	 * @return a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor containing {@code e0} and {@code e1}.
	 * @throws IllegalArgumentException if there were duplicate entries.
	 */
	public static CharOpenHashSet of(final char e0, final char e1) {
	 CharOpenHashSet result = new CharOpenHashSet (2, DEFAULT_LOAD_FACTOR);
	 result.add(e0);
	 if (!result.add(e1)) {
	  throw new IllegalArgumentException("Duplicate element: " + e1);
	 }
	 return result;
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * using the elements given.
	 *
	 * @param e0 the first element.
	 * @param e1 the second element.
	 * @param e2 the third element.
	 * @return a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor containing {@code e0}, {@code e1}, and {@code e2}.
	 * @throws IllegalArgumentException if there were duplicate entries.
	 */
	public static CharOpenHashSet of(final char e0, final char e1, final char e2) {
	 CharOpenHashSet result = new CharOpenHashSet (3, DEFAULT_LOAD_FACTOR);
	 result.add(e0);
	 if (!result.add(e1)) {
	  throw new IllegalArgumentException("Duplicate element: " + e1);
	 }
	 if (!result.add(e2)) {
	  throw new IllegalArgumentException("Duplicate element: " + e2);
	 }
	 return result;
	}
	/** Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor
	 * using a list of elements.
	 *
	 * @param a a list of elements that will be used to initialize the new hash set.
	 * @return a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor containing the elements of {@code a}.
	 * @throws IllegalArgumentException if a duplicate entry was encountered.
	 */

	public static CharOpenHashSet of(final char... a) {
	 CharOpenHashSet result = new CharOpenHashSet (a.length, DEFAULT_LOAD_FACTOR);
	 for (char element : a) {
	  if (!result.add(element)) {
	   throw new IllegalArgumentException("Duplicate element " + element);
	  }
	 }
	 return result;
	}
	private int realSize() {
	 return containsNull ? size - 1 : size;
	}
	private void ensureCapacity(final int capacity) {
	 final int needed = arraySize(capacity, f);
	 if (needed > n) rehash(needed);
	}
	private void tryCapacity(final long capacity) {
	 final int needed = (int)Math.min(1 << 30, Math.max(2, HashCommon.nextPowerOfTwo((long)Math.ceil(capacity / f))));
	 if (needed > n) rehash(needed);
	}
	@Override
	public boolean addAll(CharCollection c) {
	 if (f <= .5) ensureCapacity(c.size()); // The resulting collection will be sized for c.size() elements
	 else tryCapacity(size() + c.size()); // The resulting collection will be tentatively sized for size() + c.size() elements
	 return super.addAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends Character> c) {
	 // The resulting collection will be at least c.size() big
	 if (f <= .5) ensureCapacity(c.size()); // The resulting collection will be sized for c.size() elements
	 else tryCapacity(size() + c.size()); // The resulting collection will be tentatively sized for size() + c.size() elements
	 return super.addAll(c);
	}
	@Override
	public boolean add(final char k) {
	 int pos;
	 if (( (k) == ((char)0) )) {
	  if (containsNull) return false;
	  containsNull = true;
	 }
	 else {
	  char curr;
	  final char[] key = this.key;
	  // The starting point.
	  if (! ( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( (k) ) ) & mask]) == ((char)0) )) {
	   if (( (curr) == (k) )) return false;
	   while(! ( (curr = key[pos = (pos + 1) & mask]) == ((char)0) ))
	    if (( (curr) == (k) )) return false;
	  }
	  key[pos] = k;
	 }
	 if (size++ >= maxFill) rehash(arraySize(size + 1, f));
	 if (ASSERTS) checkTable();
	 return true;
	}
	/** Shifts left entries with the specified hash code, starting at the specified position,
	 * and empties the resulting free entry.
	 *
	 * @param pos a starting position.
	 */
	protected final void shiftKeys(int pos) {
	 // Shift entries with the same hash.
	 int last, slot;
	 char curr;
	 final char[] key = this.key;
	 for(;;) {
	  pos = ((last = pos) + 1) & mask;
	  for(;;) {
	   if (( (curr = key[pos]) == ((char)0) )) {
	    key[last] = ((char)0);
	    return;
	   }
	   slot = ( it.unimi.dsi.fastutil.HashCommon.mix( (curr) ) ) & mask;
	   if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
	   pos = (pos + 1) & mask;
	  }
	  key[last] = curr;
	 }
	}
	private boolean removeEntry(final int pos) {
	 size--;
	 shiftKeys(pos);
	 if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
	 return true;
	}
	private boolean removeNullEntry() {
	 containsNull = false;
	 key[n] = ((char)0);
	 size--;
	 if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
	 return true;
	}

	@Override
	public boolean remove(final char k) {
	 if (( (k) == ((char)0) )) {
	  if (containsNull) return removeNullEntry();
	  return false;
	 }
	 char curr;
	 final char[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( (k) ) ) & mask]) == ((char)0) )) return false;
	 if (( (k) == (curr) )) return removeEntry(pos);
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == ((char)0) )) return false;
	  if (( (k) == (curr) )) return removeEntry(pos);
	 }
	}

	@Override
	public boolean contains(final char k) {
	 if (( (k) == ((char)0) )) return containsNull;
	 char curr;
	 final char[] key = this.key;
	 int pos;
	 // The starting point.
	 if (( (curr = key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( (k) ) ) & mask]) == ((char)0) )) return false;
	 if (( (k) == (curr) )) return true;
	 while(true) {
	  if (( (curr = key[pos = (pos + 1) & mask]) == ((char)0) )) return false;
	  if (( (k) == (curr) )) return true;
	 }
	}
	/* Removes all elements from this set.
	 *
	 * <p>To increase object reuse, this method does not change the table size.
	 * If you want to reduce the table size, you must use {@link #trim()}.
	 *
	 */
	@Override
	public void clear() {
	 if (size == 0) return;
	 size = 0;
	 containsNull = false;
	 Arrays.fill(key, ((char)0));
	}
	@Override
	public int size() {
	 return size;
	}
	@Override
	public boolean isEmpty() {
	 return size == 0;
	}
	/** An iterator over a hash set. */
	private final class SetIterator implements CharIterator {
	 /** The index of the last entry returned, if positive or zero; initially, {@link #n}. If negative, the last
			element returned was that of index {@code - pos - 1} from the {@link #wrapped} list. */
	 int pos = n;
	 /** The index of the last entry that has been returned (more precisely, the value of {@link #pos} if {@link #pos} is positive,
			or {@link Integer#MIN_VALUE} if {@link #pos} is negative). It is -1 if either
			we did not return an entry yet, or the last returned entry has been removed. */
	 int last = -1;
	 /** A downward counter measuring how many entries must still be returned. */
	 int c = size;
	 /** A boolean telling us whether we should return the null key. */
	 boolean mustReturnNull = CharOpenHashSet.this.containsNull;
	 /** A lazily allocated list containing elements that have wrapped around the table because of removals. */
	 CharArrayList wrapped;
	 @Override
	 public boolean hasNext() {
	  return c != 0;
	 }
	 @Override
	 public char nextChar() {
	  if (! hasNext()) throw new NoSuchElementException();
	  c--;
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   last = n;
	   return key[n];
	  }
	  final char key[] = CharOpenHashSet.this.key;
	  for(;;) {
	   if (--pos < 0) {
	    // We are just enumerating elements from the wrapped list.
	    last = Integer.MIN_VALUE;
	    return wrapped.getChar(- pos - 1);
	   }
	   if (! ( (key[pos]) == ((char)0) )) return key[last = pos];
	  }
	 }
	 /** Shifts left entries with the specified hash code, starting at the specified position,
		 * and empties the resulting free entry.
		 *
		 * @param pos a starting position.
		 */
	 private final void shiftKeys(int pos) {
	  // Shift entries with the same hash.
	  int last, slot;
	  char curr;
	  final char[] key = CharOpenHashSet.this.key;
	  for(;;) {
	   pos = ((last = pos) + 1) & mask;
	   for(;;) {
	    if (( (curr = key[pos]) == ((char)0) )) {
	     key[last] = ((char)0);
	     return;
	    }
	    slot = ( it.unimi.dsi.fastutil.HashCommon.mix( (curr) ) ) & mask;
	    if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
	    pos = (pos + 1) & mask;
	   }
	   if (pos < last) { // Wrapped entry.
	    if (wrapped == null) wrapped = new CharArrayList (2);
	    wrapped.add(key[pos]);
	   }
	   key[last] = curr;
	  }
	 }
	 @Override
	 public void remove() {
	  if (last == -1) throw new IllegalStateException();
	  if (last == n) {
	   CharOpenHashSet.this.containsNull = false;
	   CharOpenHashSet.this.key[n] = ((char)0);
	  }
	  else if (pos >= 0) shiftKeys(last);
	  else {
	   // We're removing wrapped entries.
	   CharOpenHashSet.this.remove(wrapped.getChar(- pos - 1));
	   last = -1; // Note that we must not decrement size
	   return;
	  }
	  size--;
	  last = -1; // You can no longer remove this entry.
	  if (ASSERTS) checkTable();
	 }
	 @Override
	 public void forEachRemaining(final CharConsumer action) {
	  final char key[] = CharOpenHashSet.this.key;
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   last = n;
	   action.accept(key[n]);
	   c--;
	  }
	  while(c != 0) {
	   if (--pos < 0) {
	    // We are just enumerating elements from the wrapped list.
	    last = Integer.MIN_VALUE;
	    action.accept(wrapped.getChar(- pos - 1));
	    c--;
	   } else if (! ( (key[pos]) == ((char)0) )) {
	    action.accept(key[last = pos]);
	    c--;
	   }
	  }
	 }
	}
	@Override
	public CharIterator iterator() {
	 return new SetIterator();
	}
	private final class SetSpliterator implements CharSpliterator {
	 private static final int POST_SPLIT_CHARACTERISTICS = CharSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
	 /** The index (which bucket) of the next item to give to the action.
		 * Unlike {@link SetIterator}, this counts up instead of down.
		 */
	 int pos = 0;
	 /** The maximum bucket (exclusive) to iterate to */
	 int max = n;
	 /** An upwards counter counting how many we have given */
	 int c = 0;
	 /** A boolean telling us whether we should return the null key. */
	 boolean mustReturnNull = CharOpenHashSet.this.containsNull;
	 boolean hasSplit = false;
	 SetSpliterator() {}
	 SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
	  this.pos = pos;
	  this.max = max;
	  this.mustReturnNull = mustReturnNull;
	  this.hasSplit = hasSplit;
	 }
	 @Override
	 public boolean tryAdvance(final CharConsumer action) {
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   ++c;
	   action.accept(key[n]);
	   return true;
	  }
	  final char key[] = CharOpenHashSet.this.key;
	  while (pos < max) {
	   if (! ( (key[pos]) == ((char)0) )) {
	    ++c;
	    action.accept(key[pos++]);
	    return true;
	   } else {
	    ++pos;
	   }
	  }
	  return false;
	 }
	 @Override
	 public void forEachRemaining(final CharConsumer action) {
	  final char key[] = CharOpenHashSet.this.key;
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   action.accept(key[n]);
	   ++c;
	  }
	  while (pos < max) {
	   if (! ( (key[pos]) == ((char)0) )) {
	    action.accept(key[pos]);
	    ++c;
	   }
	   ++pos;
	  }
	 }
	 @Override
	 public int characteristics() {
	  return hasSplit ? POST_SPLIT_CHARACTERISTICS : CharSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
	 }
	 @Override
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
	 @Override
	 public SetSpliterator trySplit() {
	  if (pos >= max - 1) return null;
	  int retLen = (max - pos) >> 1;
	  if (retLen <= 1) return null;
	  int myNewPos = pos + retLen;
	  int retPos = pos;
	  int retMax = myNewPos;
	  // Since null is returned first, and the convention is that the returned split is the prefix of elements,
	  // the split will take care of returning null (if needed), and we won't return it anymore.
	  SetSpliterator split = new SetSpliterator(retPos, retMax, mustReturnNull, true);
	  this.pos = myNewPos;
	  this.mustReturnNull = false;
	  this.hasSplit = true;
	  return split;
	 }
	 @Override
	 public long skip(long n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n == 0) return 0;
	  long skipped = 0;
	  if (mustReturnNull) {
	   mustReturnNull = false;
	   ++skipped;
	   --n;
	  }
	  final char key[] = CharOpenHashSet.this.key;
	  while (pos < max && n > 0) {
	   if (! ( (key[pos++]) == ((char)0) )) {
	    ++skipped;
	    --n;
	   }
	  }
	  return skipped;
	 }
	}
	@Override
	public CharSpliterator spliterator() {
	 return new SetSpliterator();
	}
	@Override
	public void forEach(final CharConsumer action) {
	 if (containsNull) action.accept(key[n]);
	 final char key[] = this.key;
	 for(int pos = n; pos-- != 0; ) if (! ( (key[pos]) == ((char)0) )) action.accept(key[pos]);
	}
	/** Rehashes this set, making the table as small as possible.
	 *
	 * <p>This method rehashes the table to the smallest size satisfying the
	 * load factor. It can be used when the set will not be changed anymore, so
	 * to optimize access speed and size.
	 *
	 * <p>If the table size is already the minimum possible, this method
	 * does nothing.
	 *
	 * @return true if there was enough memory to trim the set.
	 * @see #trim(int)
	 */
	public boolean trim() {
	 return trim(size);
	}
	/** Rehashes this set if the table is too large.
	 *
	 * <p>Let <var>N</var> be the smallest table size that can hold
	 * <code>max(n,{@link #size()})</code> entries, still satisfying the load factor. If the current
	 * table size is smaller than or equal to <var>N</var>, this method does
	 * nothing. Otherwise, it rehashes this set in a table of size
	 * <var>N</var>.
	 *
	 * <p>This method is useful when reusing sets.  {@linkplain #clear() Clearing a
	 * set} leaves the table size untouched. If you are reusing a set
	 * many times, you can call this method with a typical
	 * size to avoid keeping around a very large table just
	 * because of a few large transient sets.
	 *
	 * @param n the threshold for the trimming.
	 * @return true if there was enough memory to trim the set.
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
	/** Rehashes the set.
	 *
	 * <p>This method implements the basic rehashing strategy, and may be
	 * overriden by subclasses implementing different rehashing strategies (e.g.,
	 * disk-based rehashing). However, you should not override this method
	 * unless you understand the internal workings of this class.
	 *
	 * @param newN the new size
	 */

	protected void rehash(final int newN) {
	 final char key[] = this.key;
	 final int mask = newN - 1; // Note that this is used by the hashing macro
	 final char newKey[] = new char[newN + 1];
	 int i = n, pos;
	 for(int j = realSize(); j-- != 0;) {
	  while(( (key[--i]) == ((char)0) ));
	  if (! ( (newKey[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( (key[i]) ) ) & mask]) == ((char)0) ))
	   while (! ( (newKey[pos = (pos + 1) & mask]) == ((char)0) ));
	  newKey[pos] = key[i];
	 }
	 n = newN;
	 this.mask = mask;
	 maxFill = maxFill(n, f);
	 this.key = newKey;
	}
	/** Returns a deep copy of this set.
	 *
	 * <p>This method performs a deep copy of this hash set; the data stored in the
	 * set, however, is not cloned. Note that this makes a difference only for object keys.
	 *
	 *  @return a deep copy of this set.
	 */
	@Override

	public CharOpenHashSet clone() {
	 CharOpenHashSet c;
	 try {
	  c = (CharOpenHashSet )super.clone();
	 }
	 catch(CloneNotSupportedException cantHappen) {
	  throw new InternalError();
	 }
	 c.key = key.clone();
	 c.containsNull = containsNull;
	 return c;
	}
	/** Returns a hash code for this set.
	 *
	 * This method overrides the generic method provided by the superclass.
	 * Since {@code equals()} is not overriden, it is important
	 * that the value returned by this method is the same value as
	 * the one returned by the overriden method.
	 *
	 * @return a hash code for this set.
	 */
	@Override
	public int hashCode() {
	 int h = 0;
	 for(int j = realSize(), i = 0; j-- != 0;) {
	  while(( (key[i]) == ((char)0) )) i++;
	   h += (key[i]);
	  i++;
	 }
	 // Zero / null have hash zero.
	 return h;
	}
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	 final CharIterator i = iterator();
	 s.defaultWriteObject();
	 for(int j = size; j-- != 0;) s.writeChar(i.nextChar());
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
	 s.defaultReadObject();
	 n = arraySize(size, f);
	 maxFill = maxFill(n, f);
	 mask = n - 1;
	 final char key[] = this.key = new char[n + 1];
	 char k;
	 for(int i = size, pos; i-- != 0;) {
	  k = s.readChar();
	  if (( (k) == ((char)0) )) {
	   pos = n;
	   containsNull = true;
	  }
	  else {
	   if (! ( (key[pos = ( it.unimi.dsi.fastutil.HashCommon.mix( (k) ) ) & mask]) == ((char)0) ))
	    while (! ( (key[pos = (pos + 1) & mask]) == ((char)0) ));
	  }
	  key[pos] = k;
	 }
	 if (ASSERTS) checkTable();
	}
	private void checkTable() {}
}
