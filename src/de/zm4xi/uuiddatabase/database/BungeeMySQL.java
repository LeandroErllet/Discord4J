package de.zm4xi.uuiddatabase.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import de.zm4xi.uuiddatabase.bungee.file.MySQLFile;

public class BungeeMySQL {
	
	private MySQLFile file;
	private String host, port, database, username, password;
	
	private Connection connection;
	
	public BungeeMySQL(File datafolder) throws SQLException {
		file = new MySQLFile(datafolder, "database");
		
		this.host = file.getString("host");
		this.port = file.getString("port");
		this.database = file.getString("database");
		this.username = file.getString("username");
		this.password = file.getString("password");
		
		connection = openConnection();
	}
	
	private Connection openConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void closeConnection() throws SQLException {
		if(!connection.isClosed()) {
			connection.close();
		}
	}
	
	/**
	 * 
	 * @param messageTag = TEXT_FACTIONS
	 * @param languageTag = DE
	 * @param value = Dieser Text ist auf deutsch
	 */
	
	public void setMessage(String messageTag, String languageTag, String value) throws SQLException {
		if (isMessageExists(messageTag, languageTag)) {
			PreparedStatement ps = connection.prepareStatement("UPDATE language_message SET message = ? WHERE UPPER(languageTag) = ? AND UPPER(messageTag) = ?");
			try {
				ps.setString(1, value);
				ps.setString(2, languageTag.toUpperCase());
				ps.setString(3, messageTag.toUpperCase());
				ps.executeUpdate();
			} finally {
				ps.close();
			}
		}
		else {
			PreparedStatement ps = connection.prepareStatement("INSERT INTO language_message (languageTag, messageTag, message) VALUES (?, ?, ?)");
			try {
				ps.setString(1, languageTag.toUpperCase());
				ps.setString(2, messageTag.toUpperCase());
				ps.setString(3, value);
				ps.executeUpdate();
			} finally {
				ps.close();
			}
		}
	}
	
	public String getMessage(String messageTag, String language) throws SQLException {
		PreparedStatement ps = getConnection().prepareStatement("SELECT message FROM language_message WHERE languageTag = ? AND messageTag = ?");
		try {
			ps.setString(1, language);
			ps.setString(2, messageTag);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getString("message");
		} finally {
			ps.close();
		}
		return "";
	}
	
	public String getLanguage(UUID uuid) throws SQLException {
		PreparedStatement ps = getConnection().prepareStatement("SELECT languageTag FROM language_user WHERE UUID = ?");
		try {
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getString("languageTag");
		} finally {
			ps.close();
		}
		return "";
	}
	
	public boolean isMessageExists(String messageTag, String languageTag) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM language_message WHERE UPPER(messageTag) = ? AND UPPER(languageTag) = ?");
		try {
			ps.setString(1, messageTag.toUpperCase());
			ps.setString(2, languageTag.toUpperCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
			else {
				return false;
			}
		} finally {
			ps.close();
		}
	}
}
