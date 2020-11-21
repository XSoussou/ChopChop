/*
 * ChopChop - A plugin to cut trees instantly
 * Copyright © 2020 Soussou
 * 
 * This file is part of ChopChop.
 * 
 * ChopChop is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ChopChop is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ChopChop. If not, see <https://www.gnu.org/licenses/>.
 */

package me.soussou.chopchop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ChopChopCommand implements CommandExecutor, TabCompleter {
	
	private static final String[] CMD_ARGS = {"on", "off", "reload"};
	
	private ChopChop plugin = ChopChop.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(ChatColor.GOLD + "[ChopChop] " + ChatColor.YELLOW + "The plugin is currently " + 
			((ChopChopConfig.enabled) ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled"));
			return true;
			
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("on")) {
				ChopChopConfig.enabled = true;
				plugin.log("Plugin enabled");
				sender.sendMessage(ChatColor.GOLD + "[ChopChop] " + ChatColor.YELLOW + "Plugin " + ChatColor.GREEN + "enabled");
				
			} else if(args[0].equalsIgnoreCase("off")) {
				ChopChopConfig.enabled = false;
				plugin.log("Plugin disabled");
				sender.sendMessage(ChatColor.GOLD + "[ChopChop] " + ChatColor.YELLOW + "Plugin " + ChatColor.RED + "disabled");
				
			} else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
				ChopChopConfig.reload();
				sender.sendMessage(ChatColor.GOLD + "[ChopChop] " + ChatColor.YELLOW + "Config reloaded");
				
			} else return false;
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		
		if(args.length == 1) {
			for(String arg : CMD_ARGS) {
				if(arg.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(arg);
				}
			}
		}
		
		return result;
	}
}
