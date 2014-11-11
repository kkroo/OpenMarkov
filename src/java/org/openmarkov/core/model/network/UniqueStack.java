package org.openmarkov.core.model.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/** This class stack ensures that each element is stored only once.
 * @author marias */ 
public class UniqueStack<T> {
	private final Stack<T> stack = new Stack<T>();
	private final HashSet<T> set = new HashSet<T>();

	/** @param t template parameter. <code>T</code>
	 * @return true (as specified by Collection.add) <code>boolean</code> */
	public boolean push(T t) {
		// Only add element to stack if the set does not contain the specified element.
		if (set.add(t)) {
			stack.add(t);
		}
		return true;
	}

	/** @return t template parameter. <code>T</code> */
	public T pop() {
		T ret = stack.pop();
		set.remove(ret);
		return ret;
	}
	
	/** @return <code>boolean</code> */
	public boolean empty() {
		return set.isEmpty();
	}
	
	/** @return <code>List</code> of the template class T */
	public List<T> list() {
		return new ArrayList<T>(stack);
	}
}

