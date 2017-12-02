package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.ServerListItemConnectThread;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MainController {
	private boolean check_host_faster = false;
	// private Pane pane_children = new Pane();
	private ObservableList<Pane> items = FXCollections.observableArrayList();
	private HashMap<Pane, ServerListItemConnectThread> serverlist_items = new HashMap<>();
	@FXML
	private Button button_add_list, button_connect, button_up, button_down, button_remove, button_save;
	@FXML
	private TextField textfield_port, textfield_adress, textfield_server_name;
	@FXML
	private ListView<Pane> listview_serverlist;
	// private ScrollPane pane_serverlist; // ListViewがいいかもしれない。

	public void initialize() {
		listview_serverlist.setFixedCellSize(122); // ListViewのitemの間のなんかこれ(cell)の大きさを変えられる。
		listview_serverlist.autosize();
	}

	@FXML
	private void onButtonAddServer() {
		// initialize.
		String server_name, address, port_s;
		short port = 25565;

		// get fields and substitution them.
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

		// get sli screen items. (add Pane of ServerList)
		FXMLLoader sli_fxml = getFXMLLoader();
		Pane pane = getPane(sli_fxml);
		ServerListItemController slictr = getServerListItemController(sli_fxml);
		ServerListItemConnectThread sli = getServerListItemConnectThread(address, port, slictr, server_name);

		serverlist_items.put(pane, sli);
		// pane.setLayoutY(items.size() * 122);
		items.add(pane);
		listview_serverlist.setItems(items);

		// refresh Main screen items.
		textfield_server_name.setText("");
		textfield_adress.setText("");
		textfield_port.setText("");
	}

	private FXMLLoader getFXMLLoader() {
		FXMLLoader sli_fxml = new FXMLLoader(getClass().getResource("ServerListItem.fxml"));
		return sli_fxml;
	}

	private Pane getPane(FXMLLoader sli_fxml) {
		Pane pane = null;
		try {
			pane = sli_fxml.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pane;
	}

	// ラムダ式ですかね。
	private ServerListItemController getServerListItemController(FXMLLoader sli_fxml) {
		ServerListItemController slictr = sli_fxml.getController();
		return slictr;
	}

	private ServerListItemConnectThread getServerListItemConnectThread(String address, short port,
			ServerListItemController slictr, String server_name) {
		ServerListItemConnectThread sli = null;
		if (check_host_faster) {
			InetSocketAddress host = null;
			try {
				host = new InetSocketAddress(address, port);
			} catch (IllegalArgumentException ex) {
				Main.printDebug("Port parameter is outside the specifid range of valid port values.");
				System.err.println("Port parameter is outside the specifid range of valid port values.");
				return null;
			}
			sli = new ServerListItemConnectThread(slictr, server_name, host);
		} else {
			sli = new ServerListItemConnectThread(slictr, server_name, address, port);
		}
		return sli;
	}

	private ServerListItemConnectThread getSelectServerListItem() {
		Pane select_item_pane = items.get(getSelectServerListItemIndex()); // Pane取得
		ServerListItemConnectThread sli = serverlist_items.get(select_item_pane); // Thread取得
		return sli;
	}

	private int getSelectServerListItemIndex() {
		int select_item_index = listview_serverlist.getSelectionModel().getSelectedIndex();
		return select_item_index;
	}

	@FXML
	private void onButtonRemoveItem() {
		int select_item_index = getSelectServerListItemIndex();
		items.remove(select_item_index);
	}

	@FXML
	private void onButtonRemoveAllItem() {
	}

	@FXML
	private void onButtonUpItem() {
		changeItemIndex(-1);
	}

	@FXML
	private void onButtonDownItem() {
		changeItemIndex(1);
	}

	// If you want to up, write - 1, to down, write (+) 1.
	private void changeItemIndex(int number) {
		int select_item_index = getSelectServerListItemIndex();
		if (number == 0) return;
		else if (number < 0 && select_item_index == 0) return; // 最上部の時。
		else if (number > 0 && select_item_index == items.size()) return; // 最下部の時。
		Pane select_item_temp = items.get(select_item_index);
		items.remove(select_item_index);
		items.add(select_item_index + number, select_item_temp);
		System.out.println("up");
	}

	// ファイルをどうするかとかいろいろ。
	@FXML
	private void onButtonSaveItem() {
	}

//	@FXML
//	private void onButtonEdit() {
//	}

	@FXML
	private void onButtonConnect() {
		for (Node pane : items) {
			if (pane instanceof Pane) {
				ServerListItemConnectThread sli = serverlist_items.get((Pane) pane).isAlreadRun()
						? serverlist_items.get((Pane) pane).refresh() // alread run
						: serverlist_items.get((Pane) pane); // not once run
				Main.printDebug("connect?");
				sli.start();
			}
		}
	}

	@FXML
	private void onButtonClearInfo() {
		//save temp
		ServerListItemConnectThread sli_temp = getSelectServerListItem();
		int item_index_temp = getSelectServerListItemIndex();

		//remove
		serverlist_items.remove(items.get(item_index_temp));
		items.remove(item_index_temp);

		// add
		FXMLLoader sli_fxml = getFXMLLoader();
		Pane pane = getPane(sli_fxml);
		ServerListItemController slictr = getServerListItemController(sli_fxml);
		ServerListItemConnectThread sli = getServerListItemConnectThread(sli_temp.getAdress(), sli_temp.getPort(),
				slictr, sli_temp.getServerName());

		serverlist_items.put(pane, sli);
		items.add(item_index_temp, pane);
		listview_serverlist.setItems(items);
		// ていうか必要？
		//バグ: remove で１個上のやつを選択する。
	}

//	private void onButtonSettingWindowOpen() {
//	}
}