package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;

public class StatusManager {

	private StatusManager() {
	}

	public static ResponseInterface receiveResponse(InetSocketAddress host, StringBuffer response_stringbuffer)
			throws IOException {
		ResponseInterface response = null;
		try (StatusConnection status_connection = new StatusConnection(host);) {
			status_connection.sendHandshakePacket();
			status_connection.sendServerStatusPacket();
			response_stringbuffer.append(status_connection.receiveResponseAsString());
			// response = StatusOutputter.convertResponse(response_stringbuffer.toString());
			response = StatusOutputter.receiveResponse(status_connection);
			status_connection.sendPingPacket();
			int time_receive = (int) status_connection.receivePing();
			response.setTime(time_receive);
		}
		return response;
	}

	public static ResponseInterface receiveResponse(InetSocketAddress host) throws IOException {
		ResponseInterface response = null;
		try (StatusConnection status_connection = new StatusConnection(host);) {
			status_connection.sendHandshakePacket();
			status_connection.sendServerStatusPacket();
			response = StatusOutputter.receiveResponse(status_connection);
			status_connection.sendPingPacket();
			int time_receive = (int) status_connection.receivePing();
			response.setTime(time_receive);
		}
		return response;
	}

	// 必要性…。
	public static String recieveResponseAsString(StatusConnection status_connection) throws IOException {
		return status_connection.receiveResponseAsString();
	}

	public static void printResponse(ResponseInterface response) {
		String is_favicon = "true";
		if (response.getFavicon() == null || response.getFavicon().isEmpty()) is_favicon = "false";
		String resposes = "Version: " + response.getVersion().getName() + System.lineSeparator()
				+ "OnlinePlayers / MaximumPlayers: " + response.getPlayers().getOnline() + " / "
				+ response.getPlayers().getMax() + System.lineSeparator() + "Ping: " + response.getTime()
				+ System.lineSeparator() + "isFavicon(Icon): " + is_favicon + System.lineSeparator()
				+ "Description(MOTD): " + response.getDescription().getText();
		System.out.println(resposes);
	}

	public static InputStream getFaviconAsInputStream(String favicon) {
		if (favicon == null) throw new NullPointerException("String of favicon must not be null.");
		else if (favicon.isEmpty()) throw new IllegalArgumentException("String of favicon must not be empty.");
		favicon = favicon.split(",")[1];
		byte[] image_byte = javax.xml.bind.DatatypeConverter.parseBase64Binary(favicon);
		return new ByteArrayInputStream(image_byte);
	}

	/*
	 * このクラスは、StatusAPI全体の親的な、または機能的なものとする。また、ユーティリティクラスともする。
	 */
}
