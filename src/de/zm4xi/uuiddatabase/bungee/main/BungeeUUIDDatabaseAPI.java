package de.zm4xi.uuiddatabase.bungee.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import de.zm4xi.uuiddatabase.database.BungeeMySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeUUIDDatabaseAPI extends Plugin implements Listener {

	private static BungeeMySQL mySQL;
	public static BungeeMySQL getMySQL() {
		return mySQL;
	}
	
	@Override
	public void onEnable() {
		try {
			mySQL = new BungeeMySQL(getDataFolder());
			setup();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BungeeCord.getInstance().getPluginManager().registerListener(this, this);
	}
	
	@EventHandler
	public void onJoin(ServerConnectEvent e) {
		try {
			registerPlayer(e.getPlayer());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}
	
	/*
	 * uuid | name | lastIP |
	 * 2131232 | zm4xi | 127.0.0.1
	 * */
	
	public static boolean isPlayerRegistered(UUID uuid) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT * FROM uuid_db WHERE uuid = ?");
		try {
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} finally {
			ps.close();
		}
	}
	
	public static boolean isPlayerRegistered(String userName) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT * FROM uuid_db WHERE username = ?");
		try {
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} finally {
			ps.close();
		}
	}
	
	private static void registerPlayer(ProxiedPlayer player) throws SQLException {
		UUID uuid = player.getUniqueId();
		if(!isPlayerRegistered(uuid)) {
			PreparedStatement ps = getMySQL().getConnection().prepareStatement("INSERT INTO uuid_db (uuid, username, lastIP) VALUES (?, ?, ?)");
			try {
				ps.setString(1, uuid.toString());
				ps.setString(2, player.getName());
				ps.setString(3, player.getAddress().getAddress().getHostAddress());
				ps.executeUpdate();
			} finally {
				ps.close();
			}
		} else {
			updatePlayer(player);
		}
	}
	
	public static UUID getUUIDByName(String name) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT uuid FROM uuid_db WHERE username = ?");
		try {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} finally {
			ps.close();
		}
		return null;
	}
	
	public static String getNameByIP(String IP) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT username FROM uuid_db WHERE lastIP = ?");
		try {
			ps.setString(1, IP);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("username");
			}
		} finally {
			ps.close();
		}
		return null;
	}
	
	public static String getNameByUUID(UUID uuid) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT username FROM uuid_db WHERE uuid = ?");
		try {
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString("username");
			}
		} finally {
			ps.close();
		}
		return null;
	}
	
	public static UUID getUUIDByIP(String IP) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("SELECT uuid FROM uuid_db WHERE lastIP = ?");
		try {
			ps.setString(1, IP);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} finally {
			ps.close();
		}
		return null;
	}
	
	private static void updatePlayer(ProxiedPlayer player) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("UPDATE uuid_db SET username = ?, lastIP = ? WHERE uuid = ?");
		try {
			ps.setString(1, player.getName());
			ps.setString(2, player.getAddress().getAddress().getHostAddress());
			ps.setString(3, player.getUniqueId().toString());
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
	private void setup() throws SQLException {
		PreparedStatement ps = getMySQL().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS uuid_db (uuid VARCHAR(99), username VARCHAR(99), lastIP VARCHAR(99))");
		try {
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}
	
}
