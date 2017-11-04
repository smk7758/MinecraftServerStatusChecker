package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ServerListItemController {
	private InetSocketAddress host = null;
	@FXML
	private Text text_server_name, text_players, text_ping, text_protocol_version, text_description, text_version,
			text_adress, text_port;
	@FXML
	private ImageView imageview_server_icon, imageview_status;
	// ServerListItem sli = null;

	@FXML
	private void onServerListItemClicked() {
		Main.printDebug("clicked serverlistitem");
	}

	public void setInitializeItems(String server_name, String address, short port) {
		text_server_name.setText(server_name);
		text_adress.setText(address);
		text_port.setText(String.valueOf(port));
		try {
			host = new InetSocketAddress(address, port);
		} catch (IllegalArgumentException ex) {
			Main.printDebug("Port parameter is outside the specifid range of valid port values.");
			System.err.println("Port parameter is outside the specifid range of valid port values.");
			return;
		}
	}

	public void setInitializeItems(String server_name, InetSocketAddress host) {
		text_server_name.setText(server_name);
		text_adress.setText(host.getHostName());
		text_port.setText(String.valueOf(host.getPort()));
	}

	private void setImageStatus(int status) {
		String icon_name = null;
		switch (status) {
			case 0:
				icon_name = "Crossout failure.png";
				break;
			case 1:
				icon_name = "Continous Cycle Circle.png";
				break;
			case 2:
				icon_name = "Checkmark success.png";
				break;
			default:
				icon_name = "Continous Cycle Circle.png";
		}
		Image image = new Image(getClass().getResourceAsStream("/icon/" + icon_name));
		imageview_status.setImage(image);
	}

	public void setItems(ServerStatusResponse response) {
		String text_motd_s, text_online_players_s, text_max_players_s, text_version_s, text_protocol_version_s,
				favicon_s;
		text_motd_s = response.getDescription().getText();
		text_online_players_s = String.valueOf(response.getPlayers().getOnline());
		text_max_players_s = String.valueOf(response.getPlayers().getMax());
		text_version_s = response.getVersion().getName();
		text_protocol_version_s = response.getVersion().getProtocol();
		favicon_s = response.getFavicon();
		text_description.setText(text_motd_s);
		text_players.setText(text_online_players_s + " / " + text_max_players_s);
		text_version.setText(text_version_s);
		text_protocol_version.setText(text_protocol_version_s);
		if (favicon_s != null && !favicon_s.isEmpty()) imageview_server_icon.setImage(new Image(getFaviconAsInputStream(favicon_s)));
	}

	public InputStream getFaviconAsInputStream(String favicon) {
		if (favicon == null) throw new NullPointerException("String of favicon must not be null.");
		favicon = favicon.split(",")[1];
		byte[] image_byte = javax.xml.bind.DatatypeConverter.parseBase64Binary(favicon);
		return new ByteArrayInputStream(image_byte);
	}

	// Threadである意味ェ
	// →ControllerがThreadでなければならないはず。
	public void startConnect() {
		if (host == null) throw new NullPointerException(
				"Host in ServerListItem must not be null. Please set it when you initialize.");
		ResponseServerStatusThread rsst = new ResponseServerStatusThread(this.host);
		rsst.start();
	}

	private class ResponseServerStatusThread extends Thread {
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
				assertNotNull(response);
				mcss.sendPingPacket();
				int ping_time = (int) mcss.recievePing();
				response.setTime(ping_time);
				Main.printResponse(response);
				setItems(response);
				setImageStatus(2);
			} catch (IOException ex) {
				ex.printStackTrace();
				return;
			}
		}

		// public ServerStatusResponse getResponse() {
		// return response;
		// }
	}
}
