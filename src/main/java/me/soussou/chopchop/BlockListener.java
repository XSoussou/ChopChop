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

import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!ChopChopConfig.enabled) return;
		
		Player player = event.getPlayer();
		
		if(player.hasPermission("chopchop.deny") && !player.isOp()) return;
		
		if(player.getGameMode() == GameMode.CREATIVE && 
			!ChopChopConfig.allowInCreative) // Config: "allow-in-creative"
				return;
		
		if(((!player.isSneaking() && ChopChopConfig.activationMode.equalsIgnoreCase("sneak")) || 
			(player.isSneaking() && ChopChopConfig.activationMode.equalsIgnoreCase("unsneak"))) && 
			!ChopChopConfig.activationMode.equalsIgnoreCase("any")) // Config: "activation-mode"
				return;
		
		Block block = event.getBlock();
		
		if(!ChopChopConfig.LOGS_MATERIALS.contains(block.getType()) && 
			!(ChopChopConfig.enableMushrooms && ChopChopConfig.MUSHROOMS_MATERIALS.containsKey(block.getType()))) // Config: "enable-mushrooms"
				return;
		
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if(!ChopChopConfig.AXES_MATERIALS.contains(item.getType()) && 
			ChopChopConfig.onlyAxes) // Config: "only-axes"
				return;
		
		TreeManager tree = new TreeManager(block);
		Set<Block> treeBlocks = tree.detectTree();
		
		if(!treeBlocks.isEmpty()) tree.cutTree(player, item, treeBlocks);
	}
}
