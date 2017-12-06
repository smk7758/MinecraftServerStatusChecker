package com.github.smk7758.MinecraftServerStatusChecker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseVanila;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String program_name = "MinecraftServerStatusChecker_0.0.6";
	public static final String fxml_url = "Screens/Main.fxml";
	public static Stage primary_stage = null;
	public static boolean debug_mode = false; // for Debug.

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
		if (debug_mode) System.out.println(text);
	}

	public static void printResponse(String server_name, ResponseVanila response) {
		System.out.println("ServerName: " + server_name);
		// printResponse(response);
	}

	// todo: throwsを修正。
	public static void outputResponseToLogFile(String response_string, String server_name, String address, short port) {
		String log_file;
		if (System.getProperty("user.name").equals("smk7758")) log_file = "F:\\users\\smk7758\\Desktop\\log_client.txt";
		else log_file = System.getProperty("user.home") + "\\Desktop\\MSSC_log_file_" + server_name + ".txt";
		// log_file = "C:\\Users\\kariyassh\\Desktop\\MSSC_log_file_" + server_name + ".txt";
		Path log_file_path = Paths.get(log_file);
		try (BufferedWriter bw = Files.newBufferedWriter(log_file_path, StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.WRITE);) {
			bw.write("Time: " + LocalDateTime.now().toString() + System.lineSeparator() + "ServerAddress: " + address
					+ System.lineSeparator() + "ServerPort: " + port + System.lineSeparator());
			bw.write(response_string);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.printDebug("OutputLogPath: " + log_file);
	}
}