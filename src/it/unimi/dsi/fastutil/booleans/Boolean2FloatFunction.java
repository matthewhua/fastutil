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
package it.unimi.dsi.fastutil.booleans;
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
public interface Boolean2FloatFunction extends Function<Boolean, Float> {
	/** Adds a pair to the map (optional operation).
	 *
	 * @param key the key.
	 * @param value the value.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#put(Object,Object)
	 */
	default float put(final boolean key, final float value) {
	 throw new UnsupportedOperationException();
	}
	/** Returns the value to which the given key is mapped.
	 *
	 * @param key the key.
	 * @return the corresponding value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#get(Object)
	 */
	float get(boolean key);
	/** Returns the value associated by this function to the specified key, or give the specified
	 *  value if not present.
	 *
	 * @param key the key.
	 * @param defaultValue the value to return if not present.
	 * @return the corresponding value, or {@code defaultValue} if no value was present for the given key.
	 * @see Function#getOrDefault(Object, Object)
	 * @since 8.5.0
	 */
	default float getOrDefault(final boolean key, final float defaultValue) {
	 final float v;
	 return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}
	/** Removes the mapping with the given key (optional operation).
	 * @param key the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 * @see Function#remove(Object)
	 */
	default float remove(final boolean key) {
	 throw new UnsupportedOperationException();
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Float put(final Boolean key, final Float value) {
	 final boolean k = (key).booleanValue();
	 final boolean containsKey = containsKey(k);
	 final float v = put(k, (value).floatValue());
	 return containsKey ? Float.valueOf(v) : null;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Float get(final Object key) {
	 if (key == null) return null;
	 final boolean k = ((Boolean)(key)).booleanValue();
	 final float v;
	 return ((v = get(k)) != defaultReturnValue() || containsKey(k)) ? Float.valueOf(v) : null;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Float getOrDefault(final Object key, Float defaultValue) {
	 if (key == null) return defaultValue;
	 final boolean k = ((Boolean)(key)).booleanValue();
	 final float v = get(k);
	 return (v != defaultReturnValue() || containsKey(k)) ? Float.valueOf(v) : defaultValue;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default Float remove(final Object key) {
	 if (key == null) return null;
	 final boolean k = ((Boolean)(key)).booleanValue();
	 return containsKey(k) ? Float.valueOf(remove(k)) : null;
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
	default boolean containsKey(boolean key) {
	 return true;
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead. */
	@Deprecated
	@Override
	default boolean containsKey(final Object key) {
	 return key == null ? false : containsKey(((Boolean)(key)).booleanValue());
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
	default void defaultReturnValue(float rv) {
	 throw new UnsupportedOperationException();
	}
	/** Gets the default return value.
	 *
	 * <p>This default implementation just return the default null value
	 * of the type ({@code null} for objects, 0 for scalars, false for Booleans).
	 *
	 * @return the current default return value.
	 */
	default float defaultReturnValue() {
	 return (0);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<T, Float> compose(java.util.function.Function<? super T, ? extends Boolean> before) {
	 return Function.super.compose(before);
	}
	/** {@inheritDoc}
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<Boolean, T> andThen(java.util.function.Function<? super Float, ? extends T> after) {
	 return Function.super.andThen(after);
	}
default it.unimi.dsi.fastutil.booleans.Boolean2ByteFunction andThenByte(it.unimi.dsi.fastutil.floats.Float2ByteFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.bytes.Byte2FloatFunction composeByte(it.unimi.dsi.fastutil.bytes.Byte2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2ShortFunction andThenShort(it.unimi.dsi.fastutil.floats.Float2ShortFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.shorts.Short2FloatFunction composeShort(it.unimi.dsi.fastutil.shorts.Short2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2IntFunction andThenInt(it.unimi.dsi.fastutil.floats.Float2IntFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.ints.Int2FloatFunction composeInt(it.unimi.dsi.fastutil.ints.Int2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2LongFunction andThenLong(it.unimi.dsi.fastutil.floats.Float2LongFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.longs.Long2FloatFunction composeLong(it.unimi.dsi.fastutil.longs.Long2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2CharFunction andThenChar(it.unimi.dsi.fastutil.floats.Float2CharFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.chars.Char2FloatFunction composeChar(it.unimi.dsi.fastutil.chars.Char2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2FloatFunction andThenFloat(it.unimi.dsi.fastutil.floats.Float2FloatFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.floats.Float2FloatFunction composeFloat(it.unimi.dsi.fastutil.floats.Float2BooleanFunction before) { return k -> get(before.get(k)); }
default it.unimi.dsi.fastutil.booleans.Boolean2DoubleFunction andThenDouble(it.unimi.dsi.fastutil.floats.Float2DoubleFunction after) { return k -> after.get(get(k)); } default it.unimi.dsi.fastutil.doubles.Double2FloatFunction composeDouble(it.unimi.dsi.fastutil.doubles.Double2BooleanFunction before) { return k -> get(before.get(k)); }
default <T> it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction <T> andThenObject(it.unimi.dsi.fastutil.floats.Float2ObjectFunction <? extends T> after) { return k -> after.get(get(k)); } default <T> it.unimi.dsi.fastutil.objects.Object2FloatFunction <T> composeObject(it.unimi.dsi.fastutil.objects.Object2BooleanFunction <? super T> before) { return k -> get(before.getBoolean(k)); }
default <T> it.unimi.dsi.fastutil.booleans.Boolean2ReferenceFunction <T> andThenReference(it.unimi.dsi.fastutil.floats.Float2ReferenceFunction <? extends T> after) { return k -> after.get(get(k)); } default <T> it.unimi.dsi.fastutil.objects.Reference2FloatFunction <T> composeReference(it.unimi.dsi.fastutil.objects.Reference2BooleanFunction <? super T> before) { return k -> get(before.getBoolean(k)); }
}
