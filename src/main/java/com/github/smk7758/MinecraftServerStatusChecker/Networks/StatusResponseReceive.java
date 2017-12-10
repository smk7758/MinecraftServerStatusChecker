package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusConnection;
import com.github.smk7758.MinecraftServerStatusAPI.StatusOutputter;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseFormatException;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;
import com.github.smk7758.MinecraftServerStatusChecker.Main;

public class StatusResponseReceive {
	public static ResponseInterface receiveResponse(InetSocketAddress host, String server_name)
			throws IOException, StatusResponseFormatException {
		ResponseInterface response = null;
		String response_string = null;
		try (StatusConnection status_connection = new StatusConnection(host);) {
			status_connection.sendHandshakePacket();
			status_connection.sendServerStatusPacket();
			response_string = status_connection.receiveResponseAsString();
			response = StatusOutputter.convertResponse(response_string);
			status_connection.sendPingPacket();
			int time_receive = (int) status_connection.receivePing();
			response.setTime(time_receive);
		}
		Main.outputResponseToLogFile(response_string, server_name, host.getHostName(), (short) host.getPort());
		return response;
	}
}
