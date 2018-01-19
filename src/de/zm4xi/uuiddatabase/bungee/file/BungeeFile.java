package de.zm4xi.uuiddatabase.bungee.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeFile {

	private File file;
	private Configuration cfg;
	
	public BungeeFile(File datafolder, String name) {
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}
		file = new File(datafolder.getPath(), name + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Configuration getConfig() {
		return cfg;
	}
	
	public File getFile() {
		return file;
	}
	
	public void reload() {
		try {
			cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addDefault(String path, Object value) {
		if(cfg.get(path) == null || cfg.get(path) == "") {
			cfg.set(path, value);
		}
		save();
	}
	
	public boolean getBoolean(String path) {
		return cfg.getBoolean(path);
	}
	
	public void set(String path, Object value) {
		cfg.set(path, value);
		save();
	}
	
	public String getString(String path) {
		return cfg.getString(path);
	}
	
	public int getInt(String path) {
		return cfg.getInt(path);
	}
	
	public double getDouble(String path) {
		return cfg.getDouble(path);
	}
	
	public List<String> getStringList(String path) {
		return cfg.getStringList(path);
	}
	
	public void save() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
