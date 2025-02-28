/*
	* Copyright (C) 2003-2022 Paolo Boldi and Sebastiano Vigna
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
import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.NoSuchElementException;
/** A type-specific array-based semi-indirect priority queue.
	*
	* <p>Instances of this class use as reference list a <em>reference array</em>,
	* which must be provided to each constructor, and represent a priority queue
	* using a backing array of integer indices&mdash;all operations are performed
	* directly on the array. The array is enlarged as needed, but it is never
	* shrunk. Use the {@link #trim()} method to reduce its size, if necessary.
	*
	* @implSpec This implementation is extremely inefficient, but it is difficult to beat
	* when the size of the queue is very small. Moreover, it allows to enqueue several
	* time the same index, without limitations.
	*/
public class CharArrayIndirectPriorityQueue implements CharIndirectPriorityQueue {
	/** The reference array. */
	protected char refArray[];
	/** The backing array. */
	protected int array[] = IntArrays.EMPTY_ARRAY;
	/** The number of elements in this queue. */
	protected int size;
	/** The type-specific comparator used in this queue. */
	protected CharComparator c;
	/** The first index, cached, if {@link #firstIndexValid} is true. */
	protected int firstIndex;
	/** Whether {@link #firstIndex} contains a valid value. */
	protected boolean firstIndexValid;
	/** Creates a new empty queue without elements with a given capacity and comparator.
	 *
	 * @param refArray the reference array.
	 * @param capacity the initial capacity of this queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public CharArrayIndirectPriorityQueue(char[] refArray, int capacity, CharComparator c) {
	 if (capacity > 0) this.array = new int[capacity];
	 this.refArray = refArray;
	 this.c = c;
	}
	/** Creates a new empty queue with given capacity and using the natural order.
	 *
	 * @param refArray the reference array.
	 * @param capacity the initial capacity of this queue.
	 */
	public CharArrayIndirectPriorityQueue(char[] refArray, int capacity) {
	 this(refArray, capacity, null);
	}
	/** Creates a new empty queue with capacity equal to the length of the reference array and a given comparator.
	 *
	 * @param refArray the reference array.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public CharArrayIndirectPriorityQueue(char[] refArray, CharComparator c) {
	 this(refArray, refArray.length, c);
	}
	/** Creates a new empty queue with capacity equal to the length of the reference array and using the natural order.
	 * @param refArray the reference array.
	 */
	public CharArrayIndirectPriorityQueue(char[] refArray) {
	 this(refArray, refArray.length, null);
	}
	/** Wraps a given array in a queue using a given comparator.
	 *
	 * <p>The queue returned by this method will be backed by the given array.
	 *
	 * @param refArray the reference array.
	 * @param a an array of indices into {@code refArray}.
	 * @param size the number of elements to be included in the queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public CharArrayIndirectPriorityQueue(final char[] refArray, final int[] a, int size, final CharComparator c) {
	 this(refArray, 0, c);
	 this.array = a;
	 this.size = size;
	}
	/** Wraps a given array in a queue using a given comparator.
	 *
	 * <p>The queue returned by this method will be backed by the given array.
	 *
	 * @param refArray the reference array.
	 * @param a an array of indices into {@code refArray}.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public CharArrayIndirectPriorityQueue(final char[] refArray, final int[] a, final CharComparator c) {
	 this(refArray, a, a.length, c);
	}
	/** Wraps a given array in a queue using the natural order.
	 *
	 * <p>The queue returned by this method will be backed by the given array.
	 *
	 * @param refArray the reference array.
	 * @param a an array of indices into {@code refArray}.
	 * @param size the number of elements to be included in the queue.
	 */
	public CharArrayIndirectPriorityQueue(final char[] refArray, final int[] a, int size) {
	 this(refArray, a, size, null);
	}
	/** Wraps a given array in a queue using the natural order.
	 *
	 * <p>The queue returned by this method will be backed by the given array.
	 *
	 * @param refArray the reference array.
	 * @param a an array of indices into {@code refArray}.
	 */
	public CharArrayIndirectPriorityQueue(final char[] refArray, final int[] a) {
	 this(refArray, a, a.length);
	}
	/** Returns the index (in {@link #array}) of the smallest element. */

	private int findFirst() {
	 if (firstIndexValid) return this.firstIndex;
	 firstIndexValid = true;
	 int i = size;
	 int firstIndex = --i;
	 char first = refArray[array[firstIndex]];
	 if (c == null) while(i-- != 0) { if (( (refArray[array[i]]) < (first) )) first = refArray[array[firstIndex = i]]; }
	 else while(i-- != 0) { if (c.compare(refArray[array[i]], first) < 0) first = refArray[array[firstIndex = i]]; }
	 return this.firstIndex = firstIndex;
	}
	/** Returns the index (in {@link #array}) of the largest element. */

