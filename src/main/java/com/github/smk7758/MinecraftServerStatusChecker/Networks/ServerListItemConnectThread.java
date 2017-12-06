package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusManager;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;
import com.github.smk7758.MinecraftServerStatusChecker.Main;
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
		// similar as new SliCtr
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
				return;
			}
		}
		slictr.setImageStatus(1);
		ResponseInterface response = null;
		StringBuffer response_stringbuffer = new StringBuffer();
		try {
			response = StatusManager.receiveResponse(host, response_stringbuffer);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		Main.outputResponseToLogFile(response_stringbuffer.toString(), server_name, address, port);
		if (response != null) {
			slictr.setItems(response);
			slictr.setImageStatus(2);
		} else {
			slictr.setImageStatus(0);
		}
	}

	// if (Main.debug_mode) {
	// String response_string = mcss.receiveServerStatusResponseAsString();
	// Main.outputResponseToLogFile(response_string, server_name, address, port);
	// response = mcss.getServerStatusResponse(response_string);
	// } else {
	// }
	//
	// private class ResponseServerStatus {
	// private InetSocketAddress host = null;
	//
	// public ResponseServerStatus(InetSocketAddress host) {
	// this.host = host;
	// }
	//
	// public ServerStatusResponse getResponse() {
	// ServerStatusResponse response = null;
	// try (MinecraftServerStatusConnection mcss = new MinecraftServerStatusConnection(host);) {
	// mcss.sendHandshakePacket();
	// mcss.sendServerStatusPacket();
	// if (Main.debug_mode) {
	// String response_string = mcss.receiveServerStatusResponseAsString();
	// Main.outputResponseToLogFile(response_string, server_name, address, port);
	// response = mcss.getServerStatusResponse(response_string);
	// } else {
	// response = mcss.receiveServerStatus();
	// }
	// mcss.sendPingPacket();
	// int time_receive = (int) mcss.receivePing();
	// response.setTime(time_receive);
	// Main.printResponse(server_name, response);
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// return null;
	// }
	// return response;
	// }
	// }
}
