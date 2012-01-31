package com.snow.IO.EventCallback;

import com.snow.IO.SnowTcpClient;

public interface IODisconnectEventCallback {
	public void Invoke(SnowTcpClient client);
}
