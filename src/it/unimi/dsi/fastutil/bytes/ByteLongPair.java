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
package it.unimi.dsi.fastutil.bytes;
/**  A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that use polymorphism to avoid (un)boxing. */
public interface ByteLongPair extends it.unimi.dsi.fastutil.Pair<Byte, Long> {
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 */
	public byte leftByte();
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Byte left() {
	 return Byte.valueOf(leftByte());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default ByteLongPair left(final byte l) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ByteLongPair left(final Byte l) {
	 return left((l).byteValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default byte firstByte() {
	 return leftByte();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Byte first() {
	 return Byte.valueOf(firstByte());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default ByteLongPair first(final byte l) {
	 return left(l);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ByteLongPair first(final Byte l) {
	 return first((l).byteValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default byte keyByte() {
	 return firstByte();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Byte key() {
	 return Byte.valueOf(keyByte());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default ByteLongPair key(final byte l) {
	 return left(l);
	}
	@Override
	@Deprecated
	public default ByteLongPair key(Byte l) {
	 return key((l).byteValue());
	}
	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 */
	public long rightLong();
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Long right() {
	 return Long.valueOf(rightLong());
	}
	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default ByteLongPair right(final long r) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ByteLongPair right(final Long l) {
	 return right((l).longValue());
	}
	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default long secondLong() {
	 return rightLong();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Long second() {
	 return Long.valueOf(secondLong());
	}
	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ByteLongPair second(final long r) {
	 return right(r);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ByteLongPair second(final Long l) {
	 return second((l).longValue());
	}
	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default long valueLong() {
	 return rightLong();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Long value() {
	 return Long.valueOf(valueLong());
	}
	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ByteLongPair value(final long r) {
	 return right(r);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ByteLongPair value(final Long l) {
	 return value((l).longValue());
	}
	/** Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static ByteLongPair of(final byte left, final long right) {
	 return new ByteLongImmutablePair (left, right);
	}
	/**
	 * Returns a lexicographical comparator for pairs.
	 *
	 * <p>
	 * The comparator returned by this method implements lexicographical order. It compares first
	 * the left elements: if the result of the comparison is nonzero, it returns said result.
	 * Otherwise, this comparator returns the result of the comparison of the right elements.
	 *
	 * @return a lexicographical comparator for pairs.
	 */

	public static java.util.Comparator<ByteLongPair > lexComparator() {
	 return (x, y) -> {
	  final int t = Byte.compare(x.leftByte(), y.leftByte());
	  if (t != 0) return t;
	  return Long.compare(x.rightLong(), y.rightLong());
	 };
	}
}
