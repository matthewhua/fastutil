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
package it.unimi.dsi.fastutil.floats;
import static it.unimi.dsi.fastutil.BigArrays.grow;
import static it.unimi.dsi.fastutil.BigArrays.length;
import static it.unimi.dsi.fastutil.BigArrays.set;
import static it.unimi.dsi.fastutil.BigArrays.trim;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.PrimitiveIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
/** A class providing static methods and objects that do useful things with type-specific iterators.
	*
	* @see Iterator
	*/
public final class FloatIterators {
	private FloatIterators() {}
	/** A class returning no elements and a type-specific iterator interface.
	 *
	 * <p>This class may be useful to implement your own in case you subclass
	 * a type-specific iterator.
	 */
	public static class EmptyIterator implements FloatListIterator , java.io.Serializable, Cloneable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 protected EmptyIterator() {}
	 @Override
	 public boolean hasNext() { return false; }
	 @Override
	 public boolean hasPrevious() { return false; }
	 @Override
	 public float nextFloat() { throw new NoSuchElementException(); }
	 @Override
	 public float previousFloat() { throw new NoSuchElementException(); }
	 @Override
	 public int nextIndex() { return 0; }
	 @Override
	 public int previousIndex() { return -1; }
	 @Override
	 public int skip(int n) { return 0; }
	 @Override
	 public int back(int n) { return 0; }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) { }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) { }
	 @Override
	 public Object clone() { return EMPTY_ITERATOR; }
	 private Object readResolve() { return EMPTY_ITERATOR; }
	}
	/** An empty iterator. It is serializable and cloneable.
	 *
	 * <p>The class of this objects represent an abstract empty iterator
	 * that can iterate as a type-specific (list) iterator.
	 */

	public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
	/** An iterator returning a single element. */
	private static class SingletonIterator implements FloatListIterator {
	 private final float element;
	 private byte curr;
	 public SingletonIterator(final float element) {
	  this.element = element;
	 }
	 @Override
	 public boolean hasNext() { return curr == 0; }
	 @Override
	 public boolean hasPrevious() { return curr == 1; }
	 @Override
	 public float nextFloat() {
	  if (! hasNext()) throw new NoSuchElementException();
	  curr = 1;
	  return element;
	 }
	 @Override
	 public float previousFloat() {
	  if (! hasPrevious()) throw new NoSuchElementException();
	  curr = 0;
	  return element;
	 }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  Objects.requireNonNull(action);
	  if (curr == 0) {
	   action.accept(element);
	   curr = 1;
	  }
	 }
	 @Override
	 public int nextIndex() {
	  return curr;
	 }
	 @Override
	 public int previousIndex() {
	  return curr - 1;
	 }
	 @Override
	 public int back(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n == 0 || curr < 1) return 0;
	  curr = 1;
	  return 1;
	 }
	 @Override
	 public int skip(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n == 0 || curr > 0) return 0;
	  curr = 0;
	  return 1;
	 }
	}
	/** Returns an immutable iterator that iterates just over the given element.
	 *
	 * @param element the only element to be returned by a type-specific list iterator.
	 * @return an immutable iterator that iterates just over {@code element}.
	 */
	public static FloatListIterator singleton(final float element) {
	 return new SingletonIterator (element);
	}
	/** A class to wrap arrays in iterators. */
	private static class ArrayIterator implements FloatListIterator {
	 private final float[] array;
	 private final int offset, length;
	 private int curr;
	 public ArrayIterator(final float[] array, final int offset, final int length) {
	  this.array = array;
	  this.offset = offset;
	  this.length = length;
	 }
	 @Override
	 public boolean hasNext() { return curr < length; }
	 @Override
	 public boolean hasPrevious() { return curr > 0; }
	 @Override
	 public float nextFloat() {
	  if (! hasNext()) throw new NoSuchElementException();
	  return array[offset + curr++];
	 }
	 @Override
	 public float previousFloat() {
	  if (! hasPrevious()) throw new NoSuchElementException();
	  return array[offset + --curr];
	 }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  Objects.requireNonNull(action);
	  for (; curr < length; ++curr) {
	   action.accept(array[offset + curr]);
	  }
	 }
	 @Override
	 public int skip(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n <= length - curr) {
	   curr += n;
	   return n;
	  }
	  n = length - curr;
	  curr = length;
	  return n;
	 }
	 @Override
	 public int back(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  if (n <= curr) {
	   curr -= n;
	   return n;
	  }
	  n = curr;
	  curr = 0;
	  return n;
	 }
	 @Override
	 public int nextIndex() {
	  return curr;
	 }
	 @Override
	 public int previousIndex() {
	  return curr - 1;
	 }
	}
	/** Wraps the given part of an array into a type-specific list iterator.
	 *
	 * <p>The type-specific list iterator returned by this method will iterate
	 * {@code length} times, returning consecutive elements of the given
	 * array starting from the one with index {@code offset}.
	 *
	 * @param array an array to wrap into a type-specific list iterator.
	 * @param offset the first element of the array to be returned.
	 * @param length the number of elements to return.
	 * @return an iterator that will return {@code length} elements of {@code array} starting at position {@code offset}.
	 */
	public static FloatListIterator wrap(final float[] array, final int offset, final int length) {
	 FloatArrays.ensureOffsetLength(array, offset, length);
	 return new ArrayIterator (array, offset, length);
	}
	/** Wraps the given array into a type-specific list iterator.
	 *
	 * <p>The type-specific list iterator returned by this method will return
	 * all elements of the given array.
	 *
	 * @param array an array to wrap into a type-specific list iterator.
	 * @return an iterator that will return the elements of {@code array}.
	 */
	public static FloatListIterator wrap(final float[] array) {
	 return new ArrayIterator (array, 0, array.length);
	}
	/** Unwraps an iterator into an array starting at a given offset for a given number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the elements
	 * returned, up to a maximum of {@code length}, in the given array starting at {@code offset}.
	 * The number of actually unwrapped elements is returned (it may be less than {@code max} if
	 * the iterator emits less than {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param array an array to contain the output of the iterator.
	 * @param offset the first element of the array to be returned.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped.
	 */
	public static int unwrap(final FloatIterator i, final float array[], int offset, final int max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 if (offset < 0 || offset + max > array.length) throw new IllegalArgumentException();
	 int j = max;
	 while(j-- != 0 && i.hasNext()) array[offset++] = i.nextFloat();
	 return max - j - 1;
	}
	/** Unwraps an iterator into an array.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the
	 * elements returned in the given array. The iteration will stop when the
	 * iterator has no more elements or when the end of the array has been reached.
	 *
	 * @param i a type-specific iterator.
	 * @param array an array to contain the output of the iterator.
	 * @return the number of elements unwrapped.
	 */
	public static int unwrap(final FloatIterator i, final float array[]) {
	 return unwrap(i, array, 0, array.length);
	}
	/** Unwraps an iterator, returning an array, with a limit on the number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns an array
	 * containing the elements returned by the iterator. At most {@code max} elements
	 * will be returned.
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be unwrapped.
	 * @return an array containing the elements returned by the iterator (at most {@code max}).
	 */

	public static float[] unwrap(final FloatIterator i, int max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 float array[] = new float[16];
	 int j = 0;
	 while(max-- != 0 && i.hasNext()) {
	  if (j == array.length) array = FloatArrays.grow(array, j + 1);
	  array[j++] = i.nextFloat();
	 }
	 return FloatArrays.trim(array, j);
	}
	/** Unwraps an iterator, returning an array.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns an array
	 * containing the elements returned by the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return an array containing the elements returned by the iterator.
	 */
	public static float[] unwrap(final FloatIterator i) {
	 return unwrap(i, Integer.MAX_VALUE);
	}
	/** Unwraps an iterator into a big array starting at a given offset for a given number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the elements
	 * returned, up to a maximum of {@code length}, in the given big array starting at {@code offset}.
	 * The number of actually unwrapped elements is returned (it may be less than {@code max} if
	 * the iterator emits less than {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param array a big array to contain the output of the iterator.
	 * @param offset the first element of the array to be returned.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped.
	 */
	public static long unwrap(final FloatIterator i, final float array[][], long offset, final long max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 if (offset < 0 || offset + max > length(array)) throw new IllegalArgumentException();
	 long j = max;
	 while(j-- != 0 && i.hasNext()) set(array, offset++, i.nextFloat());
	 return max - j - 1;
	}
	/** Unwraps an iterator into a big array.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the
	 * elements returned in the given big array. The iteration will stop when the
	 * iterator has no more elements or when the end of the array has been reached.
	 *
	 * @param i a type-specific iterator.
	 * @param array a big array to contain the output of the iterator.
	 * @return the number of elements unwrapped.
	 */
	public static long unwrap(final FloatIterator i, final float array[][]) {
	 return unwrap(i, array, 0, length(array));
	}
	/** Unwraps an iterator into a type-specific collection, with a limit on the number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the elements
	 * returned, up to a maximum of {@code max}, in the given type-specific collection.
	 * The number of actually unwrapped elements is returned (it may be less than {@code max} if
	 * the iterator emits less than {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param c a type-specific collection array to contain the output of the iterator.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped. Note that
	 * this is the number of elements returned by the iterator, which is not necessarily the number
	 * of elements that have been added to the collection (because of duplicates).
	 */
	public static int unwrap(final FloatIterator i, final FloatCollection c, final int max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 int j = max;
	 while(j-- != 0 && i.hasNext()) c.add(i.nextFloat());
	 return max - j - 1;
	}
	/** Unwraps an iterator, returning a big array, with a limit on the number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns a big array
	 * containing the elements returned by the iterator. At most {@code max} elements
	 * will be returned.
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be unwrapped.
	 * @return a big array containing the elements returned by the iterator (at most {@code max}).
	 */

	public static float[][] unwrapBig(final FloatIterator i, long max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 float array[][] = FloatBigArrays.newBigArray(16);
	 long j = 0;
	 while(max-- != 0 && i.hasNext()) {
	  if (j == length(array)) array = grow(array, j + 1);
	  set(array, j++, i.nextFloat());
	 }
	 return trim(array, j);
	}
	/** Unwraps an iterator, returning a big array.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns a big array
	 * containing the elements returned by the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return a big array containing the elements returned by the iterator.
	 */
	public static float[][] unwrapBig(final FloatIterator i) {
	 return unwrapBig(i, Long.MAX_VALUE);
	}
	/** Unwraps an iterator into a type-specific collection.
	 *
	 * <p>This method iterates over the given type-specific iterator and stores the
	 * elements returned in the given type-specific collection. The returned count on the number
	 * unwrapped elements is a long, so that it will work also with very large collections.
	 *
	 * @param i a type-specific iterator.
	 * @param c a type-specific collection to contain the output of the iterator.
	 * @return the number of elements unwrapped. Note that
	 * this is the number of elements returned by the iterator, which is not necessarily the number
	 * of elements that have been added to the collection (because of duplicates).
	 */
	public static long unwrap(final FloatIterator i, final FloatCollection c) {
	 long n = 0;
	 while(i.hasNext()) {
	  c.add(i.nextFloat());
	  n++;
	 }
	 return n;
	}
	/** Pours an iterator into a type-specific collection, with a limit on the number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and adds
	 * the returned elements to the given collection (up to {@code max}).
	 *
	 * @param i a type-specific iterator.
	 * @param s a type-specific collection.
	 * @param max the maximum number of elements to be poured.
	 * @return the number of elements poured. Note that
	 * this is the number of elements returned by the iterator, which is not necessarily the number
	 * of elements that have been added to the collection (because of duplicates).
	 */
	public static int pour(final FloatIterator i, final FloatCollection s, final int max) {
	 if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
	 int j = max;
	 while(j-- != 0 && i.hasNext()) s.add(i.nextFloat());
	 return max - j - 1;
	}
	/** Pours an iterator into a type-specific collection.
	 *
	 * <p>This method iterates over the given type-specific iterator and adds
	 * the returned elements to the given collection.
	 *
	 * @param i a type-specific iterator.
	 * @param s a type-specific collection.
	 * @return the number of elements poured. Note that
	 * this is the number of elements returned by the iterator, which is not necessarily the number
	 * of elements that have been added to the collection (because of duplicates).
	 */
	public static int pour(final FloatIterator i, final FloatCollection s) {
	 return pour(i, s, Integer.MAX_VALUE);
	}
	/** Pours an iterator, returning a type-specific list, with a limit on the number of elements.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns
	 * a type-specific list containing the returned elements (up to {@code max}). Iteration
	 * on the returned list is guaranteed to produce the elements in the same order
	 * in which they appeared in the iterator.
	 *
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be poured.
	 * @return a type-specific list containing the returned elements, up to {@code max}.
	 */
	public static FloatList pour(final FloatIterator i, int max) {
	 final FloatArrayList l = new FloatArrayList ();
	 pour(i, l, max);
	 l.trim();
	 return l;
	}
	/** Pours an iterator, returning a type-specific list.
	 *
	 * <p>This method iterates over the given type-specific iterator and returns
	 * a list containing the returned elements. Iteration
	 * on the returned list is guaranteed to produce the elements in the same order
	 * in which they appeared in the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return a type-specific list containing the returned elements.
	 */
	public static FloatList pour(final FloatIterator i) {
	 return pour(i, Integer.MAX_VALUE);
	}
	private static class IteratorWrapper implements FloatIterator {
	 final Iterator<Float> i;
	 public IteratorWrapper(final Iterator<Float> i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public void remove() { i.remove(); }
	 @Override
	 public float nextFloat() { return (i.next()).floatValue(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  i.forEachRemaining(action);
	 }
	}
	private static class PrimitiveIteratorWrapper implements FloatIterator {
	 final PrimitiveIterator.OfDouble i;
	 public PrimitiveIteratorWrapper(PrimitiveIterator.OfDouble i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public void remove() { i.remove(); }
	 @Override
	 public float nextFloat() { return (float)i.nextDouble(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	}
	private static class CheckedPrimitiveIteratorWrapper extends PrimitiveIteratorWrapper {
	 public CheckedPrimitiveIteratorWrapper(PrimitiveIterator.OfDouble i) {
	  super(i);
	 }
	 @Override
	 public float nextFloat() { return it.unimi.dsi.fastutil.SafeMath.safeDoubleToFloat(i.nextDouble()); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining((java.util.function.DoubleConsumer)(double value) -> { action.accept(it.unimi.dsi.fastutil.SafeMath.safeDoubleToFloat(value)); });
	 }
	}
	/** Wraps a standard iterator into a type-specific iterator.
	 *
	 * <p>This method wraps a standard iterator into a type-specific one which will handle the
	 * type conversions for you. Of course, any attempt to wrap an iterator returning the
	 * instances of the wrong class will generate a {@link ClassCastException}. The
	 * returned iterator is backed by {@code i}: changes to one of the iterators
	 * will affect the other, too.
	 *
	 * @implNote If {@code i} is already type-specific, it will returned and no new object
	 * will be generated.
	 *
	 * @param i an iterator.
	 * @return a type-specific iterator backed by {@code i}.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static FloatIterator asFloatIterator(final Iterator i) {
	 if (i instanceof FloatIterator) return (FloatIterator )i;
	 return new IteratorWrapper (i);
	}
	/** Wrap a JDK primitive iterator to a type-specific iterator, making checked
	 * narrowed casts.
	 *
	 * @implNote The {@code next} method throws {@link IllegalArgumentException} if any element would underflow or overflow.
	 *
	 * @param i an iterator.
	 * @return a type-specific iterator backed by {@code i}.
	 * @since 8.5.0
	 */
	public static FloatIterator narrow(final PrimitiveIterator.OfDouble i) {
	 return new CheckedPrimitiveIteratorWrapper (i);
	}
	/** Wrap a JDK primitive iterator to a type-specific iterator, making <em>unchecked</em>
	 * narrowing casts.
	 *
	 * <p><em>No</em> test is done for overflow or underflow.
	 *
	 * @param i an iterator.
	 * @return a type-specific iterator backed by {@code i}.
	 * @since 8.5.0
	 */
	public static FloatIterator uncheckedNarrow(final PrimitiveIterator.OfDouble i) {
	 return new PrimitiveIteratorWrapper (i);
	}
	/** Wrap a type-specific iterator to a JDK compatible primitive iterator.
	 *
	 * @param i an iterator
	 * @return a JDK compatible primitive iterator backed by {@code i}
	 * @since 8.5.0
	 */
	public static DoubleIterator widen(FloatIterator i) { return DoubleIterators.wrap(i); }
	private static class ListIteratorWrapper implements FloatListIterator {
	 final ListIterator<Float> i;
	 public ListIteratorWrapper(final ListIterator<Float> i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public boolean hasPrevious() { return i.hasPrevious(); }
	 @Override
	 public int nextIndex() { return i.nextIndex(); }
	 @Override
	 public int previousIndex() { return i.previousIndex(); }
	 @Override
	 public void set(float k) { i.set(Float.valueOf(k)); }
	 @Override
	 public void add(float k) { i.add(Float.valueOf(k)); }
	 @Override
	 public void remove() { i.remove(); }
	 @Override
	 public float nextFloat() { return (i.next()).floatValue(); }
	 @Override
	 public float previousFloat() { return (i.previous()).floatValue(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  i.forEachRemaining(action);
	 }
	}
	/** Wraps a standard list iterator into a type-specific list iterator.
	 *
	 * <p>This method wraps a standard list iterator into a type-specific one
	 * which will handle the type conversions for you. Of course, any attempt
	 * to wrap an iterator returning the instances of the wrong class will
	 * generate a {@link ClassCastException}. The
	 * returned iterator is backed by {@code i}: changes to one of the iterators
	 * will affect the other, too.
	 *
	 * <p>If {@code i} is already type-specific, it will returned and no new object
	 * will be generated.
	 *
	 * @param i a list iterator.
	 * @return a type-specific list iterator backed by {@code i}.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static FloatListIterator asFloatIterator(final ListIterator i) {
	 if (i instanceof FloatListIterator) return (FloatListIterator )i;
	 return new ListIteratorWrapper (i);
	}
	/**
	 * Returns whether an element returned by the given iterator satisfies the given predicate.
	 * <p>Short circuit evaluation is performed; the first {@code true} from the predicate terminates the loop.
	 * @return true if an element returned by {@code iterator} satisfies {@code predicate}.
	 */
	public static boolean any(final FloatIterator iterator, final FloatPredicate predicate) {
	 return indexOf(iterator, predicate) != -1;
	}
	/**
	 * Returns whether an element returned by the given iterator satisfies the given predicate.
	 * <p>Short circuit evaluation is performed; the first {@code true} from the predicate terminates the loop.
	 * @return true if an element returned by {@code iterator} satisfies {@code predicate}.
	 *   lambda to perform widening casts. Please use the type-specific overload to avoid this overhead.
	 */
	public static boolean any(final FloatIterator iterator, final java.util.function.DoublePredicate predicate) {
	 return any(iterator, predicate instanceof FloatPredicate ? (FloatPredicate ) predicate : (FloatPredicate ) predicate::test);
	}
	/**
	 * Returns whether all elements returned by the given iterator satisfy the given predicate.
	 * <p>Short circuit evaluation is performed; the first {@code false} from the predicate terminates the loop.
	 * @return true if all elements returned by {@code iterator} satisfy {@code predicate}.
	 */
	public static boolean all(final FloatIterator iterator, final FloatPredicate predicate) {
	 Objects.requireNonNull(predicate);
	 do {
	  if (!iterator.hasNext()) return true;
	 } while (predicate.test(iterator.nextFloat()));
	 return false;
	}
	/**
	 * Returns whether all elements returned by the given iterator satisfy the given predicate.
	 * <p>Short circuit evaluation is performed; the first {@code false} from the predicate terminates the loop.
	 * @return true if all elements returned by {@code iterator} satisfy {@code predicate}.
	 * @implNote Unless the argument is type-specific, this method will introduce an intermediary
	 *   lambda to perform widening casts. Please use the type-specific overload to avoid this overhead.
	 */
	public static boolean all(final FloatIterator iterator, final java.util.function.DoublePredicate predicate) {
	 return all(iterator, predicate instanceof FloatPredicate ? (FloatPredicate ) predicate : (FloatPredicate ) predicate::test);
	}
	/**
	 * Returns the index of the first element returned by the given iterator that satisfies the given predicate, or &minus;1 if
	 * no such element was found.
	 * <p>The next element returned by the iterator always considered element 0, even for
	 * {@link java.util.ListIterator ListIterators}. In other words {@link java.util.ListIterator#nextIndex
	 * ListIterator.nextIndex} is ignored.
	 * @return the index of the first element returned by {@code iterator} that satisfies {@code predicate}, or &minus;1 if
	 * no such element was found.
	 */
	public static int indexOf(final FloatIterator iterator, final FloatPredicate predicate) {
	 Objects.requireNonNull(predicate);
	 for (int i = 0; iterator.hasNext(); ++i) {
	  if (predicate.test(iterator.nextFloat())) return i;
	 }
	 return -1;
	}
	/**
	 * Returns the index of the first element returned by the given iterator that satisfies the given predicate, or &minus;1 if
	 * no such element was found.
	 * <p>The next element returned by the iterator always considered element 0, even for
	 * {@link java.util.ListIterator ListIterators}. In other words {@link java.util.ListIterator#nextIndex
	 * ListIterator.nextIndex} is ignored.
	 * @return the index of the first element returned by {@code iterator} that satisfies {@code predicate}, or &minus;1 if
	 * no such element was found.
	 * @implNote Unless the argument is type-specific, this method will introduce an intermediary
	 *   lambda to perform widening casts. Please use the type-specific overload to avoid this overhead.
	 */
	public static int indexOf(final FloatIterator iterator, final java.util.function.DoublePredicate predicate) {
	 return indexOf(iterator, predicate instanceof FloatPredicate ? (FloatPredicate ) predicate : (FloatPredicate ) predicate::test);
	}
	/**
	 * A skeletal implementation for an iterator backed by an index-based data store. High performance
	 * concrete implementations (like the main Iterator of ArrayList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>This class is only appropriate for sequences that are at most {@link Integer#MAX_VALUE} long.
	 * If your backing data store can be bigger then this, consider the equivalently named class in
	 * the type specific {@code BigListIterators} class.
	 *
	 * <p>As the abstract methods in this class are used in inner loops, it is generally a
	 * good idea to override the class as {@code final} as to encourage the JVM to inline
	 * them (or alternatively, override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedIterator extends AbstractFloatIterator {
	 /** The minimum pos can be, and is the logical start of the "range".
		 * Usually set to the initialPos unless it is a ListIterator, in which case it can vary.
		 *
		 * There isn't any way for a range to shift its beginning like the end can (through {@link #remove}),
		 * so this is final.
		 */
	 protected final int minPos;
	 /** The current position index, the index of the item to be returned after the next call to {@link #next()}.
		 *
		 * <p>This value will be between {@code minPos} and {@link #getMaxPos()} (exclusive) (on a best effort, so concurrent
		 * structural modifications outside this iterator may cause this to be violated, but that usually invalidates
		 * iterators anyways). Thus {@code pos} being {@code minPos + 2} would mean {@link #next()}
		 * was called twice and the next call will return the third element of this iterator.
		 */
	 protected int pos;
	 /** The last returned index by a call to {@link #next} or, if a list-iterator, {@link java.util.ListIterator#previous().
		 *
		 * It is &minus;1 if no such call has occurred or a mutation has occurred through this iterator and no
		 * advancement has been done.
		 */
	 protected int lastReturned;
	 protected AbstractIndexBasedIterator(int minPos, int initialPos) {
	  this.minPos = minPos;
	  this.pos = initialPos;
	 }
	 // When you implement these, you should probably declare them final to encourage the JVM to inline them.
	 /** Get the item corresponding to the given index location.
		 *
		 * <p>Do <em>not</em> advance {@link #pos} in this method; the default {@code next} method takes care of this.
		 *
		 * <p>The {@code location} given will be between {@code minPos} and {@link #getMaxPos()} (exclusive).
		 * Thus, a {@code location} of {@code minPos + 2} would mean {@link #next()} was called twice
		 * and this method should return what the next call to {@link #next()} should return.
		 */
	 protected abstract float get(int location);
	 /** Remove the item at the given index.
		 *
		 * <p>Do <em>not</em> modify {@link #pos} in this method; the default {@code #remove()} method takes care of this.
		 *
		 * <p>This method should also do what is needed to track the change to the {@link #getMaxPos}.
		 * Usually this is accomplished by having this method call the parent {@link Collection}'s appropriate remove
		 * method, and having {@link #getMaxPos} track the parent {@linkplain Collection#size() collection's size}.
		 */
	 protected abstract void remove(int location);
	 /** The maximum pos can be, and is the logical end (exclusive) of the "range".
		 *
		 * <p>If pos is equal to the return of this method, this means the last element has been returned and the next call to {@link #next()} will throw.
		 *
		 * <p>Usually set return the parent {@linkplain Collection#size() collection's size}, but does not have to be
		 * (for example, sublists and subranges).
		 */
	 protected abstract int getMaxPos();
	 @Override
	 public boolean hasNext() { return pos < getMaxPos(); }
	 @Override
	 public float nextFloat() { if (! hasNext()) throw new NoSuchElementException(); return get(lastReturned = pos++); }
	 @Override
	 public void remove() {
	  if (lastReturned == -1) throw new IllegalStateException();
	  remove(lastReturned);
	  /* If the last operation was a next(), we are removing an element *before* us, and we must decrease pos correspondingly. */
	  if (lastReturned < pos) pos--;
	  lastReturned = -1;
	 }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  while(pos < getMaxPos()) {
	   action.accept(get(lastReturned = pos++));
	  }
	 }
	 // TODO since this method doesn't depend on the type at all, should it be "hoisted" into a
	 // non type-specific superclass in it.unimi.dsi.fastutil?
	 @Override
	 public int skip(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  final int max = getMaxPos();
	  final int remaining = max - pos;
	  if (n < remaining) {
	   pos += n;
	  } else {
	   n = remaining;
	   pos = max;
	  }
	  lastReturned = pos - 1;
	  return n;
	 }
	}
	/**
	 * A skeletal implementation for a list-iterator backed by an index-based data store. High performance
	 * concrete implementations (like the main ListIterator of ArrayList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>This class is only appropriate for sequences that are at most {@link Integer#MAX_VALUE} long.
	 * If your backing data store can be bigger then this, consider the equivalently named class in
	 * the type specific {@code BigListSpliterators} class.
	 *
	 * <p>As the abstract methods in this class are used in inner loops, it is generally a
	 * good idea to override the class as {@code final} as to encourage the JVM to inline
	 * them (or alternatively, override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedListIterator extends AbstractIndexBasedIterator implements FloatListIterator {
	 protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
	  super(minPos, initialPos);
	 }
	 // When you implement these, you should probably declare them final to encourage the JVM to inline them.
	 /** Add the given item at the given index.
		 *
		 * <p>This method should also do what is needed to track the change to the {@link #getMaxPos}.
		 * Usually this is accomplished by having this method call the parent {@link Collection}'s appropriate add
		 * method, and having {@link #getMaxPos} track the parent {@linkplain Collection#size() collection's size}.
		 *
		 * <p>Do <em>not</em> modify {@link #pos} in this method; the default {@code #add()} method takes care of this.
		 *
		 * <p>See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
	 protected abstract void add(int location, float k);
	 /** Sets the given item at the given index.
		 *
		 * <p>See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
	 protected abstract void set(int location, float k);
	 @Override
	 public boolean hasPrevious() { return pos > minPos; }
	 @Override
	 public float previousFloat() { if (! hasPrevious()) throw new NoSuchElementException(); return get(lastReturned = --pos); }
	 @Override
	 public int nextIndex() { return pos; }
	 @Override
	 public int previousIndex() { return pos - 1; }
	 @Override
	 public void add(final float k) {
	  add(pos++, k);
	  lastReturned = -1;
	 }
	 @Override
	 public void set(final float k) {
	  if (lastReturned == -1) throw new IllegalStateException();
	  set(lastReturned, k);
	 }
	 // TODO since this method doesn't depend on the type at all, should it be "hoisted" into a
	 // non type-specific superclass in it.unimi.dsi.fastutil?
	 @Override
	 public int back(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  final int remaining = pos - minPos;
	  if (n < remaining) {
	   pos -= n;
	  } else {
	   n = remaining;
	   pos = minPos;
	  }
	  lastReturned = pos;
	  return n;
	 }
	}
	private static class IteratorConcatenator implements FloatIterator {
	 final FloatIterator a[];
	 int offset, length, lastOffset = -1;
	 public IteratorConcatenator(final FloatIterator a[], int offset, int length) {
	  this.a = a;
	  this.offset = offset;
	  this.length = length;
	  advance();
	 }
	 private void advance() {
	  while(length != 0) {
	   if (a[offset].hasNext()) break;
	   length--;
	   offset++;
	  }
	  return;
	 }
	 @Override
	 public boolean hasNext() {
	  return length > 0;
	 }
	 @Override
	 public float nextFloat() {
	  if (! hasNext()) throw new NoSuchElementException();
	  float next = a[lastOffset = offset].nextFloat();
	  advance();
	  return next;
	 }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  while (length > 0) {
	   a[lastOffset = offset].forEachRemaining(action);
	   advance();
	  }
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  while (length > 0) {
	   a[lastOffset = offset].forEachRemaining(action);
	   advance();
	  }
	 }
	 @Override
	 public void remove() {
	  if (lastOffset == -1) throw new IllegalStateException();
	  a[lastOffset].remove();
	 }
	 @Override
	 public int skip(int n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  lastOffset = -1;
	  int skipped = 0;
	  while(skipped < n && length != 0) {
	   skipped += a[offset].skip(n - skipped);
	   if (a[offset].hasNext()) break;
	   length--;
	   offset++;
	  }
	  return skipped;
	 }
	}
	/** Concatenates all iterators contained in an array.
	 *
	 * <p>This method returns an iterator that will enumerate in order the elements returned
	 * by all iterators contained in the given array.
	 *
	 * @param a an array of iterators.
	 * @return an iterator obtained by concatenation.
	 */
	public static FloatIterator concat(final FloatIterator ... a) {
	 return concat(a, 0, a.length);
	}
	/** Concatenates a sequence of iterators contained in an array.
	 *
	 * <p>This method returns an iterator that will enumerate in order the elements returned
	 * by {@code a[offset]}, then those returned
	 * by {@code a[offset + 1]}, and so on up to
	 * {@code a[offset + length - 1]}.
	 *
	 * @param a an array of iterators.
	 * @param offset the index of the first iterator to concatenate.
	 * @param length the number of iterators to concatenate.
	 * @return an iterator obtained by concatenation of {@code length} elements of {@code a} starting at {@code offset}.
	 */
	public static FloatIterator concat(final FloatIterator a[], final int offset, final int length) {
	 return new IteratorConcatenator (a, offset, length);
	}
	/** An unmodifiable wrapper class for iterators. */
	public static class UnmodifiableIterator implements FloatIterator {
	 protected final FloatIterator i;
	 public UnmodifiableIterator(final FloatIterator i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public float nextFloat() { return i.nextFloat(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  i.forEachRemaining(action);
	 }
	}
	/** Returns an unmodifiable iterator backed by the specified iterator.
	 *
	 * @param i the iterator to be wrapped in an unmodifiable iterator.
	 * @return an unmodifiable view of the specified iterator.
	 */
	public static FloatIterator unmodifiable(final FloatIterator i) { return new UnmodifiableIterator (i); }
	/** An unmodifiable wrapper class for bidirectional iterators. */
	public static class UnmodifiableBidirectionalIterator implements FloatBidirectionalIterator {
	 protected final FloatBidirectionalIterator i;
	 public UnmodifiableBidirectionalIterator(final FloatBidirectionalIterator i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public boolean hasPrevious() { return i.hasPrevious(); }
	 @Override
	 public float nextFloat() { return i.nextFloat(); }
	 @Override
	 public float previousFloat() { return i.previousFloat(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  i.forEachRemaining(action);
	 }
	}
	/** Returns an unmodifiable bidirectional iterator backed by the specified bidirectional iterator.
	 *
	 * @param i the bidirectional iterator to be wrapped in an unmodifiable bidirectional iterator.
	 * @return an unmodifiable view of the specified bidirectional iterator.
	 */
	public static FloatBidirectionalIterator unmodifiable(final FloatBidirectionalIterator i) { return new UnmodifiableBidirectionalIterator (i); }
	/** An unmodifiable wrapper class for list iterators. */
	public static class UnmodifiableListIterator implements FloatListIterator {
	 protected final FloatListIterator i;
	 public UnmodifiableListIterator(final FloatListIterator i) {
	  this.i = i;
	 }
	 @Override
	 public boolean hasNext() { return i.hasNext(); }
	 @Override
	 public boolean hasPrevious() { return i.hasPrevious(); }
	 @Override
	 public float nextFloat() { return i.nextFloat(); }
	 @Override
	 public float previousFloat() { return i.previousFloat(); }
	 @Override
	 public int nextIndex() { return i.nextIndex(); }
	 @Override
	 public int previousIndex() { return i.previousIndex(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  i.forEachRemaining(action);
	 }
	 @Deprecated
	 @Override
	 public void forEachRemaining(final Consumer<? super Float> action) {
	  i.forEachRemaining(action);
	 }
	}
	/** Returns an unmodifiable list iterator backed by the specified list iterator.
	 *
	 * @param i the list iterator to be wrapped in an unmodifiable list iterator.
	 * @return an unmodifiable view of the specified list iterator.
	 */
	public static FloatListIterator unmodifiable(final FloatListIterator i) { return new UnmodifiableListIterator (i); }
	/** A wrapper promoting the results of a ByteIterator. */
	private final static class ByteIteratorWrapper implements FloatIterator {
	 final it.unimi.dsi.fastutil.bytes.ByteIterator iterator;
	 public ByteIteratorWrapper(final it.unimi.dsi.fastutil.bytes.ByteIterator iterator) {
	  this.iterator = iterator;
	 }
	 @Override
	 public boolean hasNext() { return iterator.hasNext(); }
	 @Deprecated
	 @Override
	 public Float next() { return Float.valueOf(iterator.nextByte()); }
	 @Override
	 public float nextFloat() { return iterator.nextByte(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  Objects.requireNonNull(action);
	  iterator.forEachRemaining(action::accept);
	 }
	 @Override
	 public void remove() { iterator.remove(); }
	 @Override
	 public int skip(final int n) { return iterator.skip(n); }
	}
	/** Returns an iterator backed by the specified byte iterator.
	 * @param iterator a byte iterator.
	 * @return an iterator backed by the specified byte iterator.
	 */
	public static FloatIterator wrap(final it.unimi.dsi.fastutil.bytes.ByteIterator iterator) {
	 return new ByteIteratorWrapper(iterator);
	}
	/** A wrapper promoting the results of a ShortIterator. */
	private final static class ShortIteratorWrapper implements FloatIterator {
	 final it.unimi.dsi.fastutil.shorts.ShortIterator iterator;
	 public ShortIteratorWrapper(final it.unimi.dsi.fastutil.shorts.ShortIterator iterator) {
	  this.iterator = iterator;
	 }
	 @Override
	 public boolean hasNext() { return iterator.hasNext(); }
	 @Deprecated
	 @Override
	 public Float next() { return Float.valueOf(iterator.nextShort()); }
	 @Override
	 public float nextFloat() { return iterator.nextShort(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  Objects.requireNonNull(action);
	  iterator.forEachRemaining(action::accept);
	 }
	 @Override
	 public void remove() { iterator.remove(); }
	 @Override
	 public int skip(final int n) { return iterator.skip(n); }
	}
	/** Returns an iterator backed by the specified short iterator.
	 * @param iterator a short iterator.
	 * @return an iterator backed by the specified short iterator.
	 */
	public static FloatIterator wrap(final it.unimi.dsi.fastutil.shorts.ShortIterator iterator) {
	 return new ShortIteratorWrapper(iterator);
	}
	/** A wrapper promoting the results of a CharIterator. */
	private final static class CharIteratorWrapper implements FloatIterator {
	 final it.unimi.dsi.fastutil.chars.CharIterator iterator;
	 public CharIteratorWrapper(final it.unimi.dsi.fastutil.chars.CharIterator iterator) {
	  this.iterator = iterator;
	 }
	 @Override
	 public boolean hasNext() { return iterator.hasNext(); }
	 @Deprecated
	 @Override
	 public Float next() { return Float.valueOf(iterator.nextChar()); }
	 @Override
	 public float nextFloat() { return iterator.nextChar(); }
	 @Override
	 public void forEachRemaining(final FloatConsumer action) {
	  Objects.requireNonNull(action);
	  iterator.forEachRemaining(action::accept);
	 }
	 @Override
	 public void remove() { iterator.remove(); }
	 @Override
	 public int skip(final int n) { return iterator.skip(n); }
	}
	/** Returns an iterator backed by the specified char iterator.
	 *
	 * <p><b>WARNING</b>: This is <em>not</em> the same as converting the source to a sequence
	 * of code points. This returned instance literally performs {@code (int)(charValue)} casts.
	 * Surrogate pairs will be left as separate elements instead of combined into a single element
	 * with the code point it represents. See {@link Character} for more discussion on code points,
	 * char values, and surrogate pairs.
	 *
	 * @param iterator a char iterator.
	 * @return an iterator backed by the specified char iterator.
	 */
	public static FloatIterator wrap(final it.unimi.dsi.fastutil.chars.CharIterator iterator) {
	 return new CharIteratorWrapper(iterator);
	}
}
