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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import me.soussou.chopchop.util.VersionUtil;

public class ChopChopConfig {
	
	public static final int MAX_TRUNK_COUNT = 150;
	public static final int MAX_BRANCH_COUNT = 170;
	public static final int MAX_LEAF_COUNT = 500;
	public static final int MIN_LEAF_COUNT = 4;
	
	public static final Map<Material, Double> MAX_BRANCH_DISTANCE_FROM_TREE = new HashMap<>();
	public static final Map<Material, Double> MAX_LEAF_DISTANCE_FROM_TREE = new HashMap<>();
	
	public static final Set<Material> LOGS_MATERIALS = VersionUtil.getCompatibleLogsMaterials();
	public static final Set<Material> LEAVES_MATERIALS = VersionUtil.getCompatibleLeavesMaterials();
	public static final Set<Material> AXES_MATERIALS = VersionUtil.getCompatibleAxesMaterials();
	public static final Map<Material, Set<Material>> MUSHROOMS_MATERIALS = VersionUtil.getCompatibleMushroomsMaterials();
	
	// Contains the max distances per tree type (ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE, MUSHROOM, CRIMSON, WARPED)
	private static final double[] BRANCH_MAX_DISTANCES = {4, 1, 3, 5, 7, 1, 1, 3, 3};
	private static final double[] LEAF_MAX_DISTANCES = {3, 3, 3, 5, 3, 3, 3, 5, 5};
	
	public static String activationMode = "sneak";
	public static boolean enabled = true;
	public static boolean wearOff = true;
	public static boolean onlyAxes = true;
	public static boolean allowInCreative = true;
	public static boolean destroyLeaves = true;
	public static boolean enableMushrooms = true;
	
	private static ChopChop plugin = ChopChop.getInstance();
	
	static {
		List<Material> logs = new ArrayList<>(LOGS_MATERIALS);
		List<Material> mushrooms = new ArrayList<>(MUSHROOMS_MATERIALS.keySet());
		
		/*
		 * Initializing the max distance maps
		 */
		for(int i = 0; i < logs.size(); i++) {
			MAX_BRANCH_DISTANCE_FROM_TREE.put(logs.get(i), BRANCH_MAX_DISTANCES[i]);
			MAX_LEAF_DISTANCE_FROM_TREE.put(logs.get(i), LEAF_MAX_DISTANCES[i]);
		}
		
		for(int i = 0; i < mushrooms.size(); i++) {
			MAX_BRANCH_DISTANCE_FROM_TREE.put(mushrooms.get(i), BRANCH_MAX_DISTANCES[i + logs.size()]);
			MAX_LEAF_DISTANCE_FROM_TREE.put(mushrooms.get(i), LEAF_MAX_DISTANCES[i + logs.size()]);
		}
	}
	
	public static void load() {
		activationMode = plugin.getConfig().getString("activation-mode");
		wearOff = plugin.getConfig().getBoolean("tools-wear-off");
		onlyAxes = plugin.getConfig().getBoolean("only-axes");
		allowInCreative = plugin.getConfig().getBoolean("allow-in-creative");
		destroyLeaves = plugin.getConfig().getBoolean("destroy-leaves");
		enableMushrooms = plugin.getConfig().getBoolean("enable-mushrooms");
	}
	
	public static void reload() {
		plugin.reloadConfig();
		load();
		plugin.log("Config reloaded");
	}
}
