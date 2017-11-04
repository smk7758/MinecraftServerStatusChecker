package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;
import com.github.smk7758.MinecraftServerStatusChecker.Screens.ServerListItemController;

public class ServerListItemConnect {
	protected ServerListItemController slictr = null;
	private InetSocketAddress host = null;

	public ServerListItemConnect(ServerListItemController slictr, String server_name, InetSocketAddress host) {
		initialize(slictr, server_name, host);
	}

	public ServerListItemConnect(ServerListItemController slictr, String server_name, String address,
			short port) {
		try {
			this.host = new InetSocketAddress(address, port);
		} catch (IllegalArgumentException ex) {
			Main.printDebug("Port parameter is outside the specifid range of valid port values.");
			System.err.println("Port parameter is outside the specifid range of valid port values.");
			return;
		}
		initialize(slictr, server_name, this.host);
	}

	private void initialize(ServerListItemController slictr, String server_name, InetSocketAddress host) {
		slictr.setInitializeItems(server_name, host.getHostName(), String.valueOf(host.getPort()));
		this.slictr = slictr;
		this.host = host;
	}

	public void startConnection() {
		slictr.setImageStatus(1);
		ResponseServerStatusThread rss = new ResponseServerStatusThread(host);
		rss.start();
	}

	private class ResponseServerStatusThread extends Thread {
		private InetSocketAddress host = null;

		public ResponseServerStatusThread(InetSocketAddress host) {
			this.host = host;
		}

		@Override
		public void run() {
			ServerStatusResponse response = null;
			try (MinecraftServerStatus mcss = new MinecraftServerStatus(host);) {
				mcss.sendHandshakePacket();
				mcss.sendServerStatusPacket();
				response = mcss.recieveServerStatus();
				mcss.sendPingPacket();
				int ping_time = (int) mcss.recievePing();
				response.setTime(ping_time);
				slictr.setItems(response);
				Main.printResponse(response);
				slictr.setImageStatus(2);
			} catch (IOException ex) {
				ex.printStackTrace();
				slictr.setImageStatus(0);
				return;
			}
		}
	}
}
