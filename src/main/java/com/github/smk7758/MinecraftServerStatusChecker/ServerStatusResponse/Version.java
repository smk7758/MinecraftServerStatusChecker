package com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse;

public class Version {
	/**
	 * versions.
	 */
	private String name;
	private String protocol;

	/**
	 * @return server version.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return protocol version.
	 */
	public String getProtocol() {
		return protocol;
	}
}
