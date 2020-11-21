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

package me.soussou.chopchop.util;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtil {
	
	private static Random random = new Random();
	
	public static void breakItemInHand(Player player) {
		Location loc = player.getLocation();
		PlayerInventory inv = player.getInventory();
		
		player.spawnParticle(Particle.ITEM_CRACK, loc.add(0, 1, 0), 5, 0, 0, 0, 0.05, inv.getItemInMainHand());
		player.playSound(loc, Sound.ENTITY_ITEM_BREAK, 0.25F, random.nextFloat() / 2 + 0.75F);
		inv.setItemInMainHand(null);
	}
	
	public static boolean unbreakingShouldDamage(int unbreakingLvl) {
		double damageChance = 100.0 / (unbreakingLvl + 1);
		
		return (random.nextDouble() * 100) <= damageChance;
	}
}
