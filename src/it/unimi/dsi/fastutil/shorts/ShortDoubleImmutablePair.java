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
/**  A type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that use polymorphism to avoid (un)boxing. */
public class ShortDoubleImmutablePair implements ShortDoublePair , java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected final short left;
	protected final double right;
	/** Creates a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public ShortDoubleImmutablePair(final short left, final double right) {
	 this.left = left;
	 this.right = right;
	}
	/** Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and right value. 
	 * @param left the left value.
	 * @param right the right value.
	 *
	 * @implSpec This factory method delegates to the constructor.
	 */
	public static ShortDoubleImmutablePair of(final short left, final double right) {
	 return new ShortDoubleImmutablePair (left, right);
	}
	@Override
	public short leftShort() {
	 return left;
	}
	@Override
	public double rightDouble() {
	 return right;
	}
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
	 if (other == null) return false;
	 if (other instanceof ShortDoublePair) {
	  return
	left == ((ShortDoublePair)other).leftShort()
	&& right == ((ShortDoublePair)other).rightDouble();
	 }
	 if (other instanceof it.unimi.dsi.fastutil.Pair) {
	  return
	java.util.Objects.equals(Short.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left())
	&& java.util.Objects.equals(Double.valueOf(right), ((it.unimi.dsi.fastutil.Pair)other).right());
	 }
	 return false;
	}
	@Override
	   public int hashCode() {
	 return (left) * 19 + it.unimi.dsi.fastutil.HashCommon.double2int(right);
	}
	/** Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
	 return "<" + leftShort() + "," + rightDouble() + ">";
	}
}
