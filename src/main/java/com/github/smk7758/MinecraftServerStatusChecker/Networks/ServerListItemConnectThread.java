package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;
import com.github.smk7758.MinecraftServerStatusChecker.Screens.ServerListItemController;

public class ServerListItemConnectThread extends Thread {
	private ServerListItemController slictr = null;
	private InetSocketAddress host = null;
	private String server_name = "";
	private boolean already_run = false;
	private String address = "127.0.0.1";
	private short port = 25565;

	public ServerListItemConnectThread(ServerListItemController slictr, String server_name, InetSocketAddress host) {
		initialize(slictr, server_name, host, host.getHostString(), (short) host.getPort());
	}

	public ServerListItemConnectThread(ServerListItemController slictr, String server_name, String address,
			short port) {
		initialize(slictr, server_name, null, address, port);
	}

	private void initialize(ServerListItemController slictr, String server_name, InetSocketAddress host, String address,
			short port) {
		// similer as new SliCtr
		slictr.setInitializeItems(server_name, address, String.valueOf(port));
		this.setDaemon(true);
		this.slictr = slictr;
		this.server_name = server_name;
		this.host = host;
		this.address = address;
		this.port = port;
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

	public ServerListItemConnectThread refresh() {
		return new ServerListItemConnectThread(slictr, server_name, host);
	}

	public boolean isAlreadRun() {
		return already_run;
	}

	public ServerListItemController getServerListItemController() {
		return this.slictr;
	}

	@Override
	public void run() {
		already_run = true;
		if (this.host == null) {
			try {
				this.host = new InetSocketAddress(this.address, this.port);
			} catch (IllegalArgumentException ex) {
				Main.printDebug("Port parameter is outside the specifid range of valid port values.");
				System.err.println("Port parameter is outside the specifid range of valid port values.");
				return;
			}
		}
		slictr.setImageStatus(1);
		ResponseServerStatus rss = new ResponseServerStatus(host);
		ServerStatusResponse response = rss.getResponse();
		if (response != null) {
			slictr.setItems(response);
			slictr.setImageStatus(2);
		} else {
			slictr.setImageStatus(0);
		}
	}

	private class ResponseServerStatus {
		private InetSocketAddress host = null;

		public ResponseServerStatus(InetSocketAddress host) {
			this.host = host;
		}

		public ServerStatusResponse getResponse() {
			ServerStatusResponse response = null;
			try (MinecraftServerStatus mcss = new MinecraftServerStatus(host);) {
				mcss.sendHandshakePacket();
				mcss.sendServerStatusPacket();
				response = mcss.recieveServerStatus();
				mcss.sendPingPacket();
				int time_receive = (int) mcss.recievePing();
				response.setTime(time_receive);
				Main.printResponse(server_name, response);
			} catch (IOException ex) {
				ex.printStackTrace();
				return null;
			}
			return response;
		}
	}
}
