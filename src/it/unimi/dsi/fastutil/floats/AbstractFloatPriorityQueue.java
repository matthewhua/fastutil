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
/**  An abstract class providing basic methods for priority queues implementing a type-specific interface.
	* @deprecated As of {@code fastutil} 8 this class is no longer necessary, as its previous abstract
	* methods are now default methods of the type-specific interface.
	*/
@Deprecated
public abstract class AbstractFloatPriorityQueue extends it.unimi.dsi.fastutil.AbstractPriorityQueue<Float> implements java.io.Serializable, FloatPriorityQueue {
	private static final long serialVersionUID = 1L;
}
