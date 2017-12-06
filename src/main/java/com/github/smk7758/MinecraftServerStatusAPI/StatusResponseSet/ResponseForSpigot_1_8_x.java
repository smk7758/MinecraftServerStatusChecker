package com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet;

public class ResponseForSpigot_1_8_x implements ResponseInterface {
	private String description;
	private Players players;
	private Version version;
	private String favicon;
	private int time;

	/**
	 * @return description.
	 */
	public Description getDescription() {
		return new Description(description);
	}

	/**
	 * @return Players json block.
	 */
	public Players getPlayers() {
		return players;
	}

	/**
	 * @return Version json block.
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * @return Favicon string.
	 */
	public String getFavicon() {
		return favicon;
	}

	/**
	 * @return how long did the ping take.
	 */
	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
