package com.snow.IO.EventCallback;

import com.snow.IO.SnowTcpClient;

public interface IOReadEventCallback {
	public void Invoke(SnowTcpClient client, byte[] buffer);
}
