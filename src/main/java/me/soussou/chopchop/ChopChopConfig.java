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
import org.bukkit.configuration.file.FileConfiguration;

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
	
	/*
	 *  Contains the max distances per tree type (ACACIA, BIRCH, DARK_OAK, JUNGLE, OAK, SPRUCE, MUSHROOM, CRIMSON, WARPED)
	 */
	private static final double[] BRANCH_MAX_DISTANCES = {4, 1, 3, 5, 7, 1, 1, 3, 3};
	private static final double[] LEAF_MAX_DISTANCES = {3, 3, 3, 5, 3, 3, 3, 5, 5};
	
	public static String activationMode = "sneak";
	public static boolean enabled = true,
		wearOff = true,
		onlyAxes = true,
		onlyBreakUpwards = true,
		allowInCreative = true,
		destroyLeaves = true,
		silkTouchDamage = true,
		enableMushrooms = true,
		sendMetrics = true,
		enableUpdater = true;
	public static int configVersion = 1;
	
	private static ChopChop plugin;
	
	static {
		List<Material> logs = new ArrayList<>(LOGS_MATERIALS);
		List<Material> mushrooms = new ArrayList<>(MUSHROOMS_MATERIALS.keySet());
		
		/*
		 * Initializing max distance maps
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
	
	public static void init(ChopChop instance) {
		plugin = instance;
		load();
	}
	
	public static void load() {
		FileConfiguration config = plugin.getConfig();
		
		activationMode = config.getString("activation-mode");
		wearOff = config.getBoolean("tools-wear-off");
		onlyAxes = config.getBoolean("only-axes");
		onlyBreakUpwards = config.getBoolean("only-break-upwards");
		allowInCreative = config.getBoolean("allow-in-creative");
		destroyLeaves = config.getBoolean("destroy-leaves");
		silkTouchDamage = config.getBoolean("silk-touch-damage");
		enableMushrooms = config.getBoolean("enable-mushrooms");
		sendMetrics = config.getBoolean("send-metrics");
		enableUpdater = config.getBoolean("enable-updater");
		configVersion = config.getInt("config-version");
	}
	
	public static void reload() {
		plugin.reloadConfig();
		load();
		plugin.getLogger().info("Config reloaded");
	}
}
