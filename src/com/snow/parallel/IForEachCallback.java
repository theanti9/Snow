package com.snow.parallel;

public interface IForEachCallback {
	public <T> void Invoke(T item) throws Exception;
}
