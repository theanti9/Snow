##Snow

Snow is a simple Non-blocking Server library built on top of Java's NIO. 

###SnowTcpServer

This is the main point of the project. It allows you to create a server listener and handle the socket selections through the use of callbacks run inside a thread pool.


####Constructor

    public SnowTcpServer(int port, int poolSize, int maxPoolSize, int queueSize, long keepAliveTime)

Args:

* `port` - The port you want the server to listen on
* `poolSize` - the base pool size you want in the thread pool
* `maxPoolSize` - the maximum number of threads you want in the thread pool
* `queueSize` - the capacity of the thready pool
* `keepAliveTime` - the number of seconds a thread should wait before timing out.


####RegisterCallback
This function should be used to register the callbacks for Connect, Read, and Disconnect events.

    public void RegisterCallback(IOConnectEventCallback callback)
    public void RegisterCallback(IODisconnectEventCallback callback)
    public void RegisterCallback(IOReadEventCallback callback)

Args:

* `callback` - One of `com.snow.IO.EventCallback` s  to register.

####Start
Starts the server

    public void Start()

Args:

* `None`


##SnowTcpClient
This object represents a client connection to the server

####Constructor
    
    public SnowTcpClient(SelectionKey key) throws IOException

Args:

* `key` - This is the selection key that the server passes to create the object and get the SocketChannel

####GetSelectionKey

    public SelectionKey GetSelectionKey()

Args:

* `None`

####Read
Reads ready data into a byte buffer and returns it as an array

    public byte[] Read() throws IOException

Args:

* `None`


####Write
Write data from a byte array to the socket channel

    public void Write(byte[] bytes) throws IOException

Args:

* `bytes` - A byte array containing the data to be written to the channel

####Disconnect
Disconnect a client from the server

    public void Disconnect() throws IOException

Args:

* `None`

####SetName
Set a string connection name

    public void SetName(String n)

Args:

* `n` - String to set the name of the client to

####GetName
Get the set connection name

    public void GetName()

Args:

* `None`


####GetConnectionTime
Get the Date object representing the time the client connected

    public Date GetConnectionTime()

Args:

* `None`

##ParallelLoop

####Public Static Members
These can be changed before you call the ForEach method in order to adjust the options for the thread pool youd like to use

* `int PoolSize = 2` - The core pool size to use in the thread execution pool
* `int MaxPoolSize` - The maximum pool size for the thread execution pool
* `int QueueSize` - The size of the thread queue.
* `long KeepAliveTime` - How long a thread should wait before it times out

####ForEach
This static function can be passed an iterator and a callback to use for each iteration which will be executed in a parallel thread pool

    public static <T> void ForEach(Iterator<T> it, final IForEachCallback callback, boolean remove)
    public static <T> void ForEach(Iterator<T> it, final IForEachCallback callback)

Args:

* `it` - The iterator to be iterated over
* `callback` - The callback to be used on each item in the loop
* `remove` *optional* - Marks whether to remove an item from the iterator after consuming it. default is `false`


* * *

## Callback Interfaces

###IForEachCallback

**Methods**

    public <T> void Invoke(T item) throws Exception;

Args:

* `item` - This is the object that gets passed to the callback from the loop

Usage Example:

	Iterator it;

	// Initialize it here

    ParallelLoop.ForEach(it, new IForEachCallback() {
    	public <T> void Invoke(T item) throws IOException {
    		// code with your item here
    	}
	}, true);

###IOConnectEventCallback

**Methods**

    public void Invoke(SnowTcpClient client);
    
Args:

* `client` - The SnowTcpClient object representing the new connection

Usage Example:

    server.RegisterCallback(new IOConnectEventCallback() {
		public void Invoke(SnowTcpClient client) {
			// Set the name to the date string with no spaces.
			client.SetName(client.getConnectionTime().toString().replace(" ", ""));
    	}
    });

###IODisconnectEventCallback

**Methods**

    public void Invoke(SnowTcpClient client);
    
Args:

* `client` - The SnowTcpClient object representing the disconnected client

Usage Example:

    server.RegisterCallback(new IODisconnectEventCallback() {
    	public void Invoke(SnowTcpClient client) {
    		// Do cleanup here
    	}
    });

###IOReadEventCallback

**Methods**

    public void Invoke(SnowTcpClient client, byte[] buff);
    
Args:

* `client` - The SnowTcpClient object representing the disconnected client
* `buff` - the bytes read from the socket channel object as a byte array

Usage Example:

    server.RegisterCallback(new IOReadEventCallback() {
    	public void Invoke(SnowTcpClient client, byte[] buff) {
    		// return it to the client
    		client.Write(buff);
    	}
    });