package com.snow.IO.EventCallback;

import com.snow.IO.SnowTcpClient;

public interface IOConnectEventCallback {
	public void Invoke(SnowTcpClient client);
}
