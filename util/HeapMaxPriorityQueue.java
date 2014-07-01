package com.lm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lm.algorithms.abc.Chromosome;
import com.lm.domain.Cell;
import com.lm.util.MaxPriorityQueue;
/**
 * Class to implement a min-priority queue with a heap in an ArrayList.
 * Parts borrowed from Heapsort.java
 * 
 * @author Scot Drysdale
 */
public class HeapMaxPriorityQueue<E extends Comparable<E>> 
implements Iterable<E>,MaxPriorityQueue<E>  {
	private ArrayList<E> heap;
	private boolean IsFull;
	private int     Size;
	/**
	 *  Constructor
	 */
	public HeapMaxPriorityQueue(int size) {
		heap   = new ArrayList<E>(size);
		IsFull = false;
		Size   = size;
	}

	/**
	 * Return the element with the minimum key, and remove it from the queue.
	 * @return the element with the minimum key, or null if queue empty.
	 */
	public E extractMax() {
		if (heap.size() <= 0)
			return null;
		else {
			E minVal = heap.get(0);
			heap.set(0, heap.get(heap.size()-1));  // Move last to position 0
			heap.remove(heap.size()-1);
			maxHeapify(heap, 0);
			IsFull = false;
			return minVal;
		}
	}

	/**
	 * Insert an element into the priority queue.  
	 * Keep in heap order
	 * @param element the element to insert
	 */
	public void insert(E element) {
		if(IsFull){	// is Full
			if(element.compareTo(heap.get(0)) >= 0)
				return;
			heap.set(0, element);//replace the end;
			maxHeapify(heap,0);
		}else{ // not Full
			heap.add(element);        // Put new value at end;
			
			if(heap.size()==this.Size) 
				IsFull = true;
			
			int loc = heap.size()-1;  // and get its location
			// Swap with parent until parent not larger
			while (loc > 0 && heap.get(loc).compareTo(heap.get(parent(loc))) > 0) {
				swap(heap, loc, parent(loc));
				loc = parent(loc);
			}
		}
	}
	
	/**
	 * Is the priority queue empty?
	 * @return true if the queue is empty, false if not empty.
	 */
	public boolean isEmpty() {
		return heap.size() == 0;
	}


	/**
	 * Return the element with the minimum key, without removing it from the queue.
	 * @return the element with the minimum key, or null if queue empty.
	 */
	public E maximum() {
		if (heap.size() <= 0)
			return null;
		else
			return heap.get(0);
	}

	/**
	 * Restore the min-heap property.  When this method is called, the min-heap
	 * property holds everywhere, except possibly at node i and its children.
	 * When this method returns, the min-heap property holds everywhere.
	 * @param a the list to sort
	 * @param i the position of the possibly bad spot in the heap
	 */
	private static <E extends Comparable<E>> void maxHeapify(ArrayList<E> a, int i) {
		int left = leftChild(i);    // index of node i's left child
		int right = rightChild(i);  // index of node i's right child
		int Max;    // will hold the index of the node with the Max element
		// among node i, left, and right

		// Is there a left child and, if so, does the left child have an
		// element larger than node i?
		if (left <= a.size()-1 && a.get(left).compareTo(a.get(i)) > 0)
			Max = left;   // yes, so the left child is the largest so far
		else
			Max = i;      // no, so node i is the Max so far

		// Is there a right child and, if so, does the right child have an
		// element larger than the larger of node i and the left child?
		if (right <= a.size()-1 && a.get(right).compareTo(a.get(Max)) > 0)
			Max = right;  // yes, so the right child is the largest

		// If node i holds an element smaller than both the left and right
		// children, then the max-heap property already held, and we need do
		// nothing more.  Otherwise, we need to swap node i with the larger
		// of the two children, and then recurse down the heap from the larger child.
		if (Max != i) {
			swap(a, i, Max);
			maxHeapify(a, Max);
		}
	} 

	/**
	 * Swap two locations i and j in ArrayList a.
	 * @param a the arrayList
	 * @param i first position
	 * @param j second position
	 */
	private static <E> void swap(ArrayList<E> a, int i, int j) {
		E t = a.get(i);
		a.set(i, a.get(j));
		a.set(j, t);
	}

	/**
	 * Return the index of the left child of node i.
	 * @param i index of the parent node
	 * @return index of the left child of node i
	 */
	private static int leftChild(int i) {
		return 2*i + 1;
	}


	/**
	 * Return the index of the right child of node i.
	 * @param i index of parent
	 * @return the index of the right child of node i
	 */
	private static int rightChild(int i) {
		return 2*i + 2;
	}

	/**
	 * Return the index of the parent of node i
	 * (Parent of root will be -1)
	 * @param i index of the child
	 * @return index of the parent of node i
	 */
	private static int parent(int i) {
		return (i-1)/2;
	}
	
	/* (non-Javadoc) Override the iterator for cells
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator() {
		return heap.iterator();
	}
	
	/**
	 * A testing program
	 */
	public static void main (String [] args)  {
		System.out.println(parent(0));
		MaxPriorityQueue<String> pq = new HeapMaxPriorityQueue<String>(5);
		pq.insert("cow");
		pq.insert("dog");
		pq.insert("sheep");
		pq.insert("eagle");
		pq.insert("zebra");
		System.out.println("Max is: " + pq.maximum());
		pq.insert("cat");
		System.out.println("Max is: " + pq.maximum());
		pq.insert("bee");
		System.out.println("Max is: " + pq.maximum());
		pq.insert("ant");
		System.out.println("Max is: " + pq.maximum());
	}

	public int size() {
		return heap.size();
	}

	public E getIndex(int index) {
		return heap.get(index);
	}
}
