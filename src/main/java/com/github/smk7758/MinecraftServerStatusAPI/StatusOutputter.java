package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.IOException;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseForBungeeCord;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;
import com.google.gson.Gson;

public class StatusOutputter {
	private StatusOutputter() {
	}

	/**
	 * gives you the response from the server.
	 *
	 * @return the class from JSON.
	 * @throws IOException some connection error.
	 */
	public static ResponseInterface receiveResponse(StatusConnection status_connection) throws IOException {
		ResponseInterface response = convertResponse(status_connection.receiveResponseAsString());
		return response;
	}

	/**
	 * @param response_string
	 * @return
	 */
	public static ResponseInterface convertResponse(String response_string) {
		// todo: 判別式Do!
		ResponseInterface response = new Gson().fromJson(response_string, ResponseForBungeeCord.class);
		return response;
	}

	/*
	 * このクラスは、Json->Java 変換用のユーティリティクラスとする。
	 */
}