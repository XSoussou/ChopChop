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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;

public class VersionUtil {
	
	public static final String SERVER_VERSION = Bukkit.getBukkitVersion();
	
	public static final Set<String> POST_1_13_VERSIONS = new HashSet<>(Arrays.asList("1.13", "1.14", "1.15"));
	public static final Set<String> POST_1_16_VERSIONS = new HashSet<>(Arrays.asList("1.16"));
	
	private static final Set<String> POST_1_13_LOGS_MATERIALS = new LinkedHashSet<>(Arrays.asList("ACACIA_LOG", "BIRCH_LOG", "DARK_OAK_LOG", "JUNGLE_LOG", "OAK_LOG", "SPRUCE_LOG"));
	
	private static final Set<String> POST_1_13_LEAVES_MATERIALS = new LinkedHashSet<>(Arrays.asList("ACACIA_LEAVES", "BIRCH_LEAVES", "DARK_OAK_LEAVES", "JUNGLE_LEAVES", "OAK_LEAVES", "SPRUCE_LEAVES"));
	
	private static final Set<String> PRE_1_16_AXES_MATERIALS = new HashSet<>(Arrays.asList("WOODEN_AXE", "STONE_AXE", "IRON_AXE", "GOLDEN_AXE", "DIAMOND_AXE"));
	private static final Set<String> POST_1_16_AXES_MATERIALS = new HashSet<>(Arrays.asList("WOODEN_AXE", "STONE_AXE", "IRON_AXE", "GOLDEN_AXE", "DIAMOND_AXE", "NETHERITE_AXE"));
	
	private static final Map<String, Set<String>> PRE_1_16_MUSHROOMS_MATERIALS = new HashMap<>();
	private static final Map<String, Set<String>> POST_1_16_MUSHROOMS_MATERIALS = new HashMap<>();
	
	static {
		PRE_1_16_MUSHROOMS_MATERIALS.put("MUSHROOM_STEM", new HashSet<>(Arrays.asList("RED_MUSHROOM_BLOCK", "BROWN_MUSHROOM_BLOCK")));
		
		POST_1_16_MUSHROOMS_MATERIALS.put("MUSHROOM_STEM", new HashSet<>(Arrays.asList("RED_MUSHROOM_BLOCK", "BROWN_MUSHROOM_BLOCK")));
		POST_1_16_MUSHROOMS_MATERIALS.put("CRIMSON_STEM", new HashSet<>(Arrays.asList("NETHER_WART_BLOCK", "SHROOMLIGHT")));
		POST_1_16_MUSHROOMS_MATERIALS.put("WARPED_STEM", new HashSet<>(Arrays.asList("WARPED_WART_BLOCK", "SHROOMLIGHT")));
	}
	
	public static Set<Material> getCompatibleLogsMaterials() {
		Set<Material> materials = new LinkedHashSet<>();
		
		POST_1_13_LOGS_MATERIALS.forEach(mat -> materials.add(Material.getMaterial(mat)));
		
		return materials;
	}
	
	public static Set<Material> getCompatibleLeavesMaterials() {
		Set<Material> materials = new LinkedHashSet<>();
		
		POST_1_13_LEAVES_MATERIALS.forEach(mat -> materials.add(Material.getMaterial(mat)));
		
		return materials;
	}
	
	public static Set<Material> getCompatibleAxesMaterials() {
		Set<Material> materials = new LinkedHashSet<>();
		
		if(isVersion(POST_1_13_VERSIONS)) {
			PRE_1_16_AXES_MATERIALS.forEach(mat -> materials.add(Material.getMaterial(mat)));
			return materials;
		}
		
		POST_1_16_AXES_MATERIALS.forEach(mat -> materials.add(Material.getMaterial(mat)));
		
		return materials;
	}
	
	public static Map<Material, Set<Material>> getCompatibleMushroomsMaterials() {
		Map<Material, Set<Material>> materials = new HashMap<>();
		
		materials.putAll(createMaterialsMap(PRE_1_16_MUSHROOMS_MATERIALS));
		
		if(isVersion(POST_1_16_VERSIONS)) {
			materials.putAll(createMaterialsMap(POST_1_16_MUSHROOMS_MATERIALS));
		}
		
		return materials;
	}
	
	public static boolean isVersion(Set<String> versionSet) {
		for(String version : versionSet) {
			if(SERVER_VERSION.startsWith(version)) return true;
		}
		
		return false;
	}
	
	private static Map<Material, Set<Material>> createMaterialsMap(Map<String, Set<String>> map) {
		Map<Material, Set<Material>> result = new HashMap<>();
		
		map.keySet().forEach(logMat -> {
			Set<Material> leafMats = new HashSet<>();
			
			map.get(logMat).forEach(mat -> leafMats.add(Material.getMaterial(mat)));
			
			result.put(Material.getMaterial(logMat), leafMats);
		});
		
		return result;
	}
}
