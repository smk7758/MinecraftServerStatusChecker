package com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse;

public interface ServerStatusResponseInterface<T> {
	/**
	 * @param <T>
	 * @return description.
	 */
	public T getDescription();

	/**
	 * @return Players json block.
	 */
	public Players getPlayers();

	/**
	 * @return Version json block.
	 */
	public Version getVersion();

	/**
	 * @return Favicon string.
	 */
	public String getFavicon();

	/**
	 * @return how long did the ping take.
	 */
	public int getTime();

	public void setTime(int time);
}
