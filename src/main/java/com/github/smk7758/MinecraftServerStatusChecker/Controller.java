package com.github.smk7758.MinecraftServerStatusChecker;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.MCServerStatus.ServerStatusResponse;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class Controller {
	@FXML
	private TextField textfield_adress, textfield_port;
	@FXML
	private Text text_motd, text_online_players, text_max_players, text_version, text_protocol_version;
	@FXML
	private Button button_connect;
	@FXML
	private ImageView imageview_server_icon, imageview_status;

	@FXML
	private void onButtonConnect() {
		String address = textfield_adress.getText();
		if (!address.isEmpty()) address = "127.0.0.1";
		String port_s = textfield_port.getText();
		if (port_s.isEmpty()) port_s = "25565";
		short port = Short.parseShort(port_s);
		ConnectThread ct = new ConnectThread(address, port);
		ct.start();
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

	public String[] getItems(ServerStatusResponse response) {
		String[] items = new String[10];
		items[0] = response.getDescription().getText();
		items[1] = String.valueOf(response.getPlayers().getOnline());
		items[2] = String.valueOf(response.getPlayers().getMax());
		items[3] = response.getVersion().getName();
		items[4] = response.getVersion().getProtocol();
		items[5] = response.getFavicon();
		return items;
	}

	public void setItems(String text_motd_s, String text_online_players_s, String text_max_players_s,
			String text_version_s, String text_protocol_version_s, String favicon) {
		text_motd.setText(text_motd_s);
		text_online_players.setText(text_online_players_s);
		text_max_players.setText(text_max_players_s);
		text_version.setText(text_version_s);
		text_protocol_version.setText(text_protocol_version_s);
		if (!favicon.isEmpty()) imageview_server_icon.setImage(new Image(getFaviconAsInputStream(favicon)));
	}

	public InputStream getFaviconAsInputStream(String favicon) {
		if (favicon == null) throw new NullPointerException("String of favicon must not be null.");
		favicon = favicon.split(",")[1];
		byte[] image_byte = javax.xml.bind.DatatypeConverter.parseBase64Binary(favicon);
		return new ByteArrayInputStream(image_byte);
	}

	private class ConnectThread extends Thread {
		String address = null;
		short port = 0;

		public ConnectThread(String address, short port) {
			this.address = address;
			this.port = port;
			setImageStatus(1);
		}

		public void run() {
			InetSocketAddress host = null;
			ServerStatusResponse response = null;
			try {
				host = new InetSocketAddress(address, port);
			} catch (IllegalArgumentException ex) {
				System.err.println("Port parameter is outside the specifid range of valid port values.");
				return;
			}
			try (MCServerStatus mcss = new MCServerStatus(host);) {
				mcss.sendHandshakePacket();
				mcss.sendServerStatusPacket();
				response = mcss.recieveServerStatus();
				mcss.sendPingPacket();
				int ping_time = (int) mcss.recievePing();
				response.setTime(ping_time);
				Main.printResponse(response);
				String[] items = getItems(response);
				setItems(items[0], items[1], items[2], items[3], items[4], items[5]); // クソコード説。
				setImageStatus(2);
			} catch (IOException ex) {
				setImageStatus(0);
				ex.printStackTrace();
				return;
			}
		}
	}
}
