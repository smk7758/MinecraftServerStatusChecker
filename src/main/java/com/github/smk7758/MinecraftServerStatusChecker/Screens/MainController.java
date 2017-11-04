package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.ServerListItemConnectThread;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MainController {
	private boolean check_host_faster = false;
	private Pane pane_children = new Pane();
	private HashMap<Pane, ServerListItemConnectThread> serverlist_items = new HashMap<>();
	@FXML
	private Button button_add_list, button_connect;
	@FXML
	private TextField textfield_port, textfield_adress, textfield_server_name;
	@FXML
	private ScrollPane pane_serverlist; // ListViewがいいかもしれない。

	@FXML
	private void onButtonAddServer() {
		// check field
		String server_name, address, port_s;
		short port = 25565;
		server_name = textfield_server_name.getText();
		address = textfield_adress.getText();
		if (address.isEmpty()) address = "127.0.0.1";
		port_s = textfield_port.getText();
		if (port_s.isEmpty()) port_s = "25565";
		try {
			port = Short.parseShort(port_s);
		} catch (NumberFormatException ex) {
			Main.printDebug("Port field is not a number.");
			System.err.println("Port field is not a number.");
		}

		// add Pane of ServerList
		ServerListItemController slictr = null;
		FXMLLoader sli_fxml = new FXMLLoader(getClass().getResource("ServerListItem.fxml"));
		Pane pane = null;
		try {
			pane = sli_fxml.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		slictr = sli_fxml.getController();

		ServerListItemConnectThread sli = null;
		if (check_host_faster) {
			InetSocketAddress host = null;
			try {
				host = new InetSocketAddress(address, port);
			} catch (IllegalArgumentException ex) {
				Main.printDebug("Port parameter is outside the specifid range of valid port values.");
				System.err.println("Port parameter is outside the specifid range of valid port values.");
				return;
			}
			sli = new ServerListItemConnectThread(slictr, server_name, host);
		} else {
			sli = new ServerListItemConnectThread(slictr, server_name, address, port);
		}
		serverlist_items.put(pane, sli);
		// host→?
		pane.setLayoutY(pane_children.getChildren().size() * 120);
		pane_children.getChildren().add(pane);
		pane_serverlist.setContent(pane_children);
	}

	// private void onButtonUpItem() {
	// }

	// private void onButtonDownItem() {
	// }

	@FXML
	private void onButtonConnect() {
		for (Node pane : pane_children.getChildren()) {
			if (pane instanceof Pane) {
				ServerListItemConnectThread sli = serverlist_items.get((Pane) pane).isAlreadRun()
						? serverlist_items.get((Pane) pane).refresh() // alread run
						: serverlist_items.get((Pane) pane); // not once run
				Main.printDebug("connect?");
				sli.start();
			}
		}
	}
}