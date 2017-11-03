package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.github.smk7758.MinecraftServerStatusChecker.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MainController {
	private ServerListItemController sli_ctr = null;
	private Pane pane_children = new Pane();
	@FXML
	private Button button_add_list, button_connect;
	@FXML
	private TextField textfield_port, textfield_adress, textfield_server_name;
	@FXML
	private ScrollPane pane_serverlist; // ListViewがいいかもしれない。

	@FXML
	private void onButtonAddServer() {
		// check field
		String address, port_s;
		InetSocketAddress host = null;
		address = textfield_adress.getText();
		if (address.isEmpty()) address = "127.0.0.1";
		port_s = textfield_port.getText();
		if (port_s.isEmpty()) port_s = "25565";
		try {
			short port = Short.parseShort(port_s);
		} catch (NumberFormatException ex) {
			Main.printDebug("Port field is not a number.");
			System.err.println("Port field is not a number.");
		}
//		try {
//			host = new InetSocketAddress(address, port);
//		} catch (IllegalArgumentException ex) {
//			Main.printDebug("Port parameter is outside the specifid range of valid port values.");
//			System.err.println("Port parameter is outside the specifid range of valid port values.");
//			return;
//		}

		// add Pane of ServerList
		FXMLLoader sli_fxml = new FXMLLoader(getClass().getResource("ServerListItem.fxml"));
		Pane pane = null;
		try {
			pane = sli_fxml.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sli_ctr = sli_fxml.getController();
		sli_ctr.setInitializeItems(textfield_server_name.getText(), address, port_s);
		// host→?
		pane.setLayoutY(pane_children.getChildren().size() * 120);
		pane_children.getChildren().add(pane);
		pane_serverlist.setContent(pane_children);
	}

	// private void onButtonUpItem() {
	// }
	//
	// private void onButtonDownItem() {
	// }

	@FXML
	private void onButtonConnect() {
		// for (Node p : pane_serverlist_children.getChildren()) {
		// if (p instanceof Pane) {
		// Main.printDebug("Pane");
		// }
		// }

		// ConnectThread ct = new ConnectThread(address, port);
		// ct.start();
	}
}
