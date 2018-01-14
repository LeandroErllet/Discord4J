package br.com.craftlife.discord4j.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Leandro oliveira
 *
 */
public class CoreBukkitPlugin extends JavaPlugin {
	
	/**
	 * @param Eventos para quando o plugin estiver carregando
	 */
	@Override
	public void onLoad() {
	}
	
	/**
	 * @param Eventos para quando o plugin estiver Iniciando
	 */
	@Override
	public void onEnable() {
		Bukkit.getServer().getConsoleSender().sendMessage("§8[§7Discord4J§8] §aAPI ativada com sucesso!");
	}
	
	/**
	 * @param Eventos para quando o plugin estiver sendo desativado
	 */
	@Override
	public void onDisable() {
		Bukkit.getServer().getConsoleSender().sendMessage("§8[§7Discord4J§8] §aAPI desativada com sucesso!");
	}
	
}
