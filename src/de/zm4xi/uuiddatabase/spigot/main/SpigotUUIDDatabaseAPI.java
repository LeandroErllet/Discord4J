package de.zm4xi.uuiddatabase.spigot.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.zm4xi.uuiddatabase.database.SpigotMySQL;

public class SpigotUUIDDatabaseAPI extends JavaPlugin implements Listener {

	private static SpigotMySQL mySQL;

	public static SpigotMySQL getMySQL() {
		return mySQL;
	}

	@Override
	public void onEnable() {
		try {
			mySQL = new SpigotMySQL(getDataFolder());
			setup();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent e) {
		try {
			registerPlayer(e.getPlayer());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * uuid | name | lastIP | 2131232 | zm4xi | 127.0.0.1
	 */

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

	private void registerPlayer(Player player) throws SQLException {
		UUID uuid = player.getUniqueId();
		if (!isPlayerRegistered(player.getName())) {
			PreparedStatement ps = getMySQL().getConnection()
					.prepareStatement("INSERT INTO uuid_db (uuid, username, lastIP) VALUES (?, ?, ?)");
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
		PreparedStatement ps = getMySQL().getConnection()
				.prepareStatement("SELECT uuid FROM uuid_db WHERE username = ?");
		try {
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} finally {
			ps.close();
		}
		return null;
	}

	public static String getNameByIP(String IP) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection()
				.prepareStatement("SELECT username FROM uuid_db WHERE lastIP = ?");
		try {
			ps.setString(1, IP);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("username");
			}
		} finally {
			ps.close();
		}
		return null;
	}

	public static String getNameByUUID(UUID uuid) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection()
				.prepareStatement("SELECT username FROM uuid_db WHERE uuid = ?");
		try {
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
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
			if (rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} finally {
			ps.close();
		}
		return null;
	}

	private static void updatePlayer(Player player) throws SQLException {
		PreparedStatement ps = getMySQL().getConnection()
				.prepareStatement("UPDATE uuid_db SET username = ?, lastIP = ? WHERE uuid = ?");
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
		PreparedStatement ps = getMySQL().getConnection().prepareStatement(
				"CREATE TABLE IF NOT EXISTS uuid_db (uuid VARCHAR(99), username VARCHAR(99), lastIP VARCHAR(99))");
		try {
			ps.executeUpdate();
		} finally {
			ps.close();
		}
	}

}
