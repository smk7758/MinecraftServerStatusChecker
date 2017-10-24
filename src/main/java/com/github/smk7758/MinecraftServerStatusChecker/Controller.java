package com.github.smk7758.MinecraftServerStatusChecker;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.MCServerStatus.ServerStatusResponse;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {
	@FXML
	private TextField textfield_adress, textfield_port;
	@FXML
	private Text text_motd, text_online_players, text_max_players, text_version, text_protocol_version;
	@FXML
	private Button button_connect;

	@FXML
	private void onButtonConnect() {
		InetSocketAddress host = null;
		ServerStatusResponse response = null;
		long ping_time = 0;
		String address = textfield_adress.getText();
		short port = Short.parseShort(textfield_port.getText());
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
			ping_time = mcss.recievePing();
			response.setTime((int) ping_time);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		Main.printResponse(response);
		text_motd.setText(response.getDescription().getText());
		text_online_players.setText(String.valueOf(response.getPlayers().getOnline()));
		text_max_players.setText(String.valueOf(response.getPlayers().getMax()));
		text_version.setText(response.getVersion().getName());
		text_protocol_version.setText(response.getVersion().getProtocol());
	}
}
