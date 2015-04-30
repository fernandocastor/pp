package br.ufpe.cin.perfbook.counter.exercise;

public class LinkedList implements LinkedListInterface {
	
	private Element left;
	private Element right;

	@Override
	public void push_left(int value) {
		Element element = new Element(value);

		if (isEmpty()) {
			right = element;
		} else {
			left.previous = element;
		}
		
		element.next = left;
		left = element;
	}

	@Override
	public int pop_left() {
		Element element = left;
		if (left.next == null) {
			right = null;
		} else {
			left.next.previous = null;
		}
		left = left.next;
		return element.v;
	}

	@Override
	public void push_right(int value) {
		Element element = new Element(value);

		if (isEmpty()) {
			left = element;
		} else {
			right.next = element;
			element.previous = right;
		}
		right = element;
	}

	@Override
	public int pop_right() {
		Element element = right;
		if (left.next == null) {
			left = null;
		} else {
			right.previous.next = null;
		}
		right = right.previous;
		return element.v;
	}

	public boolean isEmpty() {
		return left == null;
	}
}