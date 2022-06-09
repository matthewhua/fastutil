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
package it.unimi.dsi.fastutil.ints;
/** A type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that use polymorphism to avoid (un)boxing. */
public class IntReferenceMutablePair <V> implements IntReferencePair <V>, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected int left;
	protected V right;
	/** Creates a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public IntReferenceMutablePair(final int left, final V right) {
	 this.left = left;
	 this.right = right;
	}
	/** Returns a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 *
	 * @implSpec This factory method delegates to the constructor.
	 */
	public static <V> IntReferenceMutablePair <V> of(final int left, final V right) {
	 return new IntReferenceMutablePair <V>(left, right);
	}
	@Override
	public int leftInt() {
	 return left;
	}
	@Override
	public IntReferenceMutablePair <V> left(final int l) {
	 left = l;
	 return this;
	}
	@Override
	public V right() {
	 return right;
	}
	@Override
	public IntReferenceMutablePair <V> right(final V r) {
	 right = r;
	 return this;
	}
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
	 if (other == null) return false;
	 if (other instanceof IntReferencePair) {
	  return
	left == ((IntReferencePair)other).leftInt()
	&& right == ((IntReferencePair)other).right();
	 }
	 if (other instanceof it.unimi.dsi.fastutil.Pair) {
	  return
	java.util.Objects.equals(Integer.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left())
	&& right == ((it.unimi.dsi.fastutil.Pair)other).right();
	 }
	 return false;
	}
	@Override
	   public int hashCode() {
	 return (left) * 19 + ( (right) == null ? 0 : System.identityHashCode(right) );
	}
	/** Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
	 return "<" + leftInt() + "," + right() + ">";
	}
}
