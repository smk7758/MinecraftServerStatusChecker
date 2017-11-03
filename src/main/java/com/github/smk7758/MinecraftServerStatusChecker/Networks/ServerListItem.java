package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.net.InetSocketAddress;

public class ServerListItem {
	String server_name = "";
	InetSocketAddress host = null;

	public ServerListItem(String server_name, InetSocketAddress host) {
		this.server_name = server_name;
		this.host = host;
	}
}
