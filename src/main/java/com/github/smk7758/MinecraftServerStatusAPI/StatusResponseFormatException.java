package com.github.smk7758.MinecraftServerStatusAPI;

public class StatusResponseFormatException extends RuntimeException {
	public StatusResponseFormatException() {
		super();
	}

	public StatusResponseFormatException(String response_string) {
		super(response_string);
	}
}
