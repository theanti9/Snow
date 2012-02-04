package com.snow.parallel;

import java.util.Iterator;

public class SafeIterator<T> {

	final Iterator<T> in;
	
	public SafeIterator(Iterator<T> in) {
		this.in = in;
	}
	
	public synchronized T NextOrNull() {
		T out;
		if (in.hasNext()) {
			out = in.next();
		} else {
			out = null;
		}
		return out;
	}
	
}
