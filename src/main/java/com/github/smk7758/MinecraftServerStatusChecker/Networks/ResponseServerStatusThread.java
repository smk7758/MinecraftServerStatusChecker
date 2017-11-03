package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;

public class ResponseServerStatusThread extends Thread {
	private InetSocketAddress host = null;
	private ServerStatusResponse response = null;

	public ResponseServerStatusThread(InetSocketAddress host) {
			this.host = host;
		}

	public void run() {
		try (MinecraftServerStatus mcss = new MinecraftServerStatus(host);) {
			mcss.sendHandshakePacket();
			mcss.sendServerStatusPacket();
			response = mcss.recieveServerStatus();
			mcss.sendPingPacket();
			int ping_time = (int) mcss.recievePing();
			response.setTime(ping_time);
			Main.printResponse(response);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
	}
}
