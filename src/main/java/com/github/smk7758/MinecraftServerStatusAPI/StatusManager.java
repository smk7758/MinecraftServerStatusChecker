package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;

public class StatusManager {
	private InetSocketAddress host = null;
	public StatusConnection mssc = null;

	public StatusManager(InetSocketAddress host) {
		this.host = host;
	}

	public ResponseInterface receiveResponse() {
		ResponseInterface response = null;
		try (StatusConnection mssc = new StatusConnection(host);) {
			mssc.sendHandshakePacket();
			mssc.sendServerStatusPacket();
			// if (Main.debug_mode) {
			// String response_string = mcss.receiveServerStatusResponseAsString();
			// Main.outputResponseToLogFile(response_string, server_name, address, port);
			// response = mcss.getServerStatusResponse(response_string);
			// } else {
			StatusOutputter mssro = new StatusOutputter(mssc);
			response = mssro.receiveServerStatus();
			// }
			mssc.sendPingPacket();
			int time_receive = (int) mssc.receivePing();
			response.setTime(time_receive);
			// Main.printResponse(server_name, response);
			this.mssc = mssc;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return response;
	}

	public String getReceivedResponse() throws IOException {
		return this.mssc.getReceivedResponse();
	}

	public static void printResponse(ResponseInterface response) {
		// if (debug_mode) {
		String is_favicon = "true";
		if (response.getFavicon() == null || response.getFavicon().isEmpty())
			is_favicon = "false";
		String resposes = "Version: " + response.getVersion().getName() + System.lineSeparator()
				+ "OnlinePlayers / MaximumPlayers: " + response.getPlayers().getOnline() + " / "
				+ response.getPlayers().getMax() + System.lineSeparator() + "Ping: " + response.getTime()
				+ System.lineSeparator() + "isFavicon(Icon): " + is_favicon + System.lineSeparator()
				+ "Description(MOTD): " + response.getDescription().getText();
		System.out.println(resposes);
		// }
	}
}
