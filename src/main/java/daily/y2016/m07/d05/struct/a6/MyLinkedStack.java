package daily.y2016.m07.d05.struct.a6;

public class MyLinkedStack<T> {

	private static final int DEFAULT_CAPACITY = 10;
	
	private int theSize;
	private Node<T> top;
	
	private static class Node<T> {
		private Node<T> next;
		private T data;
		public Node(Node<T> next, T t) {
			this.next = next;
			data = t;
		}
	}
	
	public MyLinkedStack() {
		clear();
	}
	public void clear() {
		top = null;
		theSize =0;
	}
	public boolean isEmpty() {
		return top==null;
	}
	public T top() {
		if(isEmpty()) 
			throw new RuntimeException();
		return top.data;
	}
	public T pop() {
		if(isEmpty())
			throw new RuntimeException();
		T t = top.data;
		top = top.next;
		theSize--;
		return t;
	}
	public boolean push(T x) {
		Node newTop = new Node<T>(top, x);
		top = newTop ;
		theSize++;
		return true;
	}
}
