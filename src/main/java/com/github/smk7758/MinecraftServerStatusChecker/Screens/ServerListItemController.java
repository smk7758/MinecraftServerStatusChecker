package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class ServerListItemController {
	public String adress = "127.0.0.1";
	public short port = 25565;
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

	public void setInitializeItems(String server_name, String address, String port) {
		text_server_name.setText(server_name);
		text_adress.setText(address);
		text_port.setText(port);
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
		text_description.setText(text_motd_s);
		text_players.setText(text_online_players_s + " / " + text_max_players_s);
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

//	public void startConnect() {
//		ResponseServerStatusThread rsst = new ResponseServerStatusThread(host);
//		rsst.start();
//		rsst.join();
//		rsst.getResponse();
//		String[] items = getItems(response);
//		setItems(items[0], items[1], items[2], items[3], items[4], items[5]); // クソース感
//		setImageStatus(2);
//	}
}
