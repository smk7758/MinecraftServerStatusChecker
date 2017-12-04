package com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse;

/**
 * the class from JSON for ServerStatusResponse.(for 1.9.x)
 */
public class ServerStatusResponseJson implements ServerStatusResponseInterface<Description> {
	public boolean old_mode = false;
	private Description description;
	private Players players;
	private Version version;
	private String favicon;
	private int time;

	/**
	 * @return description.
	 */
	public Description getDescription() {
		return description;
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
