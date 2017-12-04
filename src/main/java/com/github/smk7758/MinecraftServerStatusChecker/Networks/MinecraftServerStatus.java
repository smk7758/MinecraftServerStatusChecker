package com.github.smk7758.MinecraftServerStatusChecker.Networks;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import com.github.smk7758.MinecraftServerStatusChecker.Main;
import com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse.Description;
import com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse.ServerStatusResponseInterface;
import com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse.ServerStatusResponseJson;
import com.github.smk7758.MinecraftServerStatusChecker.ServerStatusResponse.ServerStatusResponseJsonForOld;
import com.google.gson.Gson;

public class MinecraftServerStatus implements AutoCloseable {
	private InetSocketAddress host = null;
	private int protocol_version = -1;
	private int timeout = 7000;
	private Socket socket = null;
	private InputStream is = null;
	private OutputStream os = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private Gson gson = new Gson();

	/**
	 * @param host the adress and the port of the server you want to access.
	 * @throws IOException some conection error.
	 */
	public MinecraftServerStatus(InetSocketAddress host) throws IOException {
		initialize(host, this.timeout, this.protocol_version);
	}

	/**
	 * @param host the adress and the port of the server you want to access.
	 * @param timeout input block millisec.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some conection error.
	 */
	public MinecraftServerStatus(InetSocketAddress host, int timeout, int protocol_version) throws IOException {
		this.timeout = timeout;
		this.protocol_version = protocol_version;
		initialize(host, timeout, protocol_version);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @throws IOException some conection error.
	 * @deprecated Can't get exception properly.
	 */
	public MinecraftServerStatus(String address, short port) throws IOException {
		initialize(address, port, this.timeout, this.protocol_version);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @param timeout input block millisec.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some conection error.
	 * @deprecated Can't get exception properly.
	 */
	public MinecraftServerStatus(String address, short port, int timeout, int protocol_version) throws IOException {
		initialize(address, port, timeout, protocol_version);
	}

	/**
	 * @param host the adress and the port of the server you want to access.
	 * @param timeout input block millisec.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some conection error.
	 */
	private void initialize(InetSocketAddress host, int timeout, int protocol_version) throws IOException {
		this.host = host;
		socket = new Socket();
		socket.setSoTimeout(timeout); // Input block millisec.
		socket.connect(host, timeout); // connect to host(wait untill timeout when no connect);
		is = socket.getInputStream();
		os = socket.getOutputStream();
		dis = new DataInputStream(is);
		dos = new DataOutputStream(os);
	}

	/**
	 * @param address the address of the server you want to access.
	 * @param port the port of the server you want to access.
	 * @param timeout input block millisec.
	 * @param protocol_version each version of Minecraft has the different one.
	 * @throws IOException some conection error.
	 * @deprecated Can't get exception properly.
	 */
	private void initialize(String address, short port, int timeout, int protocol_version) throws IOException {
		host = new InetSocketAddress(address, port);
		initialize(host, timeout, protocol_version);
	}

	/**
	 * if you want to stop the conection, use this. also you have to use when you want to stop the program.
	 *
	 * @throws IOException some conection error.
	 */
	@Override
	public void close() throws IOException {
		dis.close();
		dos.close();
		is.close();
		os.close();
		socket.close();
	}

	// public void flush() {
	//
	// }

	/**
	 * send a packet for Handshake.
	 *
	 * @throws IOException some conection error.
	 */
	public void sendHandshakePacket() throws IOException {
		// Send Handshake
		byte[] handshake_data = getHandshakePacketData(1);
		int handshake_length = handshake_data.length;
		writeVarInt(handshake_length);
		dos.write(handshake_data);
	}

	/**
	 * @return a packet data of byte for Handshake.
	 * @param state - 1 or 2, 1(send a request for status), 2(send a request for login).
	 * @throws IOException some conection error
	 */
	private byte[] getHandshakePacketData(int state) throws IOException {
		try (ByteArrayOutputStream output_data = new ByteArrayOutputStream();
			DataOutputStream output_dos = new DataOutputStream(output_data);) {// just a unit
			output_dos.writeByte(0x00); // PacketID: Handshake
			writeVarInt(protocol_version, output_dos); // Protocol Version
			writeString(host.getHostString(), output_dos, Charset.defaultCharset()); // Host Name String
			output_dos.writeShort(host.getPort()); // Host Port used to connect
			writeVarInt(state, output_dos); // for check status, state
			output_dos.flush();
			return output_data.toByteArray();
		}
	}

	/**
	 * send a packet to get ServerListRespond.
	 *
	 * @throws IOException some conection error
	 */
	public void sendServerStatusPacket() throws IOException {
		// Send Request
		dos.writeByte(0x01);// Packet Size.
		dos.writeByte(0x00);// PacketID: ServerStatusPacket{Request(ServerListPespond)}
	}

	public void sendPingPacket() throws IOException {
		sendPingPacket(System.currentTimeMillis());
	}

	/**
	 * send a packet for Ping.
	 *
	 * @param client_time the time you are.
	 * @throws IOException some conection error.
	 */
	public void sendPingPacket(long client_time) throws IOException {
		// Send Ping
		dos.writeByte(0x09); // ping packet size
		dos.writeByte(0x01); // PacketID: Ping
		dos.writeLong(client_time); // ping packet data
	}

	/**
	 * recieve a packet of Ping.
	 *
	 * @return how long did the ping take.
	 * @throws IOException some conection error.
	 */
	public long recievePing() throws IOException {
		return recievePing(System.currentTimeMillis());
	}

	/**
	 * recieve a packet of Ping.
	 *
	 * @param client_time the time you are.
	 * @return how long did the ping take.
	 * @throws IOException some conection error.
	 */
	public long recievePing(long client_time) throws IOException {
		// Recieve Ping

		// Under this is likely not used.
		int ping_size = readVarInt();
		if (ping_size != 9) throw new IOException("Invalid size.");
		int id = readVarInt();
		if (id == -1) throw new IOException("End of stream.");
		if (id != 0x01) throw new IOException("Invalid PacketID.");
		long server_time = dis.readLong();
		long ping_time = client_time - server_time;
		return ping_time;
	}

	/**
	 * gives you the response from the server.
	 *
	 * @return the class from JSON.
	 * @throws IOException some conection error.
	 */
	public ServerStatusResponseInterface recieveServerStatus() throws IOException {
		ServerStatusResponseInterface response = getServerStatusResponse(recieveServerStatusAsString());
		return response;
	}

	public String recieveServerStatusAsString() throws IOException {
		// Recieve Response
		int respond_size = readVarInt();
		if (respond_size == 0) throw new IOException("Invalid size. It's too shrot.");

		int packet_id = readVarInt();
		if (packet_id == -1) throw new IOException("End of stream.");
		if (packet_id != 0x00) throw new IOException("Invalid PacketID.");

		int response_string_length = readVarInt();
		if (response_string_length == -1) throw new IOException("End of stream.");
		if (response_string_length == 0) throw new IOException("Invalid length. It's too short.");
		byte[] response_byte = new byte[response_string_length];
		dis.readFully(response_byte);
		String response_string = new String(response_byte);
		return response_string;
	}

	@SuppressWarnings("rawtypes")
	public ServerStatusResponseInterface getServerStatusResponse(String response_string) {
		// todo: 総称型の不確定さよ。
		if (Main.debug_mode) {
			ServerStatusResponseInterface<Description> response = gson.fromJson(response_string,
					ServerStatusResponseJson.class);
			return response;
		} else {
			ServerStatusResponseInterface<String> response = gson.fromJson(response_string,
					ServerStatusResponseJsonForOld.class);
			return response;
		}
	}

	private int readVarInt() throws IOException {
		int time_receive = (int) System.currentTimeMillis();
		int numRead = 0;
		int result = 0;
		byte read;
		do {
			read = dis.readByte();
			int value = (read & 0b01111111);
			result |= (value << (7 * numRead));
			numRead++;
			if (numRead > 5) {
				throw new RuntimeException("VarInt is too big");
			}
		} while ((read & 0b10000000) != 0);

		return result;
	}

	private void writeString(String string, DataOutputStream dos, Charset charset) throws IOException {
		byte[] bytes = string.getBytes(charset);
		writeVarInt(bytes.length, dos);
		dos.write(bytes);
	}

	private void writeVarInt(int value) throws IOException {
		writeVarInt(value, this.dos);
	}

	private void writeVarInt(int value, DataOutputStream dos) throws IOException {
		do {
			byte temp = (byte) (value & 0b01111111);
			value >>>= 7;
			if (value != 0) {
				temp |= 0b10000000;
			}
			writeByte(temp, dos);
		} while (value != 0);
	}

	private void writeByte(byte data, DataOutputStream dos) throws IOException {
		dos.writeByte(data);
	}

	// /**
	// * the class from JSON for ServerStatusResponse.
	// */
	// public class ServerStatusResponse {
	// private Description description;
	// private Players players;
	// private Version version;
	// private String favicon;
	// private int time;
	//
	// /**
	// * @return description.
	// */
	// public Description getDescription() {
	// return description;
	// }
	//
	// /**
	// * @return Players json block.
	// */
	// public Players getPlayers() {
	// return players;
	// }
	//
	// /**
	// * @return Version json block.
	// */
	// public Version getVersion() {
	// return version;
	// }
	//
	// /**
	// * @return Favicon string.
	// */
	// public String getFavicon() {
	// return favicon;
	// }
	//
	// /**
	// * @return how long did the ping take.
	// */
	// public int getTime() {
	// return time;
	// }
	//
	// public void setTime(int time) {
	// this.time = time;
	// }
	// }
	//
	// public class Description {
	// private String text;
	//
	// /**
	// * @return description text(MOTD).
	// */
	// public String getText() {
	// return text;
	// }
	// }
	//
	// /**
	// * set of players.
	// */
	// public class Players {
	// private int max;
	// private int online;
	// private List<Player> players;
	//
	// /**
	// * @return maximum of how many player can connect.
	// */
	// public int getMax() {
	// return max;
	// }
	//
	// /**
	// * @return number of how many players are in the server.
	// */
	// public int getOnline() {
	// return online;
	// }
	//
	// public List<Player> getSample() {
	// return players;
	// }
	// }
	//
	// /**
	// * player data.
	// */
	// public class Player {
	// private String name;
	// private String id;
	//
	// public String getName() {
	// return name;
	// }
	//
	// public String getId() {
	// return id;
	// }
	//
	// }
	//
	// /**
	// * versions.
	// */
	// public class Version {
	// private String name;
	// private String protocol;
	//
	// /**
	// * @return server version.
	// */
	// public String getName() {
	// return name;
	// }
	//
	// /**
	// * @return protocol version.
	// */
	// public String getProtocol() {
	// return protocol;
	// }
	// }
}
