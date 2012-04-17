package com.snow.IO;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;

public class SnowTcpClient {
	
	private SelectionKey selectionKey;
	private SocketChannel socket;
	private ArrayList<ByteBuffer> toWrite;
	private Date connectTime;
	private String name = null;
	private Selector selector;
	
	public SnowTcpClient(SelectionKey key, Selector selector) throws IOException {
		this.selectionKey = key;
		this.socket = (SocketChannel)this.selectionKey.channel();
		this.socket.configureBlocking(false);
		this.selector = selector;
		toWrite = new ArrayList<ByteBuffer>();
		connectTime = new Date();
	}
	
	public SelectionKey GetSelectionKey() {
		return this.selectionKey;
	}
	
	public byte[] Read() throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		socket.read(buf);
		return buf.array();
	}
	
	public void Write(byte[] bytes) throws IOException {
		this.selectionKey = socket.register(selector, SelectionKey.OP_WRITE);
		ByteBuffer buf = ByteBuffer.allocate(bytes.length);
		buf = ByteBuffer.wrap(bytes);
		//buf.flip();
		toWrite.add(buf);
		//int nBytes = socket.write(buf);
	
	}
	
	protected void writeBuffers() throws IOException {
		for (ByteBuffer buf : toWrite) {
			socket.write(buf);
		}
		toWrite.clear();
		selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_WRITE));
	}
	
	public void Disconnect() throws IOException {
		socket.close();
		SnowTcpServer.clientDisconnected(this);
	}
	
	public void SetName(String n) {
		this.name = n;
	}
	
	public String GetName() {
		return this.name;
	}
	
	public Date GetConnectionTime() {
		return this.connectTime;
	}
}
