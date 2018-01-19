package de.zm4xi.uuiddatabase.bungee.file;

import java.io.File;

public class MySQLFile extends BungeeFile {

	public MySQLFile(File datafolder, String name) {
		super(datafolder, name);
		
		addDefault("host", "localhost");
		addDefault("port", "3306");
		addDefault("database", "databasename");
		addDefault("username", "user");
		addDefault("password", "veryRandomPassword");
		
	}

}
