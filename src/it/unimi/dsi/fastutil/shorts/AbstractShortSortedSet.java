/*
	* Copyright (C) 2003-2022 Sebastiano Vigna
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
/** An abstract class providing basic methods for sorted sets implementing a type-specific interface. */
public abstract class AbstractShortSortedSet extends AbstractShortSet implements ShortSortedSet {
	protected AbstractShortSortedSet() {}
	@Override
	public abstract ShortBidirectionalIterator iterator();
}
