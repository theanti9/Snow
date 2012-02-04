package com.snow.parallel;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ParallelLoop {
	
	// These can be changed before executing the loop
	public static int PoolSize = 2;
	public static int MaxPoolSize = 10;
	public static long KeepAliveTime = 10;
	public static int QueueSize = 20;
	
	private static ThreadPoolExecutor poolExecutor;
	private static final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(QueueSize);
	
	// Pass this an iterator of anything and a callback to perform on each item
	public static <T> void ForEach(Iterator<T> it, final IForEachCallback callback) {
		// Use the thread safe iterator
		SafeIterator<T> sit = new SafeIterator<T>(it);
		// Make the executor pool
		poolExecutor = new ThreadPoolExecutor(PoolSize, MaxPoolSize, KeepAliveTime, TimeUnit.SECONDS, queue);
		// Loop through the iterator and add the callback for each item as a runnable to the executor pool
		while(true) {
			// Get the next item
			final T item = sit.NextOrNull();
			
			// Break if we're done			
			if (item == null) {
				break;
			}
			// Add to the pool
			poolExecutor.execute(new Runnable() {
				public void run() {
					// Invoke callback
					try {
						callback.Invoke(item);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}
