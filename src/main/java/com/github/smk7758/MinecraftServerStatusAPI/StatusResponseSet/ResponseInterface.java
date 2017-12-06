package com.github.smk7758.MinecraftServerStatusAPI.StatusResponseSet;

import java.util.List;

public interface ResponseInterface {
	/**
	 * @return Description json block.
	 */
	public Description getDescription();

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

	/**
	 * Set the ping time.
	 * @param time ping time.
	 */
	public void setTime(int time);

	public class Description {
		private String text;

		public Description() {
		}

		public Description(String text) {
			this.text = text;
		}

		/**
		 * @return description text(MOTD).
		 */
		public String getText() {
			return text;
		}
	}

	/**
	 * set of players.
	 */
	public class Players {
		private int max;
		private int online;
		private List<Player> players;

		public Players() {
		}

		public Players(int max, int online, List<Player> players) {
			this.max = max;
			this.online = online;
			this.players = players;
		}

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
	}

	/**
	 * player data.
	 */
	public class Player {
		private String name;
		private String id;

		public Player() {
		}

		public Player(String name, String id) {
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

	}

	/**
	 * versions.
	 */
	public class Version {
		private String name;
		private String protocol;

		public Version() {
		}

		public Version(String name, String protocol) {
			this.name = name;
			this.protocol = protocol;
		}

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
}