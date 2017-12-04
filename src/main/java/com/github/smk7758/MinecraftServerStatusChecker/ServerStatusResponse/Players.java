package com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse;

import java.util.List;

public class Players {
	/**
	 * set of players.
	 */
	private int max;
	private int online;
	private List<Player> players;

	/**
	 * @return maximum of how many player can connect.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @return number of how many players are in the server.
	 */
	public int getOnline() {
		return online;
	}

	public List<Player> getSample() {
		return players;
	}

	/**
	 * player data.
	 */
	public class Player {
		private String name;
		private String id;

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}
	}
}
