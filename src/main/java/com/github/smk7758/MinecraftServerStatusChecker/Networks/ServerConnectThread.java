package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseFormatException;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;
import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Screens.ServerListItemController;

public class ServerConnectThread extends Thread {
	private ServerListItemController slictr = null;
	private InetSocketAddress host = null;
	private String server_name = "";
	private int protocol_version = -1;
	private int timeout = 7000;
	private boolean already_run = false;
	private String address = "127.0.0.1";
	private short port = 25565;

	public ServerConnectThread(ServerListItemController slictr, String server_name, InetSocketAddress host) {
		initialize(slictr, server_name, host, host.getHostString(), (short) host.getPort(),
				this.timeout, this.protocol_version);
	}

	public ServerConnectThread(ServerListItemController slictr, String server_name, String address,
			short port) {
		initialize(slictr, server_name, null, address, port, this.timeout, this.protocol_version);
	}

	private void initialize(ServerListItemController slictr, String server_name, InetSocketAddress host, String address,
			short port, int timeout, int protocol_version) {
		// similar as new SliCtr
		slictr.setInitializeItems(server_name, address, String.valueOf(port));
		this.setDaemon(true);
		this.slictr = slictr;
		this.server_name = server_name;
		this.host = host;
		this.address = address;
		this.port = port;
		this.timeout = timeout;
		this.protocol_version = protocol_version;
	}

	public String getAdress() {
		return this.address;
	}

	public short getPort() {
		return this.port;
	}

	public String getServerName() {
		return this.server_name;
	}

	public ServerConnectThread refresh() {
		return new ServerConnectThread(this.slictr, this.server_name, this.host);
	}

	public boolean isAlreadRun() {
		return this.already_run;
	}

	public ServerListItemController getServerListItemController() {
		return this.slictr;
	}

	@Override
	public void run() {
		this.already_run = true;
		if (this.host == null) {
			try {
				this.host = new InetSocketAddress(this.address, this.port);
			} catch (IllegalArgumentException ex) {
				Main.printDebug("Port parameter is outside the specifid range of valid port values.");
				return;
			}
		}
		this.slictr.setImageStatus(1);
		ResponseInterface response = null;
		try {
			// TODO: timeout, protocol_version add.
			response = StatusResponseReceive.receiveResponse(this.host, this.server_name);
		} catch (StatusResponseFormatException | IOException e) {
			e.printStackTrace();
		}
		if (response != null) {
			this.slictr.setItems(response);
			this.slictr.setImageStatus(2);
		} else {
			this.slictr.setImageStatus(0);
		}
	}
}
