/*
	* Copyright (C) 2019-2022 Sebastiano Vigna
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
/** A class providing static methods and objects that do useful things with type-specific spliterators on
	* big (potentially greater then {@link Integer#MAX_VALUE} items long).
	*
	* Since the {@link java.util.Spliterator} interface already natively works in long indexes, most of the
	* utility methods reside in the regular {@code Spliterators} class.
	*
	* @author C. Sean Young &lt;csyoung@google.com&gt;
	*
	* @since 8.5.0
	*/
public final class CharBigSpliterators {
	/**
	 * A skeletal implementation for a spliterator backed by an index based data store. High performance
	 * concrete implementations (like the main Spliterator of BigArrayBigList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>As the abstract methods in this class are used in inner loops, it is generally a
	 * good idea to override the class as {@code final} as to encourage the JVM to inline
	 * them (or alternatively, override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedSpliterator extends AbstractCharSpliterator {
	 /** The current position index, the index of the item to be given after the next call to {@link #tryAdvance}.
		 *
		 * <p>This value will be between {@code minPos} and {@link #getMaxPos()} (exclusive) (on a best effort, so concurrent
		 * structural modifications may cause this to be violated, but that usually invalidates spliterators anyways).
		 * Thus {@code pos} being {@code minPos + 2} would mean {@link #tryAdvance}
		 * was called twice and the next call will give the third element of this spliterator.
		 */
	 protected long pos;
	 protected AbstractIndexBasedSpliterator(long initialPos) {
	  this.pos = initialPos;
	 }
	 // When you implement these, you should probably declare them final to encourage the JVM to inline them.
	 /** Get the item corresponding to the given index location.
		 *
		 * <p>Do <em>not</em> advance {@link #pos} in this method; the default {@link #tryAdvance} and
		 * {@link #forEachRemaining} methods takes care of this.
		 *
		 * <p>The {@code location} given will be between {@code minPos} and {@link #getMaxPos()} (exclusive).
		 * Thus, a {@code location} of {@code minPos + 2} would mean {@link #tryAdvance} was called twice
		 * and this method should return what the next call to {@link #tryAdvance()} should give.
		 */
	 protected abstract char get(long location);
	 /** The maximum pos can be, and is the logical end (exclusive) of the "range".
		 *
		 * <p>If pos is equal to the return of this method, this means the last element has been returned and the next call to {@link #tryAdvance} will return {@code false}.
		 *
		 * <p>Usually set return the parent {@linkplain java.util.Collection#size() collection's size}, but does not have to be
		 * (for example, sublists and subranges).
		 *
		 * <p>This method allows the implementation to decide how it binds on the size (late or early).
		 * However, {@link EarlyBindingSizeIndexBasedSpliterator} and {@link LateBindingSizeIndexBasedSpliterator} give
		 * an implementation of this method for the two most common strategies.
		 */
	 protected abstract long getMaxPos();
	 /** Make a new spliterator to {@link #trySplit()} starting with the given {@code pos}
		 * and ending at the given {@code maxPos}.
		 *
		 * <p>An implementation is free to look at the range given, and if it deems it too small
		 * to split further, return {@code null}. In which case, {@link #trySplit()} will not
		 * modify the state of this spliterator.
		 *
		 * <p>Do <em>not</em> modify {@link #pos} in this method; the default {@link #trySplit()}
		 * method takes care of this.
		 *
		 * <p>To comply with the spec of {@link java.util.Spliterator#ORDERED}, this will
		 * only be called to create prefixes of the current sequence this spliterator is over,
		 * and this instance will start at the end of the returned sequence and have the same
		 * end point.
		 * As such, this method should also not change what {@link #getMaxPos()} returns.
		 */
	 protected abstract CharSpliterator makeForSplit(long pos, long maxPos);
	 /** Compute where to split on the next {@link #trySplit()}, given the current pos and
		 * {@link #getMaxPos()} (or any other metric the implementation wishes to use).
		 *
		 * <p>If a value {@code == pos} or {@code == getMaxPos()} is returned, the
		 * {@link #trySplit()} method will assume a split of size 0 was computed,
		 * and thus won't split or change state. If a value outside that range is
		 * returned, then {@link #trySplit()} will throw {@link IndexOutOfBoundsException}.
		 * In particular, this means that no handling of overflow or underflow
		 * is performed.
		 *
		 * @apiNote The reasoning behind the throwing if out of range behavior is that, even
		 * though it can significantly slow the process of splitting, it is much better then
		 * risking a buggy implementation causing splits to stop happening much earlier then
		 * intended. Also, splitting is not usually in the "inner loop" of stream operations,
		 * so this slowness isn't in the bottleneck. That and we have already warned that
		 * high performance spliterators should prefer implementing all the methods themselves
		 * instead of through this interface.
		 *
		 * @implSpec This default implementation is a simple split-by-2 strategy, dividing
		 * in the middle of pos and {@link #getMaxPos()}. It is unspecified whether
		 * the first range or the second range will be larger in the case of an odd length range.
		 */
	 protected long computeSplitPoint() {
	  // Overflow safe midpoint computation.
	  return pos + ((getMaxPos() - pos) / 2);
	 }
	 private void splitPointCheck(final long splitPoint, final long observedMax) {
	  // TODO When minimum Java version becomes Java 9, use Objects.checkFromToIndex​ (after first letting == max case pass through)
	  if (splitPoint < pos || splitPoint > observedMax) {
	   throw new IndexOutOfBoundsException("splitPoint " + splitPoint + " outside of range of current position " + pos + " and range end " + observedMax);
	  }
	 }
	 // Since this is an index based spliterator, list characteristics make sense.
	 @Override
	 public int characteristics() { return CharSpliterators.LIST_SPLITERATOR_CHARACTERISTICS; }
	 @Override
	 public long estimateSize() { return getMaxPos() - pos; }
	 @Override
	 public boolean tryAdvance(final CharConsumer action) {
	  if (pos >= getMaxPos()) return false;
	  action.accept(get(pos++));
	  return true;
	 }
	 @Override
	 public void forEachRemaining(final CharConsumer action) {
	  for (final long max = getMaxPos(); pos < max; ++pos) {
	   action.accept(get(pos));
	  }
	 }
	 @Override
	 public long skip(long n) {
	  if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
	  final long max = getMaxPos();
	  if (pos >= max) return 0;
	  final long remaining = max - pos;
	  if (n < remaining) {
	   pos += n;
	   return n;
	  }
	  n = remaining;
	  pos = max;
	  return n;
	 }
	 /** {@inheritDoc}
		 *
		 * @implSpec This implementation always returns a prefix of the elements, in order to comply with
		 * the {@link java.util.Spliterator#ORDERED} property. This means this current iterator does not need to
		 * to update what {@link #getMaxPos()} returns in response to this method (but it may do
		 * "book-keeping" on it based on binding strategy).
		 *
		 * <p>The split point is computed by {@link #computeSplitPoint()}; see that method for details.
		 *
		 * @throws IndexOutOfBoundsException if the return of {@link #computeSplitPoint()} was
		 *  {@code < pos} or {@code > {@link #getMaxPos()}}.
		 */
	 @Override
	 public CharSpliterator trySplit() {
	  final long max = getMaxPos();
	  final long splitPoint = computeSplitPoint();
	  if (splitPoint == pos || splitPoint == max) return null;
	  splitPointCheck(splitPoint, max);
	  long oldPos = pos;
	  CharSpliterator maybeSplit = makeForSplit(oldPos, splitPoint);
	  if (maybeSplit != null) this.pos = splitPoint;
	  return maybeSplit;
	 }
	}
	/**
	 * A skeletal implementation for a spliterator backed by an index based data store. High performance
	 * concrete implementations (like the main Spliterator of ArrayList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>This class implements an early binding strategy for {@link #getMaxPos()}. The last index
	 * this spliterator covers is fixed at construction time and does not vary on changes to the
	 * backing data store. This should usually be the {@linkplain java.util.Collection#size() size} of the
	 * backing data store (until a split at least), hence the class' name, but this is not required.
	 *
	 * <p>As the abstract methods in this class are used in inner loops, it is generally a
	 * good idea to override the class as {@code final} as to encourage the JVM to inline
	 * them (or alternatively, override the abstract methods as final).
	 */
	 public static abstract class EarlyBindingSizeIndexBasedSpliterator extends AbstractIndexBasedSpliterator {
	  /** The maximum {@link #pos} can be */
	  protected final long maxPos;
	 protected EarlyBindingSizeIndexBasedSpliterator(long initialPos, long maxPos) {
	  super(initialPos);
	  this.maxPos = maxPos;
	 }
	 @Override
	 protected final long getMaxPos() { return maxPos; }
	}
	/**
	 * A skeletal implementation for a spliterator backed by an index based data store. High performance
	 * concrete implementations (like the main Spliterator of ArrayList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>This class implements a late binding strategy. On a new, non-split instance, the
	 * {@link #getMaxPos() max pos} will track the given data store (usually it's
	 * {@linkplain java.util.Collection#size() size}, hence the class' name). On the first
	 * {@linkplain #trySplit() split}, the last index will be read from the backing data store one
	 * last time and then be fixed for the remaining duration of this instance.<br>
	 * The returned split <em>should</em> should also be have a constant {@code maxPos}.
	 *
	 * <p>As the abstract methods in this class are used in inner loops, it is generally a
	 * good idea to override the class as {@code final} as to encourage the JVM to inline
	 * them (or alternatively, override the abstract methods as final).
	 */
	 public static abstract class LateBindingSizeIndexBasedSpliterator extends AbstractIndexBasedSpliterator {
	  /** The maximum {@link #pos} can be, or -1 if it hasn't been fixed yet. */
	  protected long maxPos = -1;
	  private boolean maxPosFixed;
	 protected LateBindingSizeIndexBasedSpliterator(long initialPos) {
	  super(initialPos);
	  this.maxPosFixed = false;
	 }
	 protected LateBindingSizeIndexBasedSpliterator(long initialPos, long fixedMaxPos) {
	  super(initialPos);
	  this.maxPos = fixedMaxPos;
	  this.maxPosFixed = true;
	 }
	 /** Return the maximum pos can be dynamically tracking the backing data store.
		 *
		 * <p>This method will be the return value of {@link #getMaxPos()} until this spliterator
		 * is {@linkplain #trySplit()} split, in which case its final return value will be saved
		 * and remain constant for the rest of the duration of this instance.
		 */
	 protected abstract long getMaxPosFromBackingStore();
	 @Override
	 protected final long getMaxPos() { return maxPosFixed ? maxPos : getMaxPosFromBackingStore(); }
	 @Override
	 public CharSpliterator trySplit() {
	  CharSpliterator maybeSplit = super.trySplit();
	  if (!maxPosFixed && maybeSplit != null) {
	   maxPos = getMaxPosFromBackingStore();
	   maxPosFixed = true;
	  }
	  return maybeSplit;
	 }
	}
}
