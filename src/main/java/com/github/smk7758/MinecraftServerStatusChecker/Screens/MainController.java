package com.github.smk7758.MinecraftServerStatusChecker.Screens;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.Networks.ServerConnectThread;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class MainController {
	private boolean check_host_faster = false;
	private ObservableList<Pane> items = FXCollections.observableArrayList();
	private HashMap<Pane, ServerConnectThread> serverlist_items = new HashMap<>();
	@FXML
	private Button button_add_list, button_connect, button_up, button_down, button_remove, button_save;
	@FXML
	private CheckBox checkbox_debug_mode;
	@FXML
	private TextField textfield_port, textfield_adress, textfield_server_name;
	@FXML
	private ListView<Pane> listview_serverlist;

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
		}

		// get SLI screen items. (add Pane of ServerList)
		FXMLLoader sli_fxml = getServerListItemFXMLLoader();
		Pane pane = getServerListItemPane(sli_fxml);
		ServerListItemController slictr = getServerListItemController(sli_fxml);
		ServerConnectThread sct = getServerListItemConnectThread(address, port, slictr, server_name);

		serverlist_items.put(pane, sct);
		// pane.setLayoutY(items.size() * 122);
		items.add(pane);
		listview_serverlist.setItems(items);

		// PrintDebugThings
		Main.printDebug("ServerName: " + server_name + System.lineSeparator()
				+ "ServerAddress: " + address + ", ServerPort: " + port + System.lineSeparator()
				+ "ItemsCount: " + items.size());

		// refresh Main screen items.
		textfield_server_name.setText("");
		textfield_adress.setText("");
		textfield_port.setText("");
	}

	private FXMLLoader getServerListItemFXMLLoader() {
		FXMLLoader sli_fxml = new FXMLLoader(getClass().getResource("ServerListItem.fxml"));
		return sli_fxml;
	}

	private Pane getServerListItemPane(FXMLLoader sli_fxml) {
		Pane pane = null;
		try {
			pane = sli_fxml.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pane;
	}

	private ServerListItemController getServerListItemController(FXMLLoader sli_fxml) {
		ServerListItemController slictr = sli_fxml.getController();
		return slictr;
	}

	private ServerConnectThread getServerListItemConnectThread(String address, short port,
			ServerListItemController slictr, String server_name) {
		ServerConnectThread sct = null;
		if (check_host_faster) {
			InetSocketAddress host = null;
			try {
				host = new InetSocketAddress(address, port);
			} catch (IllegalArgumentException ex) {
				Main.printDebug("Port parameter is outside the specifid range of valid port values.");
				return null;
			}
			sct = new ServerConnectThread(slictr, server_name, host);
		} else {
			sct = new ServerConnectThread(slictr, server_name, address, port);
		}
		return sct;
	}

	private ServerConnectThread getSelectServerListItem() {
		Pane select_item_pane = items.get(getSelectServerListItemIndex()); // Pane取得
		ServerConnectThread sct = serverlist_items.get(select_item_pane); // Thread取得
		return sct;
	}

	private int getSelectServerListItemIndex() {
		int select_item_index = listview_serverlist.getSelectionModel().getSelectedIndex();
		return select_item_index;
	}

	@FXML
	private void onButtonRemoveItem() {
		int select_item_index = getSelectServerListItemIndex();
		if (select_item_index != -1) items.remove(select_item_index);
	}

	@FXML
	private void onButtonRemoveAllItem() {
	}

	@FXML
	private void onButtonUpItem() {
		changeItemIndex(1);
	}

	@FXML
	private void onButtonDownItem() {
		changeItemIndex(-1);
	}

	/**
	 * If you want to up, write - 1, to down, write (+) 1.
	 *
	 * @param numbers_goto + will go up, - will go down.
	 */
	private void changeItemIndex(int numbers_goto) {
		int select_item_index = getSelectServerListItemIndex();
		if (numbers_goto == 0) return;
		else if (numbers_goto > 0 && select_item_index == 0) return; // 最上部の時。
		else if (numbers_goto < 0 && select_item_index == items.size()) return; // 最下部の時。
		Pane select_item_temp = items.get(select_item_index);
		items.remove(select_item_index);
		items.add(select_item_index - numbers_goto, select_item_temp);
		System.out.println("up");
	}

	// ファイルをどうするかとかいろいろ。
	@FXML
	private void onButtonSaveItem() {
	}

	// @FXML
	// private void onButtonEdit() {
	// }

	@FXML
	private void onButtonConnect() {
		Main.debug_mode = checkbox_debug_mode.isSelected() ? true : false;
		if (checkbox_debug_mode.isSelected()) Main.debug_mode = true;
		else Main.debug_mode = false;
		Main.printDebug("checkbox_debug_mode: " + checkbox_debug_mode.isSelected()
				+ ", MainDebugMode: " + Main.debug_mode + "");
		for (Node pane : items) {
			if (pane instanceof Pane) {
				ServerConnectThread sct = serverlist_items.get(pane).isAlreadRun()
						? serverlist_items.get(pane).refresh() // Already running.
						: serverlist_items.get(pane); // Isn't running.
				Main.printDebug("Starting to connect...");
				sct.start();
			}
		}
	}

	@FXML
	private void onButtonClearInfo() {
		// save temp
		ServerConnectThread sli_temp = getSelectServerListItem();
		int item_index_temp = getSelectServerListItemIndex();

		// remove
		serverlist_items.remove(items.get(item_index_temp));
		items.remove(item_index_temp);

		// add
		FXMLLoader sli_fxml = getServerListItemFXMLLoader();
		Pane pane = getServerListItemPane(sli_fxml);
		ServerListItemController slictr = getServerListItemController(sli_fxml);
		ServerConnectThread sct = getServerListItemConnectThread(sli_temp.getAdress(), sli_temp.getPort(),
				slictr, sli_temp.getServerName());

		serverlist_items.put(pane, sct);
		items.add(item_index_temp, pane);
		listview_serverlist.setItems(items);
		// ていうか必要？
		// バグ: remove で１個上のやつを選択する。
	}

	// private void onButtonSettingWindowOpen() {
	// }
}