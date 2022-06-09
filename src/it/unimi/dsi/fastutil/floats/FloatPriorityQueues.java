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
package it.unimi.dsi.fastutil.floats;
/** A class providing static methods and objects that do useful things with type-specific priority queues.
	*
	* @see it.unimi.dsi.fastutil.PriorityQueue
	*/
public final class FloatPriorityQueues {
	private FloatPriorityQueues() {}
	/** A synchronized wrapper class for priority queues. */
	public static class SynchronizedPriorityQueue implements FloatPriorityQueue {
	 protected final FloatPriorityQueue q;
	 protected final Object sync;
	 protected SynchronizedPriorityQueue(final FloatPriorityQueue q, final Object sync) {
	  this.q = q;
	  this.sync = sync;
	 }
	 protected SynchronizedPriorityQueue(final FloatPriorityQueue q) {
	  this.q = q;
	  this.sync = this;
	 }
	 @Override
	 public void enqueue(float x) { synchronized(sync) { q.enqueue(x); } }
	 @Override
	 public float dequeueFloat() { synchronized(sync) { return q.dequeueFloat(); } }
	 @Override
	 public float firstFloat() { synchronized(sync) { return q.firstFloat(); } }
	 @Override
	 public float lastFloat() { synchronized(sync) { return q.lastFloat(); } }
	 @Override
	 public boolean isEmpty() { synchronized(sync) { return q.isEmpty(); } }
	 @Override
	 public int size() { synchronized(sync) { return q.size(); } }
	 @Override
	 public void clear() { synchronized(sync) { q.clear(); } }
	 @Override
	 public void changed() { synchronized(sync) { q.changed(); } }
	 @Override
	 public FloatComparator comparator() { synchronized(sync) { return q.comparator(); } }
	 @Deprecated
	 @Override
	 public void enqueue(Float x) { synchronized(sync) { q.enqueue(x); } }
	 @Deprecated
	 @Override
	 public Float dequeue() { synchronized(sync) { return q.dequeue(); } }
	 @Deprecated
	 @Override
	 public Float first() { synchronized(sync) { return q.first(); } }
	 @Deprecated
	 @Override
	 public Float last() { synchronized(sync) { return q.last(); } }
	 @Override
	 public int hashCode() { synchronized(sync) { return q.hashCode(); } }
	 @Override
	 public boolean equals(final Object o) { if (o == this) return true; synchronized(sync) { return q.equals(o); } }
	 private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	  synchronized(sync) { s.defaultWriteObject(); }
	 }
	}
	/** Returns a synchronized type-specific priority queue backed by the specified type-specific priority queue.
	 *
	 * @param q the priority queue to be wrapped in a synchronized priority queue.
	 * @return a synchronized view of the specified priority queue.
	 */
	public static FloatPriorityQueue synchronize(final FloatPriorityQueue q) { return new SynchronizedPriorityQueue(q); }
	/** Returns a synchronized type-specific priority queue backed by the specified type-specific priority queue, using an assigned object to synchronize.
	 *
	 * @param q the priority queue to be wrapped in a synchronized priority queue.
	 * @param sync an object that will be used to synchronize the access to the priority queue.
	 * @return a synchronized view of the specified priority queue.
	 */
	public static FloatPriorityQueue synchronize(final FloatPriorityQueue q, final Object sync) { return new SynchronizedPriorityQueue(q, sync); }
}
