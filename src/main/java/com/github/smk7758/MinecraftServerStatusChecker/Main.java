package com.github.smk7758.MinecraftServerStatusChecker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse.ServerStatusResponseInterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String program_name = "MinecraftServerStatusChecker_0.0.6.a";
	public static final String fxml_url = "Screens/Main.fxml";
	public static Stage primary_stage = null;
	public static boolean debug_mode = true; // for Debug.

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

	public static void printResponse(String server_name,
			@SuppressWarnings("rawtypes") ServerStatusResponseInterface response) {
		System.out.println("ServerName: " + server_name);
		printResponse(response);
	}

	public static void printResponse(@SuppressWarnings("rawtypes") ServerStatusResponseInterface response) {
		String is_favicon = "true";
		if (response.getFavicon() == null || response.getFavicon().isEmpty()) is_favicon = "false";
		String resposes = "Version: " + response.getVersion().getName() + System.lineSeparator()
				+ "OnlinePlayers / MaximumPlayers: "
				+ getPlayersText(response.getPlayers().getOnline(), response.getPlayers().getMax())
				+ System.lineSeparator()
				+ "Ping: " + response.getTime() + System.lineSeparator()
				+ "isFavicon(Icon): " + is_favicon + System.lineSeparator()
				//todo:
//				+ "Description(MOTD): " + ((ServerStatusResponseJson) response).getDescription().getText()
				;
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

	public static void outputToLogFile(String response, String address, short port) throws IOException {
		String log_file;
		if (System.getProperty("user.name").equals("smk7758")) log_file = "F:\\users\\smk7758\\Desktop\\log_client.txt"; // ユーザー名がsmk7758の時
		else log_file = System.getProperty("user.home") + "\\Desktop\\log_client.txt";
		Path log_file_path = Paths.get(log_file);
		BufferedWriter bw = Files.newBufferedWriter(log_file_path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		bw.write("Time: " + LocalDateTime.now().toString() + System.lineSeparator()
				+ "ServerAddress: " + address + System.lineSeparator()
				+ "ServerPort: " + port + System.lineSeparator());
		bw.write(response);
		bw.flush();
	}

	// todo: BungeeCordや1.9以前に対応する。(→ver1.0?)
}