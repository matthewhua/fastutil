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
package it.unimi.dsi.fastutil.doubles;
import it.unimi.dsi.fastutil.Function;
/** A type-specific {@link Function}; provides some additional methods that use polymorphism to avoid (un)boxing.
	*
	* <p>Type-specific versions of {@code get()}, {@code put()} and
	* {@code remove()} cannot rely on {@code null} to denote absence of
	* a key. Rather, they return a {@linkplain #defaultReturnValue() default
	* return value}, which is set to 0/false at creation, but can be changed using
	* the {@code defaultReturnValue()} method.
	*
	* <p>For uniformity reasons, even functions returning objects implement the default
	* return value (of course, in this case the default return value is
	* initialized to {@code null}).
	*
	* <p>The default implementation of optional operations just throw an {@link
	* UnsupportedOperationException}, except for the type-specific {@code
	* containsKey()}, which return true. Generic versions of accessors delegate to
	* the corresponding type-specific counterparts following the interface rules.
	*
	* <p><strong>Warning:</strong> to fall in line as much as possible with the
	* {@linkplain java.util.Map standard map interface}, it is required that
	* standard versions of {@code get()}, {@code put()} and
	* {@code remove()} for maps with primitive-type keys or values <em>return
	* {@code null} to denote missing keys </em> rather than wrap the default
	* return value in an object. In case both keys and values are reference
	* types, the default return value must be returned instead, thus violating
	* the {@linkplain java.util.Map standard map interface} when the default
	* return value is not {@code null}.
	*
	* @see Function
	*/
@FunctionalInterface
public interface Double2BooleanFunction extends Function<Double, Boolean>, java.util.function.DoublePredicate {
	/** {@inheritDoc}
	 * @since 8.0.0
	 */
	@Override
	default boolean test(double operand) { return get(operand); }
	/** Adds a pair to the map (optional operation).
	 *
	 * @param key the key.
	 * @param value the value.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#put(Object,Object)
	 */
	default boolean put(final double key, final boolean value) {
	 throw new UnsupportedOperationException();
	}
	/** Returns the value to which the given key is mapped.
	 *
	 * @param key the key.
	 * @return the corresponding value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#get(Object)
	 */
	boolean get(double key);
	/** Returns the value associated by this function to the specified key, or give the specified
	 *  value if not present.
	 *
	 * @param key the key.
	 * @param defaultValue the value to return if not present.
	 * @return the corresponding value, or {@code defaultValue} if no value was present for the given key.
	 * @see Function#getOrDefault(Object, Object)
	 * @since 8.5.0
	 */
	default boolean getOrDefault(final double key, final boolean defaultValue) {
	 final boolean v;
	 return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}
	/** Removes the mapping with the given key (optional operation).
	 * @param key the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#remove(Object)
	 */
	default boolean remove(final double key) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Boolean put(final Double key, final Boolean value) {
	 final double k = (key).doubleValue();
	 final boolean containsKey = containsKey(k);
	 final boolean v = put(k, (value).booleanValue());
	 return containsKey ? Boolean.valueOf(v) : null;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Boolean get(final Object key) {
	 if (key == null) return null;
	 final double k = ((Double)(key)).doubleValue();
	 final boolean v;
	 return ((v = get(k)) != defaultReturnValue() || containsKey(k)) ? Boolean.valueOf(v) : null;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Boolean getOrDefault(final Object key, Boolean defaultValue) {
	 if (key == null) return defaultValue;
	 final double k = ((Double)(key)).doubleValue();
	 final boolean v = get(k);
	 return (v != defaultReturnValue() || containsKey(k)) ? Boolean.valueOf(v) : defaultValue;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Boolean remove(final Object key) {
	 if (key == null) return null;
	 final double k = ((Double)(key)).doubleValue();
	 return containsKey(k) ? Boolean.valueOf(remove(k)) : null;
	}
	/** Returns true if this function contains a mapping for the specified key.
	 *
	 * <p>Note that for some kind of functions (e.g., hashes) this method will
	 * always return true. In particular, this default implementation always
	 * returns true.
	 *
	 * @param key the key.
	 * @return true if this function associates a value to {@code key}.
	 * @see Function#containsKey(Object)
	 */
	default boolean containsKey(double key) {
	 return true;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default boolean containsKey(final Object key) {
	 return key == null ? false : containsKey(((Double)(key)).doubleValue());
	}
	/** Sets the default return value (optional operation).
	 *
	 * This value must be returned by type-specific versions of
	 * {@code get()}, {@code put()} and {@code remove()} to
	 * denote that the map does not contain the specified key. It must be
	 * 0/{@code false}/{@code null} by default.
	 *
	 * @param rv the new default return value.
	 * @see #defaultReturnValue()
	 */
	default void defaultReturnValue(boolean rv) {
	 throw new UnsupportedOperationException();
	}
	/** Gets the default return value.
	 *
	 * <p>This default implementation just return the default null value
	 * of the type ({@code null} for objects, 0 for scalars, false for Booleans).
	 *
	 * @return the current default return value.
	 */
	default boolean defaultReturnValue() {
	 return (false);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<T, Boolean> compose(java.util.function.Function<? super T, ? extends Double> before) {
	 return Function.super.compose(before);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<Double, T> andThen(java.util.function.Function<? super Boolean, ? extends T> after) {
	 return Function.super.andThen(after);
	}
default it.unimi.dsi.fastutil.doubles.Double2ByteFunction andThenByte(it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction composeByte(it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2ShortFunction andThenShort(it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.shorts.Short2BooleanFunction composeShort(it.unimi.dsi.fastutil.shorts.Short2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2IntFunction andThenInt(it.unimi.dsi.fastutil.booleans.Boolean2IntFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.ints.Int2BooleanFunction composeInt(it.unimi.dsi.fastutil.ints.Int2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2LongFunction andThenLong(it.unimi.dsi.fastutil.booleans.Boolean2LongFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.longs.Long2BooleanFunction composeLong(it.unimi.dsi.fastutil.longs.Long2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2CharFunction andThenChar(it.unimi.dsi.fastutil.booleans.Boolean2CharFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.chars.Char2BooleanFunction composeChar(it.unimi.dsi.fastutil.chars.Char2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2FloatFunction andThenFloat(it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.floats.Float2BooleanFunction composeFloat(it.unimi.dsi.fastutil.floats.Float2DoubleFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.doubles.Double2DoubleFunction andThenDouble(it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.doubles.Double2BooleanFunction composeDouble(it.unimi.dsi.fastutil.doubles.Double2DoubleFunction before) { return k -> get(before.get(k)); }
default <T> it.unimi.dsi.fastutil.doubles.Double2ObjectFunction <T> andThenObject(it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction <? extends T> after) { return k -> after.get(get(k)); } default <T> it.unimi.dsi.fastutil.objects.Object2BooleanFunction <T> composeObject(it.unimi.dsi.fastutil.objects.Object2DoubleFunction <? super T> before) { return k -> get(before.getDouble(k)); }
default <T> it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction <T> andThenReference(it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction <? extends T> after) { return k -> after.get(get(k)); } default <T> it.unimi.dsi.fastutil.objects.Reference2BooleanFunction <T> composeReference(it.unimi.dsi.fastutil.objects.Reference2DoubleFunction <? super T> before) { return k -> get(before.getDouble(k)); }
}