	private int findLast() {
	 int i = size;
	 int lastIndex = --i;
	 char last = refArray[array[lastIndex]];
	 if (c == null) { while(i-- != 0) if (( (last) < (refArray[array[i]]) )) last = refArray[array[lastIndex = i]]; }
	 else { while(i-- != 0) if (c.compare(last, refArray[array[i]]) < 0) last = refArray[array[lastIndex = i]]; }
	 return lastIndex;
	}
	protected final void ensureNonEmpty() {
	 if (size == 0) throw new NoSuchElementException();
	}
	/** Ensures that the given index is a firstIndexValid reference.
	 *
	 * @param index an index in the reference array.
	 * @throws IndexOutOfBoundsException if the given index is negative or larger than the reference array length.
	 */
	protected void ensureElement(final int index) {
	 if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
	 if (index >= refArray.length) throw new IndexOutOfBoundsException("Index (" + index + ") is larger than or equal to reference array size (" + refArray.length + ")");
	}
	/** {@inheritDoc}
	 *
	 * <p>Note that for efficiency reasons this method will <em>not</em> throw an exception
	 * when {@code x} is already in the queue. However, the queue state will become
	 * inconsistent and the following behaviour will not be predictable.
	 */
	@Override

	public void enqueue(int x) {
	 ensureElement(x);
	 if (size == array.length) array = IntArrays.grow(array, size + 1);
	 if (firstIndexValid) {
	  if (c == null) { if (( (refArray[x]) < (refArray[array[firstIndex]]) )) firstIndex = size; }
	  else if (c.compare(refArray[x], refArray[array[firstIndex]]) < 0) firstIndex = size;
	 }
	 else firstIndexValid = false;
	 array[size++] = x;
	}
	@Override
	public int dequeue() {
	 ensureNonEmpty();
	 final int firstIndex = findFirst();
	 final int result = array[firstIndex];
	 if (--size != 0) System.arraycopy(array, firstIndex + 1, array, firstIndex, size - firstIndex);
	 firstIndexValid = false;
	 return result;
	}
	@Override
	public int first() {
	 ensureNonEmpty();
	 return array[findFirst()];
	}
	@Override
	public int last() {
	 ensureNonEmpty();
	 return array[findLast()];
	}
	@Override
	public void changed() {
	 ensureNonEmpty();
	 firstIndexValid = false;
	}
	/** {@inheritDoc}
	 *
	 * <p>Note that for efficiency reasons this method will <em>not</em> throw an exception
	 * when {@code index} is not in the queue.
	 */
	@Override
	public void changed(int index) {
	 ensureElement(index);
	 if (index == firstIndex) firstIndexValid = false;
	}
	/** Signals the queue that all elements have changed. */
	@Override
	public void allChanged() { firstIndexValid = false; }
	@Override
	public boolean remove(int index) {
	 ensureElement(index);
	 final int[] a = array;
	 int i = size;
	 while(i-- != 0) if (a[i] == index) break;
	 if (i < 0) return false;
	 firstIndexValid = false;
	 if (--size != 0) System.arraycopy(a, i + 1, a, i, size - i);
	 return true;
	}
	/** Writes in the provided array the <em>front</em> of the queue, that is, the set of indices
	 * whose elements have the same priority as the top.
	 * @param a an array whose initial part will be filled with the frnot (must be sized as least as the heap size).
	 * @return the number of elements of the front.
	 */
	@Override
	public int front(int[] a) {
	 final char top = refArray[array[findFirst()]];
	 int i = size, c = 0;
	 while(i-- != 0) if (( (top) == (refArray[array[i]]) )) a[c++] = array[i];
	 return c;
	}
	@Override
	public int size() { return size; }
	@Override
	public void clear() { size = 0; firstIndexValid = false; }
	/** Trims the backing array so that it has exactly {@link #size()} elements. */
	public void trim() {
	 array = IntArrays.trim(array, size);
	}
	@Override
	public CharComparator comparator() { return c; }
	@Override
	public String toString() {
	 StringBuffer s = new StringBuffer();
	 s.append("[");
	 for (int i = 0; i < size; i++) {
	  if (i != 0) s.append(", ");
	  s.append(refArray[array [i]]);
	 }
	 s.append("]");
	 return s.toString();
	}
}
