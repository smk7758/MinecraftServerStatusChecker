package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import com.github.smk7758.MinecraftServerStatusAPI.StatusManager;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Only to manage screen.
 */
public class ServerListItemController {
	@FXML
	public Text text_server_name, text_players, text_ping, text_protocol_version, text_description, text_version,
			text_adress, text_port;
	@FXML
	private ImageView imageview_server_icon, imageview_status;

	public void setInitializeItems(String server_name, String address, String port) {
		text_server_name.setText(server_name);
		text_adress.setText(address);
		text_port.setText(port);
	}

	public void setImageStatus(int status) {
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

	public void setItems(ResponseInterface response) {
		String text_motd_s, text_online_players_s, text_max_players_s, text_version_s, text_protocol_version_s,
				text_ping_s, favicon_s;
		text_motd_s = response.getDescription().getText();
		text_online_players_s = String.valueOf(response.getPlayers().getOnline());
		text_max_players_s = String.valueOf(response.getPlayers().getMax());
		text_version_s = response.getVersion().getName();
		text_protocol_version_s = response.getVersion().getProtocol();
		text_ping_s = String.valueOf(response.getTime());
		favicon_s = response.getFavicon();
		text_description.setText(text_motd_s);
		text_players.setText(getPlayersText(text_online_players_s, text_max_players_s));
		text_version.setText(text_version_s);
		text_protocol_version.setText(text_protocol_version_s);
		text_ping.setText(text_ping_s + " ms");
		if (favicon_s != null && !favicon_s.isEmpty()) imageview_server_icon.setImage(
				new Image(StatusManager.getFaviconAsInputStream(favicon_s)));
	}

	private String getPlayersText(String text_online_players_s, String text_max_players_s) {
		String blank_front = "", blank_back = "";
		int add_blank_length = text_max_players_s.length() - text_online_players_s.length();
		if (add_blank_length != 0) {
			for (int i = 0; i < add_blank_length; i++) {
				if (add_blank_length > 0) blank_front += " ";
				else blank_back += " ";
			}
		}
		return blank_front + text_online_players_s + " / " + text_max_players_s + blank_back;
	}
}
