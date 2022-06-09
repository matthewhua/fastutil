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
package it.unimi.dsi.fastutil.chars;
/**  A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that use polymorphism to avoid (un)boxing. */
public interface CharObjectPair <V> extends it.unimi.dsi.fastutil.Pair<Character, V> {
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 */
	public char leftChar();
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Character left() {
	 return Character.valueOf(leftChar());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default CharObjectPair <V> left(final char l) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default CharObjectPair <V> left(final Character l) {
	 return left((l).charValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default char firstChar() {
	 return leftChar();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Character first() {
	 return Character.valueOf(firstChar());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default CharObjectPair <V> first(final char l) {
	 return left(l);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default CharObjectPair <V> first(final Character l) {
	 return first((l).charValue());
	}
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default char keyChar() {
	 return firstChar();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public default Character key() {
	 return Character.valueOf(keyChar());
	}
	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default CharObjectPair <V> key(final char l) {
	 return left(l);
	}
	@Override
	@Deprecated
	public default CharObjectPair <V> key(Character l) {
	 return key((l).charValue());
	}
	/** Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static <V> CharObjectPair <V> of(final char left, final V right) {
	 return new CharObjectImmutablePair <V>(left, right);
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
	public static <V> java.util.Comparator<CharObjectPair <V> > lexComparator() {
	 return (x, y) -> {
	  final int t = Character.compare(x.leftChar(), y.leftChar());
	  if (t != 0) return t;
	  return ((Comparable <V>)x.right()).compareTo(y.right());
	 };
	}
}
