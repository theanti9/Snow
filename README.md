**Snow**

Snow is a simple Non-blocking Server library built on top of Java's NIO. 

****SnowTcpServer****

This is the main point of the project. It allows you to create a server listener and handle the socket selections through the use of callbacks run inside a thread pool.


***Constructor***

    public SnowTcpServer(int port, int poolSize, int maxPoolSize, int queueSize, long keepAliveTime)

Args:

* `port` - The port you want the server to listen on
* `poolSize` - the base pool size you want in the thread pool
* `maxPoolSize` - the maximum number of threads you want in the thread pool
* `queueSize` - the capacity of the thready pool
* `keepAliveTime` - the number of seconds a thread should wait before timing out.


***RegisterCallback***
This function should be used to register the callbacks for Connect, Read, and Disconnect events.

    public void RegisterCallback(IOConnectEventCallback callback)
    public void RegisterCallback(IODisconnectEventCallback callback)
    public void RegisterCallback(IOReadEventCallback callback)

Args:

* `callback` - One of `com.snow.IO.EventCallback` s  to register.

***Start***
Starts the server

    public void Start()

Args:

* `None`


****SnowTcpClient****
This object represents a client connection to the server

***Constructor***
    
    public SnowTcpClient(SelectionKey key) throws IOException

Args:

* `key` - This is the selection key that the server passes to create the object and get the SocketChannel

***GetSelectionKey***

    public SelectionKey GetSelectionKey()

Args:

* `None`

***Read***
Reads ready data into a byte buffer and returns it as an array

    public byte[] Read() throws IOException

Args:

* `None`


***Write***
Write data from a byte array to the socket channel

    public void Write(byte[] bytes) throws IOException

Args:

* `bytes` - A byte array containing the data to be written to the channel

***Disconnect***
Disconnect a client from the server

    public void Disconnect() throws IOException

Args:

* `None`

***SetName***
Set a string connection name

    public void SetName(String n)

Args:

* `n` - String to set the name of the client to

***FetName***
Get the set connection name

    public void GetName()

Args:

* `None`


***GetConnectionTime***
Get the Date object representing the time the client connected

    public Date GetConnectionTime()

Args:

* `None`	