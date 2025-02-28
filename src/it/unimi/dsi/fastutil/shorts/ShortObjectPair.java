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
package it.unimi.dsi.fastutil.shorts;
/**  A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that use polymorphism to avoid (un)boxing. */
public interface ShortObjectPair <V> extends it.unimi.dsi.fastutil.Pair<Short, V> {
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 */
	public short leftShort();
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Short left() {
	 return Short.valueOf(leftShort());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default ShortObjectPair <V> left(final short l) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ShortObjectPair <V> left(final Short l) {
	 return left((l).shortValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default short firstShort() {
	 return leftShort();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Short first() {
	 return Short.valueOf(firstShort());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default ShortObjectPair <V> first(final short l) {
	 return left(l);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default ShortObjectPair <V> first(final Short l) {
	 return first((l).shortValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default short keyShort() {
	 return firstShort();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Short key() {
	 return Short.valueOf(keyShort());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default ShortObjectPair <V> key(final short l) {
	 return left(l);
	}
	@Override
	@Deprecated
	public default ShortObjectPair <V> key(Short l) {
	 return key((l).shortValue());
	}
	/** Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static <V> ShortObjectPair <V> of(final short left, final V right) {
	 return new ShortObjectImmutablePair <V>(left, right);
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
	@SuppressWarnings("unchecked")
	public static <V> java.util.Comparator<ShortObjectPair <V> > lexComparator() {
	 return (x, y) -> {
	  final int t = Short.compare(x.leftShort(), y.leftShort());
	  if (t != 0) return t;
	  return ((Comparable <V>)x.right()).compareTo(y.right());
	 };
	}
}
