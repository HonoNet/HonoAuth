package net.simplyrin.hononet.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

/**
 * Created by SimplyRin on 2018/02/01.
 */
public class Main extends JavaPlugin implements Listener {

	/**
	 * このプラグインは 2017/07/18 に作成されたプラグインを元に再構成されたものです。
	 */
	private static Main plugin;

	@Override
	public void onEnable() {
		plugin = this;
		if(!plugin.getDescription().getAuthors().contains("SimplyRin")) {
			plugin.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		plugin.saveDefaultConfig();
		plugin.getCommand("code").setExecutor(this);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(!sender.hasPermission("honoauth.use")) {
			sender.sendMessage(this.getPrefix() + "§cYou do not have access to this command");
			return true;
		}

		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("reload")) {
				if(!sender.hasPermission("honoauth.reload")) {
					sender.sendMessage(this.getPrefix() + "§cYou do not have access to this command");
					return true;
				}
				plugin.reloadConfig();
				sender.sendMessage(this.getPrefix() + this.getMessage("Messages.Reload"));
				return true;
			}


			if(!(sender instanceof Player)) {
				sender.sendMessage(this.getPrefix() + "§cこのコマンドはゲーム内からのみ使用できます。");
				return true;
			}

			Player player = (Player) sender;

			if(!plugin.getConfig().isSet("CodeList." + args[0])) {
				sender.sendMessage(this.getPrefix() + this.getMessage("Messages.None"));
				return true;
			}

			if(plugin.getConfig().getBoolean("Players." + player.getUniqueId().toString() + "." + args[0])) {
				sender.sendMessage(this.getPrefix() + this.getMessage("Messages.Already"));
				return true;
			}

			sender.sendMessage(this.getPrefix() + this.getMessage("Messages.Done"));

			if(plugin.getConfig().isSet("CodeList." + args[0] + ".Messages")) {
				for(String msg : plugin.getConfig().getStringList("CodeList." + args[0] + ".Messages")) {
					sender.sendMessage(this.getPrefix() + ChatColor.translateAlternateColorCodes('&', msg));
				}
			}

			if(plugin.getConfig().isSet("CodeList." + args[0] + ".Commands")) {
				for(String msg : plugin.getConfig().getStringList("CodeList." + args[0] + ".Commands")) {
					msg = msg.replace("%player", player.getName());
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), msg);
				}
			}

			plugin.getConfig().set("Players." + player.getUniqueId().toString() + "." + args[0], true);
			plugin.saveConfig();
			plugin.reloadConfig();
		}
		return true;
	}

	public String getPrefix() {
		return this.getMessage("Prefix") + " ";
	}

	public String getMessage(String path) {
		return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
	}
}
