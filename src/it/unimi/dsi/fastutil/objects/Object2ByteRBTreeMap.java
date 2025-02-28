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
package it.unimi.dsi.fastutil.objects;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.NoSuchElementException;
/** A type-specific red-black tree map with a fast, small-footprint implementation.
	*
	* <p>The iterators provided by the views of this class are type-specific {@linkplain
	* it.unimi.dsi.fastutil.BidirectionalIterator bidirectional iterators}.
	* Moreover, the iterator returned by {@code iterator()} can be safely cast
	* to a type-specific {@linkplain java.util.ListIterator list iterator}.
	*
	*/
public class Object2ByteRBTreeMap <K> extends AbstractObject2ByteSortedMap <K> implements java.io.Serializable, Cloneable {
	/** A reference to the root entry. */
	protected transient Entry <K> tree;
	/** Number of entries in this map. */
	protected int count;
	/** The first key in this map. */
	protected transient Entry <K> firstEntry;
	/** The last key in this map. */
	protected transient Entry <K> lastEntry;
	/** Cached set of entries. */
	protected transient ObjectSortedSet<Object2ByteMap.Entry <K> > entries;
	/** Cached set of keys. */
	protected transient ObjectSortedSet <K> keys;
	/** Cached collection of values. */
	protected transient ByteCollection values;
	/** The value of this variable remembers, after a {@code put()}
	 * or a {@code remove()}, whether the <em>domain</em> of the map
	 * has been modified. */
	protected transient boolean modified;
	/** This map's comparator, as provided in the constructor. */
	protected Comparator<? super K> storedComparator;
	/** This map's actual comparator; it may differ from {@link #storedComparator} because it is
		always a type-specific comparator, so it could be derived from the former by wrapping. */
	protected transient Comparator <? super K> actualComparator;
	private static final long serialVersionUID = -7046029254386353129L;
	{
	 allocatePaths();
	}
	/** Creates a new empty tree map.
	 */
	public Object2ByteRBTreeMap() {
	 tree = null;
	 count = 0;
	}
	/** Generates the comparator that will be actually used.
	 *
	 * <p>When a given {@link Comparator} is specified and stored in {@link
	 * #storedComparator}, we must check whether it is type-specific.  If it is
	 * so, we can used directly, and we store it in {@link #actualComparator}. Otherwise,
	 * we adapt it using a helper static method.
	 */
	private void setActualComparator() {
	 actualComparator = storedComparator;
	}
	/** Creates a new empty tree map with the given comparator.
	 *
	 * @param c a (possibly type-specific) comparator.
	 */
	public Object2ByteRBTreeMap(final Comparator<? super K> c) {
	 this();
	 storedComparator = c;
	 setActualComparator();
	}
	/** Creates a new tree map copying a given map.
	 *
	 * @param m a {@link Map} to be copied into the new tree map.
	 */
	public Object2ByteRBTreeMap(final Map<? extends K, ? extends Byte> m) {
	 this();
	 putAll(m);
	}
	/** Creates a new tree map copying a given sorted map (and its {@link Comparator}).
	 *
	 * @param m a {@link SortedMap} to be copied into the new tree map.
	 */
	public Object2ByteRBTreeMap(final SortedMap<K,Byte> m) {
	 this(m.comparator());
	 putAll(m);
	}
	/** Creates a new tree map copying a given map.
	 *
	 * @param m a type-specific map to be copied into the new tree map.
	 */
	public Object2ByteRBTreeMap(final Object2ByteMap <? extends K> m) {
	 this();
	 putAll(m);
	}
	/** Creates a new tree map copying a given sorted map (and its {@link Comparator}).
	 *
	 * @param m a type-specific sorted map to be copied into the new tree map.
	 */
	public Object2ByteRBTreeMap(final Object2ByteSortedMap <K> m) {
	 this(m.comparator());
	 putAll(m);
	}
	/** Creates a new tree map using the elements of two parallel arrays and the given comparator.
	 *
	 * @param k the array of keys of the new tree map.
	 * @param v the array of corresponding values in the new tree map.
	 * @param c a (possibly type-specific) comparator.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Object2ByteRBTreeMap(final K[] k, final byte v[], final Comparator<? super K> c) {
	 this(c);
	 if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
	 for(int i = 0; i < k.length; i++) this.put(k[i], v[i]);
	}
	/** Creates a new tree map using the elements of two parallel arrays.
	 *
	 * @param k the array of keys of the new tree map.
	 * @param v the array of corresponding values in the new tree map.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Object2ByteRBTreeMap(final K[] k, final byte v[]) {
	 this(k, v, null);
	}
	/*
	 * The following methods implements some basic building blocks used by
	 * all accessors.  They are (and should be maintained) identical to those used in RBTreeSet.drv.
	 *
	 * The put()/remove() code is derived from Ben Pfaff's GNU libavl
	 * (https://adtinfo.org/). If you want to understand what's
	 * going on, you should have a look at the literate code contained therein
	 * first.
	 */
	/** Compares two keys in the right way.
	 *
	 * <p>This method uses the {@link #actualComparator} if it is non-{@code null}.
	 * Otherwise, it resorts to primitive type comparisons or to {@link Comparable#compareTo(Object) compareTo()}.
	 *
	 * @param k1 the first key.
	 * @param k2 the second key.
	 * @return a number smaller than, equal to or greater than 0, as usual
	 * (i.e., when k1 &lt; k2, k1 = k2 or k1 &gt; k2, respectively).
	 */
	@SuppressWarnings("unchecked")
	final int compare(final K k1, final K k2) {
	 return actualComparator == null ? ( ((Comparable<K>)(k1)).compareTo(k2) ) : actualComparator.compare(k1, k2);
	}
	/** Returns the entry corresponding to the given key, if it is in the tree; {@code null}, otherwise.
	 *
	 * @param k the key to search for.
	 * @return the corresponding entry, or {@code null} if no entry with the given key exists.
	 */
	final Entry <K> findKey(final K k) {
	 Entry <K> e = tree;
	 int cmp;
	 while (e != null && (cmp = compare(k, e.key)) != 0) e = cmp < 0 ? e.left() : e.right();
	 return e;
	}
	/** Locates a key.
	 *
	 * @param k a key.
	 * @return the last entry on a search for the given key; this will be
	 * the given key, if it present; otherwise, it will be either the smallest greater key or the greatest smaller key.
	 */
	final Entry <K> locateKey(final K k) {
	 Entry <K> e = tree, last = tree;
	 int cmp = 0;
	 while (e != null && (cmp = compare(k, e.key)) != 0) {
	  last = e;
	  e = cmp < 0 ? e.left() : e.right();
	 }
	 return cmp == 0 ? e : last;
	}
	/** This vector remembers the path and the direction followed during the
	 *  current insertion. It suffices for about 2<sup>32</sup> entries. */
	private transient boolean dirPath[];
	private transient Entry <K> nodePath[];
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void allocatePaths() {
	 dirPath = new boolean[64];
	 nodePath = new Entry[64];
	}
	/** Adds an increment to value currently associated with a key.
	 *
	 * <p>Note that this method respects the {@linkplain #defaultReturnValue() default return value} semantics: when
	 * called with a key that does not currently appears in the map, the key
	 * will be associated with the default return value plus
	 * the given increment.
	 *
	 * @param k the key.
	 * @param incr the increment.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value was present for the given key.
	 */
	public byte addTo(final K k, final byte incr) {
	 Entry <K> e = add(k);
	 final byte oldValue = e.value;
	 e.value += incr;
	 return oldValue;
	}
	@Override
	public byte put(final K k, final byte v) {
	 Entry <K> e = add(k);
	 final byte oldValue = e.value;
	 e.value = v;
	 return oldValue;
	}
	/** Returns a node with key k in the balanced tree, creating one with defRetValue if necessary.
	 *
	 * @param k the key
	 * @return a node with key k. If a node with key k already exists, then that node is returned,
	 * otherwise a new node with defRetValue is created ensuring that the tree is balanced
	 * after creation of the node.
	 */
	private Entry <K> add(final K k) {
	 /* After execution of this method, modified is true iff a new entry has been inserted. */
	 java.util.Objects.requireNonNull(k);
	 modified = false;
	 int maxDepth = 0;
	 Entry <K> e;
	 if (tree == null) { // The case of the empty tree is treated separately.
	  count++;
	  e = tree = lastEntry = firstEntry = new Entry <>(k, defRetValue);
	 }
	 else {
	  Entry <K> p = tree;
	  int cmp, i = 0;
	  while(true) {
	   if ((cmp = compare(k, p.key)) == 0) {
	    // We clean up the node path, or we could have stale references later.
	    while(i-- != 0) nodePath[i] = null;
	    return p;
	   }
	   nodePath[i] = p;
	   if (dirPath[i++] = cmp > 0) {
	    if (p.succ()) {
	     count++;
	     e = new Entry <>(k, defRetValue);
	     if (p.right == null) lastEntry = e;
	     e.left = p;
	     e.right = p.right;
	     p.right(e);
	     break;
	    }
	    p = p.right;
	   }
	   else {
	    if (p.pred()) {
	     count++;
	     e = new Entry <>(k, defRetValue);
	     if (p.left == null) firstEntry = e;
	     e.right = p;
	     e.left = p.left;
	     p.left(e);
	     break;
	    }
	    p = p.left;
	   }
	  }
	  modified = true;
	  maxDepth = i--;
	  while(i > 0 && ! nodePath[i].black()) {
	   if (! dirPath[i - 1]) {
	    Entry <K> y = nodePath[i - 1].right;
	    if (! nodePath[i - 1].succ() && ! y.black()) {
	     nodePath[i].black(true);
	     y.black(true);
	     nodePath[i - 1].black(false);
	     i -= 2;
	    }
	    else {
	     Entry <K> x;
	     if (! dirPath[i]) y = nodePath[i];
	     else {
	      x = nodePath[i];
	      y = x.right;
	      x.right = y.left;
	      y.left = x;
	      nodePath[i - 1].left = y;
	      if (y.pred()) {
	       y.pred(false);
	       x.succ(y);
	      }
	     }
	     x = nodePath[i - 1];
	     x.black(false);
	     y.black(true);
	     x.left = y.right;
	     y.right = x;
	     if (i < 2) tree = y;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = y;
	      else nodePath[i - 2].left = y;
	     }
	     if (y.succ()) {
	      y.succ(false);
	      x.pred(y);
	     }
	     break;
	    }
	   }
	   else {
	    Entry <K> y = nodePath[i - 1].left;
	    if (! nodePath[i - 1].pred() && ! y.black()) {
	     nodePath[i].black(true);
	     y.black(true);
	     nodePath[i - 1].black(false);
	     i -= 2;
	    }
	    else {
	     Entry <K> x;
	     if (dirPath[i]) y = nodePath[i];
	     else {
	      x = nodePath[i];
	      y = x.left;
	      x.left = y.right;
	      y.right = x;
	      nodePath[i - 1].right = y;
	      if (y.succ()) {
	       y.succ(false);
	       x.pred(y);
	      }
	     }
	     x = nodePath[i - 1];
	     x.black(false);
	     y.black(true);
	     x.right = y.left;
	     y.left = x;
	     if (i < 2) tree = y;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = y;
	      else nodePath[i - 2].left = y;
	     }
	     if (y.pred()){
	      y.pred(false);
	      x.succ(y);
	     }
	     break;
	    }
	   }
	  }
	 }
	 tree.black(true);
	 // We clean up the node path, or we could have stale references later.
	 while(maxDepth-- != 0) nodePath[maxDepth] = null;
	 return e;
	}
	/* After execution of this method, {@link #modified} is true iff an entry
	has been deleted. */
	@SuppressWarnings("unchecked")
	@Override
	public byte removeByte(final Object k) {
	 modified = false;
	 if (tree == null) return defRetValue;
	 Entry <K> p = tree;
	 int cmp;
	 int i = 0;
	 final K kk = (K) k;
	 while(true) {
	  if ((cmp = compare(kk, p.key)) == 0) break;
	  dirPath[i] = cmp > 0;
	  nodePath[i] = p;
	  if (dirPath[i++]) {
	   if ((p = p.right()) == null) {
	    // We clean up the node path, or we could have stale references later.
	    while(i-- != 0) nodePath[i] = null;
	    return defRetValue;
	   }
	  }
	  else {
	   if ((p = p.left()) == null) {
	    // We clean up the node path, or we could have stale references later.
	    while(i-- != 0) nodePath[i] = null;
	    return defRetValue;
	   }
	  }
	 }
	 if (p.left == null) firstEntry = p.next();
	 if (p.right == null) lastEntry = p.prev();
	 if (p.succ()) {
	  if (p.pred()) {
	   if (i == 0) tree = p.left;
	   else {
	    if (dirPath[i - 1]) nodePath[i - 1].succ(p.right);
	    else nodePath[i - 1].pred(p.left);
	   }
	  }
	  else {
	   p.prev().right = p.right;
	   if (i == 0) tree = p.left;
	   else {
	    if (dirPath[i - 1]) nodePath[i - 1].right = p.left;
	    else nodePath[i - 1].left = p.left;
	   }
	  }
	 }
	 else {
	  boolean color;
	  Entry <K> r = p.right;
	  if (r.pred()) {
	   r.left = p.left;
	   r.pred(p.pred());
	   if (! r.pred()) r.prev().right = r;
	   if (i == 0) tree = r;
	   else {
	    if (dirPath[i - 1]) nodePath[i - 1].right = r;
	    else nodePath[i - 1].left = r;
	   }
	   color = r.black();
	   r.black(p.black());
	   p.black(color);
	   dirPath[i] = true;
	   nodePath[i++] = r;
	  }
	  else {
	   Entry <K> s;
	   int j = i++;
	   while(true) {
	    dirPath[i] = false;
	    nodePath[i++] = r;
	    s = r.left;
	    if (s.pred()) break;
	    r = s;
	   }
	   dirPath[j] = true;
	   nodePath[j] = s;
	   if (s.succ()) r.pred(s);
	   else r.left = s.right;
	   s.left = p.left;
	   if (! p.pred()) {
	    p.prev().right = s;
	    s.pred(false);
	   }
	   s.right(p.right);
	   color = s.black();
	   s.black(p.black());
	   p.black(color);
	   if (j == 0) tree = s;
	   else {
	    if (dirPath[j - 1]) nodePath[j - 1].right = s;
	    else nodePath[j - 1].left = s;
	   }
	  }
	 }
	 int maxDepth = i;
	 if (p.black()) {
	  for(; i > 0; i--) {
	   if (dirPath[i - 1] && ! nodePath[i - 1].succ() ||
	     ! dirPath[i - 1] && ! nodePath[i - 1].pred()) {
	    Entry <K> x = dirPath[i - 1] ? nodePath[i - 1].right : nodePath[i - 1].left;
	    if (! x.black()) {
	     x.black(true);
	     break;
	    }
	   }
	   if (! dirPath[i - 1]) {
	    Entry <K> w = nodePath[i - 1].right;
	    if (! w.black()) {
	     w.black(true);
	     nodePath[i - 1].black(false);
	     nodePath[i - 1].right = w.left;
	     w.left = nodePath[i - 1];
	     if (i < 2) tree = w;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = w;
	      else nodePath[i - 2].left = w;
	     }
	     nodePath[i] = nodePath[i - 1];
	     dirPath[i] = false;
	     nodePath[i - 1] = w;
	     if (maxDepth == i++) maxDepth++;
	     w = nodePath[i - 1].right;
	    }
	    if ((w.pred() || w.left.black()) &&
	      (w.succ() || w.right.black())) {
	     w.black(false);
	    }
	    else {
	     if (w.succ() || w.right.black()) {
	      Entry <K> y = w.left;
	      y.black (true);
	      w.black(false);
	      w.left = y.right;
	      y.right = w;
	      w = nodePath[i - 1].right = y;
	      if (w.succ()) {
	       w.succ(false);
	       w.right.pred(w);
	      }
	     }
	     w.black(nodePath[i - 1].black());
	     nodePath[i - 1].black(true);
	     w.right.black(true);
	     nodePath[i - 1].right = w.left;
	     w.left = nodePath[i - 1];
	     if (i < 2) tree = w;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = w;
	      else nodePath[i - 2].left = w;
	     }
	     if (w.pred()) {
	      w.pred(false);
	      nodePath[i - 1].succ(w);
	     }
	     break;
	    }
	   }
	   else {
	    Entry <K> w = nodePath[i - 1].left;
	    if (! w.black()) {
	     w.black (true);
	     nodePath[i - 1].black(false);
	     nodePath[i - 1].left = w.right;
	     w.right = nodePath[i - 1];
	     if (i < 2) tree = w;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = w;
	      else nodePath[i - 2].left = w;
	     }
	     nodePath[i] = nodePath[i - 1];
	     dirPath[i] = true;
	     nodePath[i - 1] = w;
	     if (maxDepth == i++) maxDepth++;
	     w = nodePath[i - 1].left;
	    }
	    if ((w.pred() || w.left.black()) &&
	      (w.succ() || w.right.black())) {
	     w.black(false);
	    }
	    else {
	     if (w.pred() || w.left.black()) {
	      Entry <K> y = w.right;
	      y.black(true);
	      w.black (false);
	      w.right = y.left;
	      y.left = w;
	      w = nodePath[i - 1].left = y;
	      if (w.pred()) {
	       w.pred(false);
	       w.left.succ(w);
	      }
	     }
	     w.black(nodePath[i - 1].black());
	     nodePath[i - 1].black(true);
	     w.left.black(true);
	     nodePath[i - 1].left = w.right;
	     w.right = nodePath[i - 1];
	     if (i < 2) tree = w;
	     else {
	      if (dirPath[i - 2]) nodePath[i - 2].right = w;
	      else nodePath[i - 2].left = w;
	     }
	     if (w.succ()) {
	      w.succ(false);
	      nodePath[i - 1].pred(w);
	     }
	     break;
	    }
	   }
	  }
	  if (tree != null) tree.black(true);
	 }
	 modified = true;
	 count--;
	 // We clean up the node path, or we could have stale references later.
	 while(maxDepth-- != 0) nodePath[maxDepth] = null;
	 return p.value;
	}
	@Override
	public boolean containsValue(final byte v) {
	 final ValueIterator i = new ValueIterator();
	 byte ev;
	 int j = count;
	 while(j-- != 0) {
	  ev = i.nextByte();
	  if (( (ev) == (v) )) return true;
	 }
	 return false;
	}
	@Override
	public void clear() {
	 count = 0;
	 tree = null;
	 entries = null;
	 values = null;
	 keys = null;
	 firstEntry = lastEntry = null;
	}
	/** This class represent an entry in a tree map.
	 *
	 * <p>We use the only "metadata", i.e., {@link Entry#info}, to store
	 * information about color, predecessor status and successor status.
	 *
	 * <p>Note that since the class is recursive, it can be
	 * considered equivalently a tree.
	 */
	private static final class Entry <K> extends AbstractObject2ByteMap.BasicEntry <K> implements Cloneable {
	 /** The the bit in this mask is true, the node is black. */
	 private static final int BLACK_MASK = 1;
	 /** If the bit in this mask is true, {@link #right} points to a successor. */
	 private static final int SUCC_MASK = 1 << 31;
	 /** If the bit in this mask is true, {@link #left} points to a predecessor. */
	 private static final int PRED_MASK = 1 << 30;
	 /** The pointers to the left and right subtrees. */
	 Entry <K> left, right;
	 /** This integers holds different information in different bits (see {@link #SUCC_MASK} and {@link #PRED_MASK}. */
	 int info;
	 Entry() {
	  super((null), ((byte)0));
	 }
	 /** Creates a new entry with the given key and value.
		 *
		 * @param k a key.
		 * @param v a value.
		 */
	 Entry(final K k, final byte v) {
	  super(k, v);
	  info = SUCC_MASK | PRED_MASK;
	 }
	 /** Returns the left subtree.
		 *
		 * @return the left subtree ({@code null} if the left
		 * subtree is empty).
		 */
	 Entry <K> left() {
	  return (info & PRED_MASK) != 0 ? null : left;
	 }
	 /** Returns the right subtree.
		 *
		 * @return the right subtree ({@code null} if the right
		 * subtree is empty).
		 */
	 Entry <K> right() {
	  return (info & SUCC_MASK) != 0 ? null : right;
	 }
	 /** Checks whether the left pointer is really a predecessor.
		 * @return true if the left pointer is a predecessor.
		 */
	 boolean pred() {
	  return (info & PRED_MASK) != 0;
	 }
	 /** Checks whether the right pointer is really a successor.
		 * @return true if the right pointer is a successor.
		 */
	 boolean succ() {
	  return (info & SUCC_MASK) != 0;
	 }
	 /** Sets whether the left pointer is really a predecessor.
		 * @param pred if true then the left pointer will be considered a predecessor.
		 */
	 void pred(final boolean pred) {
	  if (pred) info |= PRED_MASK;
	  else info &= ~PRED_MASK;
	 }
	 /** Sets whether the right pointer is really a successor.
		 * @param succ if true then the right pointer will be considered a successor.
		 */
	 void succ(final boolean succ) {
	  if (succ) info |= SUCC_MASK;
	  else info &= ~SUCC_MASK;
	 }
	 /** Sets the left pointer to a predecessor.
		 * @param pred the predecessr.
		 */
	 void pred(final Entry <K> pred) {
	  info |= PRED_MASK;
	  left = pred;
	 }
	 /** Sets the right pointer to a successor.
		 * @param succ the successor.
		 */
	 void succ(final Entry <K> succ) {
	  info |= SUCC_MASK;
	  right = succ;
	 }
	 /** Sets the left pointer to the given subtree.
		 * @param left the new left subtree.
		 */
	 void left(final Entry <K> left) {
	  info &= ~PRED_MASK;
	  this.left = left;
	 }
	 /** Sets the right pointer to the given subtree.
		 * @param right the new right subtree.
		 */
	 void right(final Entry <K> right) {
	  info &= ~SUCC_MASK;
	  this.right = right;
	 }
	 /** Returns whether this node is black.
		 * @return true iff this node is black.
		 */
	 boolean black() {
	  return (info & BLACK_MASK) != 0;
	 }
	 /** Sets whether this node is black.
		 * @param black if true, then this node becomes black; otherwise, it becomes red..
		 */
	 void black(final boolean black) {
	  if (black) info |= BLACK_MASK;
	  else info &= ~BLACK_MASK;
	 }
	 /** Computes the next entry in the set order.
		 *
		 * @return the next entry ({@code null}) if this is the last entry).
		 */
	 Entry <K> next() {
	  Entry <K> next = this.right;
	  if ((info & SUCC_MASK) == 0) while ((next.info & PRED_MASK) == 0) next = next.left;
	  return next;
	 }
	 /** Computes the previous entry in the set order.
		 *
		 * @return the previous entry ({@code null}) if this is the first entry).
		 */
	 Entry <K> prev() {
	  Entry <K> prev = this.left;
	  if ((info & PRED_MASK) == 0) while ((prev.info & SUCC_MASK) == 0) prev = prev.right;
	  return prev;
	 }
	 @Override
	 public byte setValue(final byte value) {
	  final byte oldValue = this.value;
	  this.value = value;
	  return oldValue;
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public Entry <K> clone() {
	  Entry <K> c;
	  try {
	   c = (Entry <K>)super.clone();
	  }
	  catch(CloneNotSupportedException cantHappen) {
	   throw new InternalError();
	  }
	  c.key = key;
	  c.value = value;
	  c.info = info;
	  return c;
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public boolean equals(final Object o) {
	  if (!(o instanceof Map.Entry)) return false;
	  Map.Entry <K, Byte> e = (Map.Entry <K, Byte>)o;
	  return java.util.Objects.equals(key, (e.getKey())) && ( (value) == ((e.getValue()).byteValue()) );
	 }
	 @Override
	 public int hashCode() {
	  return ( (key).hashCode() ) ^ (value);
	 }
	 @Override
	 public String toString() {
	  return key + "=>" + value;
	 }
	 /*
		  public void prettyPrint() {
		  prettyPrint(0);
		  }

		  public void prettyPrint(int level) {
		  if (pred()) {
		  for (int i = 0; i < level; i++)
		  System.err.print("  ");
		  System.err.println("pred: " + left);
		  }
		  else if (left != null)
		  left.prettyPrint(level +1);
		  for (int i = 0; i < level; i++)
		  System.err.print("  ");
		  System.err.println(key + "=" + value + " (" + balance() + ")");
		  if (succ()) {
		  for (int i = 0; i < level; i++)
		  System.err.print("  ");
		  System.err.println("succ: " + right);
		  }
		  else if (right != null)
		  right.prettyPrint(level + 1);
		  }*/
	}
	/*
	  public void prettyPrint() {
	  System.err.println("size: " + count);
	  if (tree != null) tree.prettyPrint();
	  }*/
	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(final Object k) {
	 if (k == null) return false;
	 return findKey((K) k) != null;
	}
	@Override
	public int size() {
	 return count;
	}
	@Override
	public boolean isEmpty() {
	 return count == 0;
	}
	@SuppressWarnings("unchecked")
	@Override
	public byte getByte(final Object k) {
	 final Entry <K> e = findKey((K) k);
	 return e == null ? defRetValue : e.value;
	}
	@Override
	public K firstKey() {
	 if (tree == null) throw new NoSuchElementException();
	 return firstEntry.key;
	}
	@Override
	public K lastKey() {
	 if (tree == null) throw new NoSuchElementException();
	 return lastEntry.key;
	}
	/** An abstract iterator on the whole range.
	 *
	 * <p>This class can iterate in both directions on a threaded tree.
	 */
	private class TreeIterator {
	 /** The entry that will be returned by the next call to {@link java.util.ListIterator#previous()} (or {@code null} if no previous entry exists). */
	 Entry <K> prev;
	 /** The entry that will be returned by the next call to {@link java.util.ListIterator#next()} (or {@code null} if no next entry exists). */
	 Entry <K> next;
	 /** The last entry that was returned (or {@code null} if we did not iterate or used {@link #remove()}). */
	 Entry <K> curr;
	 /** The current index (in the sense of a {@link java.util.ListIterator}). Note that this value is not meaningful when this {@link TreeIterator} has been created using the nonempty constructor.*/
	 int index = 0;
	 TreeIterator() {
	  next = firstEntry;
	 }
	 TreeIterator(final K k) {
	  if ((next = locateKey(k)) != null) {
	   if (compare(next.key, k) <= 0) {
	    prev = next;
	    next = next.next();
	   }
	   else prev = next.prev();
	  }
	 }
	 public boolean hasNext() { return next != null; }
	 public boolean hasPrevious() { return prev != null; }
	 void updateNext() {
	  next = next.next();
	 }
	 Entry <K> nextEntry() {
	  if (! hasNext()) throw new NoSuchElementException();
	  curr = prev = next;
	  index++;
	  updateNext();
	  return curr;
	 }
	 void updatePrevious() {
	  prev = prev.prev();
	 }
	 Entry <K> previousEntry() {
	  if (! hasPrevious()) throw new NoSuchElementException();
	  curr = next = prev;
	  index--;
	  updatePrevious();
	  return curr;
	 }
	 public int nextIndex() {
	  return index;
	 }
	 public int previousIndex() {
	  return index - 1;
	 }
	 public void remove() {
	  if (curr == null) throw new IllegalStateException();
	  /* If the last operation was a next(), we are removing an entry that preceeds
			   the current index, and thus we must decrement it. */
	  if (curr == prev) index--;
	  next = prev = curr;
	  updatePrevious();
	  updateNext();
	  Object2ByteRBTreeMap.this.removeByte(curr.key);
	  curr = null;
	 }
	 public int skip(final int n) {
	  int i = n;
	  while(i-- != 0 && hasNext()) nextEntry();
	  return n - i - 1;
	 }
	 public int back(final int n) {
	  int i = n;
	  while(i-- != 0 && hasPrevious()) previousEntry();
	  return n - i - 1;
	 }
	}
	/** An iterator on the whole range.
	 *
	 * <p>This class can iterate in both directions on a threaded tree.
	 */
	private class EntryIterator extends TreeIterator implements ObjectListIterator<Object2ByteMap.Entry <K> > {
	 EntryIterator() {}
	 EntryIterator(final K k) {
	  super(k);
	 }
	 @Override
	 public Object2ByteMap.Entry <K> next() { return nextEntry(); }
	 @Override
	 public Object2ByteMap.Entry <K> previous() { return previousEntry(); }
	}
	@Override
	@SuppressWarnings("unchecked")
	public ObjectSortedSet<Object2ByteMap.Entry <K> > object2ByteEntrySet() {
	 if (entries == null) entries = new AbstractObjectSortedSet<Object2ByteMap.Entry <K> >() {
	   final Comparator<? super Object2ByteMap.Entry <K> > comparator = (Object2ByteRBTreeMap.this.actualComparator == null ?
	     (Comparator<Object2ByteMap.Entry <K> >) (x, y) -> ( ((Comparable<K>)(x.getKey())).compareTo(y.getKey()) ) :
	     (Comparator<Object2ByteMap.Entry <K> >) (x, y) -> Object2ByteRBTreeMap.this.actualComparator.compare(x.getKey(), y.getKey())
	   );
	   @Override
	   public Comparator<? super Object2ByteMap.Entry <K> > comparator() { return comparator; }
	   @Override
	   public ObjectBidirectionalIterator<Object2ByteMap.Entry <K> > iterator() { return new EntryIterator(); }
	   @Override
	   public ObjectBidirectionalIterator<Object2ByteMap.Entry <K> > iterator(final Object2ByteMap.Entry <K> from) { return new EntryIterator(from.getKey()); }
	   @Override
	   @SuppressWarnings("unchecked")
	   public boolean contains(final Object o) {
	    if (o == null || !(o instanceof Map.Entry)) return false;
	    final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	    if (e.getKey() == null) return false;
	    if (e.getValue() == null || ! (e.getValue() instanceof Byte)) return false;
	    final Entry <K> f = findKey(((K) e.getKey()));
	    return e.equals(f);
	   }
	   @Override
	   @SuppressWarnings("unchecked")
	   public boolean remove(final Object o) {
	    if (!(o instanceof Map.Entry)) return false;
	    final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	    if (e.getKey() == null) return false;
	    if (e.getValue() == null || ! (e.getValue() instanceof Byte)) return false;
	    final Entry <K> f = findKey(((K) e.getKey()));
	    if (f == null || ! ( (f.getByteValue()) == (((Byte)(e.getValue())).byteValue()) )) return false;
	    Object2ByteRBTreeMap.this.removeByte(f.key);
	    return true;
	   }
	   @Override
	   public int size() { return count; }
	   @Override
	   public void clear() { Object2ByteRBTreeMap.this.clear(); }
	   @Override
	   public Object2ByteMap.Entry <K> first() { return firstEntry; }
	   @Override
	   public Object2ByteMap.Entry <K> last() { return lastEntry; }
	   @Override
	   public ObjectSortedSet<Object2ByteMap.Entry <K> > subSet(Object2ByteMap.Entry <K> from, Object2ByteMap.Entry <K> to) { return subMap(from.getKey(), to.getKey()).object2ByteEntrySet(); }
	   @Override
	   public ObjectSortedSet<Object2ByteMap.Entry <K> > headSet(Object2ByteMap.Entry <K> to) { return headMap(to.getKey()).object2ByteEntrySet(); }
	   @Override
	   public ObjectSortedSet<Object2ByteMap.Entry <K> > tailSet(Object2ByteMap.Entry <K> from) { return tailMap(from.getKey()).object2ByteEntrySet(); }
	  };
	 return entries;
	}
	/** An iterator on the whole range of keys.
	 *
	 * <p>This class can iterate in both directions on the keys of a threaded tree. We
	 * simply override the {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and possibly
	 * their type-specific counterparts) so that they return keys instead of entries.
	 */
	private final class KeyIterator extends TreeIterator implements ObjectListIterator <K> {
	 public KeyIterator() {}
	 public KeyIterator(final K k) { super(k); }
	 @Override
	 public K next() { return nextEntry().key; }
	 @Override
	 public K previous() { return previousEntry().key; }
	};
	/** A keyset implementation using a more direct implementation for iterators. */
	private class KeySet extends AbstractObject2ByteSortedMap <K>.KeySet {
	 @Override
	 public ObjectBidirectionalIterator <K> iterator() { return new KeyIterator(); }
	 @Override
	 public ObjectBidirectionalIterator <K> iterator(final K from) { return new KeyIterator(from); }
	}
	/** Returns a type-specific sorted set view of the keys contained in this map.
	 *
	 * <p>In addition to the semantics of {@link java.util.Map#keySet()}, you can
	 * safely cast the set returned by this call to a type-specific sorted
	 * set interface.
	 *
	 * @return a type-specific sorted set view of the keys contained in this map.
	 */
	@Override
	public ObjectSortedSet <K> keySet() {
	 if (keys == null) keys = new KeySet();
	 return keys;
	}
	/** An iterator on the whole range of values.
	 *
	 * <p>This class can iterate in both directions on the values of a threaded tree. We
	 * simply override the {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and possibly
	 * their type-specific counterparts) so that they return values instead of entries.
	 */
	private final class ValueIterator extends TreeIterator implements ByteListIterator {
	 @Override
	 public byte nextByte() { return nextEntry().value; }
	 @Override
	 public byte previousByte() { return previousEntry().value; }
	};
	/** Returns a type-specific collection view of the values contained in this map.
	 *
	 * <p>In addition to the semantics of {@link java.util.Map#values()}, you can
	 * safely cast the collection returned by this call to a type-specific collection
	 * interface.
	 *
	 * @return a type-specific collection view of the values contained in this map.
	 */
	@Override
	public ByteCollection values() {
	 if (values == null) values = new AbstractByteCollection () {
	   @Override
	   public ByteIterator iterator() { return new ValueIterator(); }
	   @Override
	   public boolean contains(final byte k) { return containsValue(k); }
	   @Override
	   public int size() { return count; }
	   @Override
	   public void clear() { Object2ByteRBTreeMap.this.clear(); }
	  };
	 return values;
	}
	@Override
	public Comparator <? super K> comparator() { return actualComparator; }
	@Override
	public Object2ByteSortedMap <K> headMap(K to) { return new Submap((null), true, to, false); }
	@Override
	public Object2ByteSortedMap <K> tailMap(K from) { return new Submap(from, false, (null), true); }
	@Override
	public Object2ByteSortedMap <K> subMap(K from, K to) { return new Submap(from, false, to, false); }
	/** A submap with given range.
	 *
	 * <p>This class represents a submap. One has to specify the left/right
	 * limits (which can be set to -&infin; or &infin;). Since the submap is a
	 * view on the map, at a given moment it could happen that the limits of
	 * the range are not any longer in the main map. Thus, things such as
	 * {@link java.util.SortedMap#firstKey()} or {@link java.util.Collection#size()} must be always computed
	 * on-the-fly.
	 */
	private final class Submap extends AbstractObject2ByteSortedMap <K> implements java.io.Serializable {
	 private static final long serialVersionUID = -7046029254386353129L;
	 /** The start of the submap range, unless {@link #bottom} is true. */
	 K from;
	 /** The end of the submap range, unless {@link #top} is true. */
	 K to;
	 /** If true, the submap range starts from -&infin;. */
	 boolean bottom;
	 /** If true, the submap range goes to &infin;. */
	 boolean top;
	 /** Cached set of entries. */
	 protected transient ObjectSortedSet<Object2ByteMap.Entry <K> > entries;
	 /** Cached set of keys. */
	 protected transient ObjectSortedSet <K> keys;
	 /** Cached collection of values. */
	 protected transient ByteCollection values;
	 /** Creates a new submap with given key range.
		 *
		 * @param from the start of the submap range.
		 * @param bottom if true, the first parameter is ignored and the range starts from -&infin;.
		 * @param to the end of the submap range.
		 * @param top if true, the third parameter is ignored and the range goes to &infin;.
		 */
	 public Submap(final K from, final boolean bottom, final K to, final boolean top) {
	  if (! bottom && ! top && Object2ByteRBTreeMap.this.compare(from, to) > 0) throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
	  this.from = from;
	  this.bottom = bottom;
	  this.to = to;
	  this.top = top;
	  this.defRetValue = Object2ByteRBTreeMap.this.defRetValue;
	 }
	 @Override
	 public void clear() {
	  final SubmapIterator i = new SubmapIterator();
	  while(i.hasNext()) {
	   i.nextEntry();
	   i.remove();
	  }
	 }
	 /** Checks whether a key is in the submap range.
		 * @param k a key.
		 * @return true if is the key is in the submap range.
		 */
	 final boolean in(final K k) {
	  return (bottom || Object2ByteRBTreeMap.this.compare(k, from) >= 0) &&
	   (top || Object2ByteRBTreeMap.this.compare(k, to) < 0);
	 }
	 @Override
	 public ObjectSortedSet<Object2ByteMap.Entry <K> > object2ByteEntrySet() {
	  if (entries == null) entries = new AbstractObjectSortedSet<Object2ByteMap.Entry <K> >() {
	    @Override
	    public ObjectBidirectionalIterator<Object2ByteMap.Entry <K> > iterator() {
	     return new SubmapEntryIterator();
	    }
	    @Override
	    public ObjectBidirectionalIterator<Object2ByteMap.Entry <K> > iterator(final Object2ByteMap.Entry <K> from) {
	     return new SubmapEntryIterator(from.getKey());
	    }
	    @Override
	    public Comparator<? super Object2ByteMap.Entry <K> > comparator() { return Object2ByteRBTreeMap.this.object2ByteEntrySet().comparator(); }
	    @Override
	    @SuppressWarnings("unchecked")
	    public boolean contains(final Object o) {
	     if (!(o instanceof Map.Entry)) return false;
	     final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	     if (e.getValue() == null || ! (e.getValue() instanceof Byte)) return false;
	     final Object2ByteRBTreeMap.Entry <K> f = findKey(((K) e.getKey()));
	     return f != null && in(f.key) && e.equals(f);
	    }
	    @Override
	    @SuppressWarnings("unchecked")
	    public boolean remove(final Object o) {
	     if (!(o instanceof Map.Entry)) return false;
	     final Map.Entry<?,?> e = (Map.Entry<?,?>)o;
	     if (e.getValue() == null || ! (e.getValue() instanceof Byte)) return false;
	     final Object2ByteRBTreeMap.Entry <K> f = findKey(((K) e.getKey()));
	     if (f != null && in(f.key)) Submap.this.removeByte(f.key);
	     return f != null;
	    }
	    @Override
	    public int size() {
	     int c = 0;
	     for(Iterator<?> i = iterator(); i.hasNext(); i.next()) c++;
	     return c;
	    }
	    @Override
	    public boolean isEmpty() { return ! new SubmapIterator().hasNext(); }
	    @Override
	    public void clear() { Submap.this.clear(); }
	    @Override
	    public Object2ByteMap.Entry <K> first() { return firstEntry(); }
	    @Override
	    public Object2ByteMap.Entry <K> last() { return lastEntry(); }
	    @Override
	    public ObjectSortedSet<Object2ByteMap.Entry <K> > subSet(Object2ByteMap.Entry <K> from, Object2ByteMap.Entry <K> to) { return subMap(from.getKey(), to.getKey()).object2ByteEntrySet(); }
	    @Override
	    public ObjectSortedSet<Object2ByteMap.Entry <K> > headSet(Object2ByteMap.Entry <K> to) { return headMap(to.getKey()).object2ByteEntrySet(); }
	    @Override
	    public ObjectSortedSet<Object2ByteMap.Entry <K> > tailSet(Object2ByteMap.Entry <K> from) { return tailMap(from.getKey()).object2ByteEntrySet(); }
	   };
	  return entries;
	 }
	 private class KeySet extends AbstractObject2ByteSortedMap <K>.KeySet {
	  @Override
	  public ObjectBidirectionalIterator <K> iterator() { return new SubmapKeyIterator(); }
	  @Override
	  public ObjectBidirectionalIterator <K> iterator(final K from) { return new SubmapKeyIterator(from); }
	 }
	 @Override
	 public ObjectSortedSet <K> keySet() {
	  if (keys == null) keys = new KeySet();
	  return keys;
	 }
	 @Override
	 public ByteCollection values() {
	  if (values == null) values = new AbstractByteCollection () {
	    @Override
	    public ByteIterator iterator() { return new SubmapValueIterator(); }
	    @Override
	    public boolean contains(final byte k) { return containsValue(k); }
	    @Override
	    public int size() { return Submap.this.size(); }
	    @Override
	    public void clear() { Submap.this.clear(); }
	   };
	  return values;
	 }
	    @Override
	 @SuppressWarnings("unchecked")
	 public boolean containsKey(final Object k) {
	  if (k == null) return false;
	  return in((K) k) && Object2ByteRBTreeMap.this.containsKey(k);
	 }
	 @Override
	 public boolean containsValue(final byte v) {
	  final SubmapIterator i = new SubmapIterator();
	  byte ev;
	  while(i.hasNext()) {
	   ev = i.nextEntry().value;
	   if (( (ev) == (v) )) return true;
	  }
	  return false;
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public byte getByte(final Object k) {
	  final Object2ByteRBTreeMap.Entry <K> e;
	  final K kk = (K) k;
	  return in(kk) && (e = findKey(kk)) != null ? e.value : this.defRetValue;
	 }
	 @Override
	 public byte put(final K k, final byte v) {
	  modified = false;
	  if (! in(k)) throw new IllegalArgumentException("Key (" + k + ") out of range [" + (bottom ? "-" : String.valueOf(from)) + ", " + (top ? "-" : String.valueOf(to)) + ")");
	  final byte oldValue = Object2ByteRBTreeMap.this.put(k, v);
	  return modified ? this.defRetValue : oldValue;
	 }
	 @Override
	 @SuppressWarnings("unchecked")
	 public byte removeByte(final Object k) {
	  modified = false;
	  if (! in((K) k)) return this.defRetValue;
	  final byte oldValue = Object2ByteRBTreeMap.this.removeByte(k);
	  return modified ? oldValue : this.defRetValue;
	 }
	 @Override
	 public int size() {
	  final SubmapIterator i = new SubmapIterator();
	  int n = 0;
	  while(i.hasNext()) {
	   n++;
	   i.nextEntry();
	  }
	  return n;
	 }
	 @Override
	 public boolean isEmpty() { return ! new SubmapIterator().hasNext(); }
	 @Override
	 public Comparator <? super K> comparator() { return actualComparator; }
	 @Override
	 public Object2ByteSortedMap <K> headMap(final K to) {
	  if (top) return new Submap(from, bottom, to, false);
	  return compare(to, this.to) < 0 ? new Submap(from, bottom, to, false) : this;
	 }
	 @Override
	 public Object2ByteSortedMap <K> tailMap(final K from) {
	  if (bottom) return new Submap(from, false, to, top);
	  return compare(from, this.from) > 0 ? new Submap(from, false, to, top) : this;
	 }
	 @Override
	 public Object2ByteSortedMap <K> subMap(K from, K to) {
	  if (top && bottom) return new Submap(from, false, to, false);
	  if (! top) to = compare(to, this.to) < 0 ? to : this.to;
	  if (! bottom) from = compare(from, this.from) > 0 ? from : this.from;
	  if (! top && ! bottom && from == this.from && to == this.to) return this;
	  return new Submap(from, false, to, false);
	 }
	 /** Locates the first entry.
		 *
		 * @return the first entry of this submap, or {@code null} if the submap is empty.
		 */
	 public Object2ByteRBTreeMap.Entry <K> firstEntry() {
	  if (tree == null) return null;
	  // If this submap goes to -infinity, we return the main map first entry; otherwise, we locate the start of the map.
	  Object2ByteRBTreeMap.Entry <K> e;
	  if (bottom) e = firstEntry;
	  else {
	   e = locateKey(from);
	   // If we find either the start or something greater we're OK.
	   if (compare(e.key, from) < 0) e = e.next();
	  }
	  // Finally, if this submap doesn't go to infinity, we check that the resulting key isn't greater than the end.
	  if (e == null || ! top && compare(e.key, to) >= 0) return null;
	  return e;
	 }
	 /** Locates the last entry.
		 *
		 * @return the last entry of this submap, or {@code null} if the submap is empty.
		 */
	 public Object2ByteRBTreeMap.Entry <K> lastEntry() {
	  if (tree == null) return null;
	  // If this submap goes to infinity, we return the main map last entry; otherwise, we locate the end of the map.
	  Object2ByteRBTreeMap.Entry <K> e;
	  if (top) e = lastEntry;
	  else {
	   e = locateKey(to);
	   // If we find something smaller than the end we're OK.
	   if (compare(e.key, to) >= 0) e = e.prev();
	  }
	  // Finally, if this submap doesn't go to -infinity, we check that the resulting key isn't smaller than the start.
	  if (e == null || ! bottom && compare(e.key, from) < 0) return null;
	  return e;
	 }
	 @Override
	 public K firstKey() {
	  Object2ByteRBTreeMap.Entry <K> e = firstEntry();
	  if (e == null) throw new NoSuchElementException();
	  return e.key;
	 }
	 @Override
	 public K lastKey() {
	  Object2ByteRBTreeMap.Entry <K> e = lastEntry();
	  if (e == null) throw new NoSuchElementException();
	  return e.key;
	 }
	 /** An iterator for subranges.
		 *
		 * <p>This class inherits from {@link TreeIterator}, but overrides the methods that
		 * update the pointer after a {@link java.util.ListIterator#next()} or {@link java.util.ListIterator#previous()}. If we would
		 * move out of the range of the submap we just overwrite the next or previous
		 * entry with {@code null}.
		 */
	 private class SubmapIterator extends TreeIterator {
	  SubmapIterator() {
	   next = firstEntry();
	  }
	  SubmapIterator(final K k) {
	   this();
	   if (next != null) {
	    if (! bottom && compare(k, next.key) < 0) prev = null;
	    else if (! top && compare(k, (prev = lastEntry()).key) >= 0) next = null;
	    else {
	     next = locateKey(k);
	     if (compare(next.key, k) <= 0) {
	      prev = next;
	      next = next.next();
	     }
	     else prev = next.prev();
	    }
	   }
	  }
	  @Override
	  void updatePrevious() {
	   prev = prev.prev();
	   if (! bottom && prev != null && Object2ByteRBTreeMap.this.compare(prev.key, from) < 0) prev = null;
	  }
	  @Override
	  void updateNext() {
	   next = next.next();
	   if (! top && next != null && Object2ByteRBTreeMap.this.compare(next.key, to) >= 0) next = null;
	  }
	 }
	 private class SubmapEntryIterator extends SubmapIterator implements ObjectListIterator<Object2ByteMap.Entry <K> > {
	  SubmapEntryIterator() {}
	  SubmapEntryIterator(final K k) {
	   super(k);
	  }
	  @Override
	  public Object2ByteMap.Entry <K> next() { return nextEntry(); }
	  @Override
	  public Object2ByteMap.Entry <K> previous() { return previousEntry(); }
	 }
	 /** An iterator on a subrange of keys.
		 *
		 * <p>This class can iterate in both directions on a subrange of the
		 * keys of a threaded tree. We simply override the {@link
		 * java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and possibly their
		 * type-specific counterparts) so that they return keys instead of
		 * entries.
		 */
	 private final class SubmapKeyIterator extends SubmapIterator implements ObjectListIterator <K> {
	  public SubmapKeyIterator() { super(); }
	  public SubmapKeyIterator(K from) { super(from); }
	  @Override
	  public K next() { return nextEntry().key; }
	  @Override
	  public K previous() { return previousEntry().key; }
	 };
	 /** An iterator on a subrange of values.
		 *
		 * <p>This class can iterate in both directions on the values of a
		 * subrange of the keys of a threaded tree. We simply override the
		 * {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and possibly their
		 * type-specific counterparts) so that they return values instead of
		 * entries.
		 */
	 private final class SubmapValueIterator extends SubmapIterator implements ByteListIterator {
	  @Override
	  public byte nextByte() { return nextEntry().value; }
	  @Override
	  public byte previousByte() { return previousEntry().value; }
	 };
	}
	/** Returns a deep copy of this tree map.
	 *
	 * <p>This method performs a deep copy of this tree map; the data stored in the
	 * set, however, is not cloned. Note that this makes a difference only for object keys.
	 *
	 * @return a deep copy of this tree map.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object2ByteRBTreeMap <K> clone() {
	 Object2ByteRBTreeMap <K> c;
	 try {
	  c = (Object2ByteRBTreeMap <K>)super.clone();
	 }
	 catch(CloneNotSupportedException cantHappen) {
	  throw new InternalError();
	 }
	 c.keys = null;
	 c.values = null;
	 c.entries = null;
	 c.allocatePaths();
	 if (count != 0) {
	  // Also this apparently unfathomable code is derived from GNU libavl.
	  Entry <K> e, p, q, rp = new Entry <>(), rq = new Entry <>();
	  p = rp;
	  rp.left(tree);
	  q = rq;
	  rq.pred(null);
	  while(true) {
	   if (! p.pred()) {
	    e = p.left.clone();
	    e.pred(q.left);
	    e.succ(q);
	    q.left(e);
	    p = p.left;
	    q = q.left;
	   }
	   else {
	    while(p.succ()) {
	     p = p.right;
	     if (p == null) {
	      q.right = null;
	      c.tree = rq.left;
	      c.firstEntry = c.tree;
	      while(c.firstEntry.left != null) c.firstEntry = c.firstEntry.left;
	      c.lastEntry = c.tree;
	      while(c.lastEntry.right != null) c.lastEntry = c.lastEntry.right;
	      return c;
	     }
	     q = q.right;
	    }
	    p = p.right;
	    q = q.right;
	   }
	   if (! p.succ()) {
	    e = p.right.clone();
	    e.succ(q.right);
	    e.pred(q);
	    q.right(e);
	   }
	  }
	 }
	 return c;
	}
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
	 int n = count;
	 EntryIterator i = new EntryIterator();
	 Entry <K> e;
	 s.defaultWriteObject();
	 while(n-- != 0) {
	  e = i.nextEntry();
	  s.writeObject(e.key);
	  s.writeByte(e.value);
	 }
	}
	/** Reads the given number of entries from the input stream, returning the corresponding tree.
	 *
	 * @param s the input stream.
	 * @param n the (positive) number of entries to read.
	 * @param pred the entry containing the key that preceeds the first key in the tree.
	 * @param succ the entry containing the key that follows the last key in the tree.
	 */
	@SuppressWarnings("unchecked")
	private Entry <K> readTree(final java.io.ObjectInputStream s, final int n, final Entry <K> pred, final Entry <K> succ) throws java.io.IOException, ClassNotFoundException {
	 if (n == 1) {
	  final Entry <K> top = new Entry <>((K) s.readObject(), s.readByte());
	  top.pred(pred);
	  top.succ(succ);
	  top.black(true);
	  return top;
	 }
	 if (n == 2) {
	  /* We handle separately this case so that recursion will
			 *always* be on nonempty subtrees. */
	  final Entry <K> top = new Entry <>((K) s.readObject(), s.readByte());
	  top.black(true);
	  top.right(new Entry <>((K) s.readObject(), s.readByte()));
	  top.right.pred(top);
	  top.pred(pred);
	  top.right.succ(succ);
	  return top;
	 }
	 // The right subtree is the largest one.
	 final int rightN = n / 2, leftN = n - rightN - 1;
	 final Entry <K> top = new Entry <>();
	 top.left(readTree(s, leftN, pred, top));
	 top.key = (K) s.readObject();
	 top.value = s.readByte();
	 top.black(true);
	 top.right(readTree(s, rightN, top, succ));
	 if (n + 2 == ((n + 2) & -(n + 2))) top.right.black(false); // Quick test for determining whether n + 2 is a power of 2.
	 return top;
	}
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
	 s.defaultReadObject();
	 /* The storedComparator is now correctly set, but we must restore
		   on-the-fly the actualComparator. */
	 setActualComparator();
	 allocatePaths();
	 if (count != 0) {
	  tree = readTree(s, count, null, null);
	  Entry <K> e;
	  e = tree;
	  while(e.left() != null) e = e.left();
	  firstEntry = e;
	  e = tree;
	  while(e.right() != null) e = e.right();
	  lastEntry = e;
	 }
	}
}
