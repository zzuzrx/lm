package com.lm.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<Type> implements Iterable<Type>  {
	/**
	 * @Description Node私有类
	 * 
	 * 其属性和构造函数都是公有的
	 * 这样，其父类可以直接访问其属性
	 * 而外部类根本不知道Node类的存在。
	 */
	@SuppressWarnings("hiding")
	private class Node<Type>{
		public Type data;
		public double value;
		public Node<Type> pre;
		public Node<Type> next;
		public Node(Type d, Double v, Node<Type> p, Node<Type> n){
			this.data = d;
			this.value = v;
			this.pre = p;
			this.next = n;
		}
	}
	
	private Node<Type> Header;
	private Node<Type> Tail;
	private int theSize;
	
	public MyLinkedList(){
		theSize = 0;
		Header 	=  new Node<Type>(null,-1.0*Double.MAX_VALUE,null,null);
		Tail   	=  new Node<Type>(null,Double.MAX_VALUE,Header,null);
		Header.next = Tail;
	}
	
	public void insert(Type item,double value){
		Node<Type> aNode = new Node<Type>(item,value,null,null);
	    //遍历，找到合适位置再插入
		Node<Type> cur = Header;
//		System.out.println(value);
		for(;cur.value < value; cur= cur.next);
		cur.pre.next = aNode;
	    aNode.pre    = cur.pre;
	    aNode.next   = cur;
	    cur.pre = aNode;
	    theSize++;
	}
	public int size(){
		return theSize;
	}

	public Iterator<Type> iterator() {
		// TODO Auto-generated method stub
		return new MyListIterator();
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("");
		Node<Type> cur = Header.next;
		for(;cur!= Tail; cur= cur.next){
			str.append(cur.data).append(",");
		}
		return str.toString();
	}
	
	/**
	 * @Description 内部私有类
	 * 实现了iterator接口
	
	 * @author:lm
	
	 * @time:2014-5-6 下午05:00:47
	
	 */
	private class MyListIterator implements Iterator<Type>{
		Node<Type> current = Header.next;
		public boolean hasNext() {
			return (current != Tail);
		}

		public Type next() {
			if(!hasNext())  throw new IndexOutOfBoundsException();
			Type item = current.data;
			current = current.next;
			return item;
		}
		

		public void remove() {
			if(!hasNext())  throw new NoSuchElementException();
			current.pre.next = current.next;
			current.next.pre = current.pre;
			current = current.next;
			theSize--;
		}
	}
}
