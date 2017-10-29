package com.github.smk7758.MinecraftServerStatusChecker;

import com.github.smk7758.MinecraftServerStatusChecker.MCServerStatus.ServerStatusResponse;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static String program_name = "MinecraftServerStatusChecker_0.0.2";
	public static String fxml_url = "MinecraftServerStatusChecker_0.0.2.fxml";
	public static Stage primary_stage = null;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary_stage) throws Exception {
		Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxml_url)));
		// Set Title
		primary_stage.setTitle(program_name);
		// Set Window
		primary_stage.setResizable(false);
		// Set Scene
		primary_stage.setScene(scene);
		primary_stage.show();
		Main.primary_stage = primary_stage;
	}

	public static void printResponse(ServerStatusResponse response) {
		String is_favicon = "true";
		if (response.getFavicon().isEmpty()) is_favicon = "false";
		String resposes = "Version: " + response.getVersion().getName() + "\n"
				+ "OnlinePlayers / MaximumPlayers: " + response.getPlayers().getOnline() + " / " + response.getPlayers().getMax() + "\n"
				+ "Ping: " + response.getTime() + "\n"
				+ "isFavicon(Icon):" + is_favicon + "\n"
				+ "Description(MOTD): " + response.getDescription().getText();
//		+ "Favicon(Icon): " + response.getFavicon() + "\n"
		System.out.println(resposes);
	}
}
