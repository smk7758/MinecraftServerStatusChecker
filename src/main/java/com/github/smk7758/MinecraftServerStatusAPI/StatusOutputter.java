package com.github.smk7758.MinecraftServerStatusAPI;

import java.io.IOException;

import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseForBungeeCord;
import com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet.ResponseInterface;
import com.google.gson.Gson;

public class StatusOutputter {
	private StatusConnection mssc = null;
	private Gson gson = new Gson();

	public StatusOutputter(StatusConnection mssc) {
		this.mssc = mssc;
	}

	/**
	 * gives you the response from the server.
	 *
	 * @return the class from JSON.
	 * @throws IOException some connection error.
	 */
	public ResponseInterface receiveServerStatus() throws IOException {
		ResponseInterface response = convertServerStatusResponse(mssc.receiveServerStatusResponseAsString());
		return response;
	}

	/**
	 *
	 * @param response_string
	 * @return
	 */
	public ResponseInterface convertServerStatusResponse(String response_string) {
		ResponseInterface response = gson.fromJson(response_string, ResponseForBungeeCord.class);
		return response;
	}
}