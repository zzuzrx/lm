package com.lm.util;

import java.util.Iterator;

/**
 * Interface for a min-priority queue.
 * 
 * @author THC and Scot Drysdale
 */
public interface MaxPriorityQueue<E extends Comparable<E>>{
  /**
   * Is the priority queue empty?
   * @return true if the priority queue is empty, false if not empty.
   */
  public boolean isEmpty();
  
  /**
   * Insert an element into the queue.
   * @param element thing to insert
   */
  public void insert(E element);
  
  /**
   * Return the element with the minimum key, without removing it from the queue.
   * @return the element with the minimum key in the priority queue
   */
  public E maximum();
  
  /**
   * Return the element with the minimum key, and remove it from the queue.
   * @return the element with the minimum key in the priority queue
   */
  public E extractMax();

}
