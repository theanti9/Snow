package com.snow.IO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.snow.IO.EventCallback.IOConnectEventCallback;
import com.snow.IO.EventCallback.IODisconnectEventCallback;
import com.snow.IO.EventCallback.IOReadEventCallback;
import com.snow.parallel.IForEachCallback;
import com.snow.parallel.ParallelLoop;

public class SnowTcpServer {
	
	private ServerSocketChannel serverSockChannel;
	private SocketChannel sockChannel;
	private Selector selector;
	
	private static IOConnectEventCallback connectCallback = null;
	private static IODisconnectEventCallback disconnectCallback = null;
	private static IOReadEventCallback readCallback = null;
	
	private static HashMap<SelectionKey, SnowTcpClient> activeClients;
	
	private int port;
	private int poolSize;
	private int maxPoolSize;
	private int queueSize;
	private long keepAliveTime;
	
	
	public SnowTcpServer(int port, int poolSize, int maxPoolSize, int queueSize, long keepAliveTime) {
		this.port = port;
		this.poolSize = poolSize;
		this.maxPoolSize = maxPoolSize;
		this.queueSize = queueSize;
		this.keepAliveTime = keepAliveTime;
		activeClients = new HashMap<SelectionKey, SnowTcpClient>();
	}
	
	// Register event callbacks
	public void RegisterCallback(IOConnectEventCallback callback) {
		connectCallback = callback;
	}
	
	public void RegisterCallback(IODisconnectEventCallback callback) {
		disconnectCallback = callback;
	}
	
	public void RegisterCallback(IOReadEventCallback callback) {
		readCallback = callback;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void Start() throws IOException, UnknownHostException {
		// Open selector
		selector = Selector.open();
		// Open server channel
		serverSockChannel = ServerSocketChannel.open();
		// Set server to not block
		serverSockChannel.configureBlocking(false);
		// Get the socked address object for localhost on the listening port
		InetSocketAddress isa = new InetSocketAddress("0.0.0.0",this.port);
		// Bind the server
		serverSockChannel.socket().bind(isa);
		// Get the selection key for accepting
		SelectionKey acceptKey = serverSockChannel.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("LISTENING");
		// Set parallel loop handler variables
		ParallelLoop.PoolSize = this.poolSize;
		ParallelLoop.MaxPoolSize = this.maxPoolSize;
		ParallelLoop.QueueSize = this.queueSize;
		ParallelLoop.KeepAliveTime = this.keepAliveTime;
		// Wait for connections
		while (acceptKey.selector().select() > 0) {
			// Get the keys for new connections
			Set readyKeys = selector.selectedKeys();
			// Get the iterator for new connections
			Iterator it = readyKeys.iterator();
			// Handle the selected items in parallel
			ParallelLoop.ForEach(it, new SelectIteratorCallback(), true);
		}
	}
	
	protected static void clientDisconnected(SnowTcpClient client) {
		activeClients.remove(client.GetSelectionKey());
		disconnectCallback.Invoke(client);
	}
	
	private class SelectIteratorCallback implements IForEachCallback {

		@Override
		public <T> void Invoke(T item) throws IOException {
			// Get the seleciton key for the current new connection
			SelectionKey key = (SelectionKey)item;
			// If it's a good key
			if (key.isAcceptable()) {
				// Get the server channel
				ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
				// Accept the connection
				sockChannel = (SocketChannel)ssc.accept();
				// set the socket to non-blocking
				sockChannel.configureBlocking(false);
				// Get read/write SelectionKey and register it with the selector
				SelectionKey rw = sockChannel.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE);
				// Create the client object
				SnowTcpClient client = new SnowTcpClient(rw);
				// Add client to client list
				activeClients.put(rw, client);
				// Invoke connect callback
				connectCallback.Invoke(client);
			}
			
			if (key.isReadable()) {
				SnowTcpClient c = activeClients.get(key);
				try {
					readCallback.Invoke(c, c.Read());
				} catch (IOException e) {
					// This means the client disconnected. call disconnect callback
					clientDisconnected(c);
				}
			}
			
			if (key.isWritable()) {
				SnowTcpClient c = activeClients.get(key);
				try {
					c.writeBuffers();
				} catch (IOException e) {
					// This means the client disconnected. call disconnect callback
					clientDisconnected(c);
				}
			}
		}
	}
	
}
