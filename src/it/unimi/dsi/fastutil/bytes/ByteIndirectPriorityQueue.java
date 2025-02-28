/*
	* Copyright (C) 2003-2022 Paolo Boldi and Sebastiano Vigna
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
import it.unimi.dsi.fastutil.IndirectPriorityQueue;
/** A type-specific {@link IndirectPriorityQueue}.
	*
	* <p>Additionally, this interface strengthens {@link #comparator()}.
	*/
public interface ByteIndirectPriorityQueue extends IndirectPriorityQueue<Byte> {
	/** Returns the type-specific comparator associated with this queue.
	 *
	 * @apiNote Note that this specification strengthens the one given in {@link IndirectPriorityQueue}.
	 *
	 * @return the comparator associated with this queue.
	 * @see IndirectPriorityQueue#comparator()
	 */
	@Override
	ByteComparator comparator();
}
