package com.github.smk7758.MinecraftServerStatusChecker;

import com.github.smk7758.MinecraftServerStatusChecker.Networks.MinecraftServerStatus.ServerStatusResponse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String program_name = "MinecraftServerStatusChecker_0.0.4";
	public static final String fxml_url = "Screens/Main_2017-12-01.fxml";
	public static Stage primary_stage = null;
	private static boolean debug_mode = true; // for Debug.

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary_stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_url));
		Scene scene = new Scene(loader.load());
		// Set Title
		primary_stage.setTitle(program_name);
		// Set Window
		primary_stage.setResizable(false);
		// Set Scene
		primary_stage.setScene(scene);
		primary_stage.show();
		Main.primary_stage = primary_stage;
	}

	public static void printDebug(String text) {
		if (!debug_mode) return;
		System.out.println(text);
	}

	public static void printResponse(String server_name, ServerStatusResponse response) {
		System.out.println("ServerName: " + server_name);
		printResponse(response);
	}

	public static void printResponse(ServerStatusResponse response) {
		String is_favicon = "true";
		if (response.getFavicon() == null || response.getFavicon().isEmpty()) is_favicon = "false";
		String resposes = "Version: " + response.getVersion().getName() + "\n"
				+ "OnlinePlayers / MaximumPlayers: " + getPlayersText(response.getPlayers().getOnline(), response.getPlayers().getMax()) + "\n"
				+ "Ping: " + response.getTime() + "\n"
				+ "isFavicon(Icon): " + is_favicon + "\n"
				+ "Description(MOTD): " + response.getDescription().getText();
		System.out.println(resposes);
	}

	private static String getPlayersText(int text_online_players, int text_max_players) {
		String text_online_players_s = String.valueOf(text_online_players);
		String text_max_players_s = String.valueOf(text_max_players);
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