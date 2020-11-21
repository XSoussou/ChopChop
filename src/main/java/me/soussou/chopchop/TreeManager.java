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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import me.soussou.chopchop.util.PlayerUtil;

public class TreeManager {
	
	private static final Map<Material, Material> MATERIALS_MAP = new HashMap<>();
	
	/*
	 * Vectors used to find tree blocks relative to other blocks.
	 */
	private static final Set<Vector> MAIN_RELATIVES = new HashSet<>();
	private static final Set<Vector> TRUNK_RELATIVES = new HashSet<>();
	private static final Set<Vector> BRANCHES_RELATIVES = new HashSet<>();
	private static final Set<Vector> LEAVES_RELATIVES = new HashSet<>();
	private static final Set<Vector> MUSHROOM_BRANCHES_RELATIVES = new HashSet<>();
	
	private Set<Block> treeBlocks = new LinkedHashSet<>();
	private Set<Material> leavesMaterials = new HashSet<>();
	
	private Block baseTreeBlock;
	private Material treeMaterial;
	
	private boolean isMushroom;
	private double maxLeafDistanceFromTreeSquared, maxBranchDistanceFromTreeSquared;
	private int trunkCount, branchCount, leafCount;
	
	static {
		List<Material> logs = new ArrayList<>(ChopChopConfig.LOGS_MATERIALS);
		List<Material> leaves = new ArrayList<>(ChopChopConfig.LEAVES_MATERIALS);
		
		for(int i = 0; i < logs.size(); i++) {
			MATERIALS_MAP.put(logs.get(i), leaves.get(i));
		}
		
		// 3x3x3 around block
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					MAIN_RELATIVES.add(new Vector(x, y, z));
				}
			}
		}
		
		// 3x3 around block, y is 0
		for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				TRUNK_RELATIVES.add(new Vector(x, 0, z));
			}
		}
		
		// 3x2x3, y starts at 0
		for(int x = -1; x <= 1; x++) {
			for(int y = 0; y <= 1; y++) {
				for(int z = -1; z <= 1; z++) {
					BRANCHES_RELATIVES.add(new Vector(x, y, z));
				}
			}
		}
		
		// 1 for every face of the block
		for(int i = -1; i <= 1; i += 2) {
			LEAVES_RELATIVES.add(new Vector(i, 0, 0));
			LEAVES_RELATIVES.add(new Vector(0, i, 0));
			LEAVES_RELATIVES.add(new Vector(0, 0, i));
		}
		
		// 3x3x3, y starts at 0
		for(int x = -1; x <= 1; x++) {
			for(int y = 0; y <= 2; y++) {
				for(int z = -1; z <= 1; z++) {
					MUSHROOM_BRANCHES_RELATIVES.add(new Vector(x, y, z));
				}
			}
		}
	}
	
	public TreeManager(Block firstTreeBlock) {
		this.baseTreeBlock = firstTreeBlock;
		this.treeMaterial = firstTreeBlock.getType();
		this.isMushroom = ChopChopConfig.MUSHROOMS_MATERIALS.containsKey(this.treeMaterial);
		
		if(this.isMushroom) this.leavesMaterials.addAll(ChopChopConfig.MUSHROOMS_MATERIALS.get(this.treeMaterial));
		else this.leavesMaterials.add(getLeavesMaterial(this.treeMaterial));
		
		double maxBranchDistance = ChopChopConfig.MAX_BRANCH_DISTANCE_FROM_TREE.get(this.treeMaterial);
		this.maxBranchDistanceFromTreeSquared = maxBranchDistance * maxBranchDistance;
		
		double maxLeafDistance = ChopChopConfig.MAX_LEAF_DISTANCE_FROM_TREE.get(this.treeMaterial);
		this.maxLeafDistanceFromTreeSquared = maxLeafDistance * maxLeafDistance;
	}
	
	public Set<Block> detectTree() {
		this.treeBlocks = getTreeTrunk();
		
		Set<Block> treeBranches = new LinkedHashSet<>();
		addAllTreeBranches(treeBranches, this.baseTreeBlock);
		
		/*
		 * Since the method is recursive, we have to reverse the order
		 * to put the upper branches later in the list.
		 */
		List<Block> branchesList = new ArrayList<>(treeBranches);
		Collections.reverse(branchesList);
		this.treeBlocks.addAll(branchesList);
		
		Set<Block> logsClone = new LinkedHashSet<>(this.treeBlocks);
		
		for(Block log : this.treeBlocks) {
			addAllTreeLeaves(logsClone, log);
		}
		
		if((logsClone.size() - this.treeBlocks.size() < ChopChopConfig.MIN_LEAF_COUNT) && 
			!this.isMushroom) 
				return null;
		
		if(ChopChopConfig.destroyLeaves || // Config: "destroy-leaves"
			this.isMushroom) 
				this.treeBlocks.addAll(logsClone);
		
		return this.treeBlocks;
	}
	
	private Set<Block> getTreeTrunk() {
		Set<Block> trunk = new LinkedHashSet<>();
		int oldSize, y = 0;
		
		do {
			oldSize = trunk.size();
			
			for(Vector vector : TRUNK_RELATIVES) {
				Block nextBlock = this.baseTreeBlock.getRelative(vector.getBlockX(), y, vector.getBlockZ());
				
				if(nextBlock.getType() == this.treeMaterial) {
					trunk.add(nextBlock);
					this.trunkCount++;
				}
			}
			y++;
			
		} while(trunk.size() != oldSize && this.trunkCount < ChopChopConfig.MAX_TRUNK_COUNT); // Max counts act as a safety
		
		return trunk;
	}
	
	private void addAllTreeBranches(Set<Block> baseBlockSet, Block baseBlock) {
		for(Vector vector : (this.isMushroom) ? MUSHROOM_BRANCHES_RELATIVES : BRANCHES_RELATIVES) {
			Block nextBlock = baseBlock.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			
			if(nextBlock.getType() == this.treeMaterial && !baseBlockSet.contains(nextBlock)) {
				baseBlockSet.add(nextBlock);
				this.branchCount++;
				if(isBranchInTreeRange(nextBlock) && this.branchCount < ChopChopConfig.MAX_BRANCH_COUNT) addAllTreeBranches(baseBlockSet, nextBlock);
			}
		}
	}
	
	private void addAllTreeLeaves(Set<Block> baseBlockSet, Block baseBlock) {
		for(Vector vector : (this.isMushroom) ? MAIN_RELATIVES : LEAVES_RELATIVES) {
			Block nextBlock = baseBlock.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			
			if(!baseBlockSet.contains(nextBlock) && this.leavesMaterials.contains(nextBlock.getType()) && isLeafPartOfTree(baseBlockSet, nextBlock)) {
				baseBlockSet.add(nextBlock);
				this.leafCount++;
				if(isLeafInTreeRange(nextBlock) && this.leafCount < ChopChopConfig.MAX_LEAF_COUNT) addAllTreeLeaves(baseBlockSet, nextBlock);
			}
		}
	}
	
	private boolean isLeafPartOfTree(Set<Block> baseBlockSet, Block leafBlock) {
		for(Vector vector : MAIN_RELATIVES) {
			Block nextBlock = leafBlock.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			
			if(!baseBlockSet.contains(nextBlock)) {
				if(this.isMushroom) {
					if(ChopChopConfig.MUSHROOMS_MATERIALS.containsKey(nextBlock.getType())) return false;
					
				} else if(this.leavesMaterials.contains(getLeavesMaterial(nextBlock.getType()))) return false;
			}
		}
		
		return true;
	}
	
	private boolean isBranchInTreeRange(Block branchBlock) {
		int distanceX = this.baseTreeBlock.getX() - branchBlock.getX();
		int distanceZ = this.baseTreeBlock.getZ() - branchBlock.getZ();
		double distanceSquared = (distanceX * distanceX) + (distanceZ * distanceZ);
		
		if(distanceSquared < this.maxBranchDistanceFromTreeSquared) return true;
		
		return false;
	}
	
	private boolean isLeafInTreeRange(Block leafBlock) {
		for(Block treeBlock : this.treeBlocks) {
			int distanceX = treeBlock.getX() - leafBlock.getX();
			int distanceZ = treeBlock.getZ() - leafBlock.getZ();
			double distanceSquared = (distanceX * distanceX) + (distanceZ * distanceZ);
			
			if(distanceSquared < this.maxLeafDistanceFromTreeSquared) return true;
		}
		
		return false;
	}
	
	public void cutTree(Player player, ItemStack item, Set<Block> treeBlocks) {
		boolean isCreative = player.getGameMode() == GameMode.CREATIVE;
		
		Material itemMat = item.getType();
		boolean isAxe = ChopChopConfig.AXES_MATERIALS.contains(itemMat);
		Damageable meta = (Damageable) item.getItemMeta();
		
		boolean isEnchanted = item.containsEnchantment(Enchantment.DURABILITY);
		int unbreakingLvl = item.getEnchantmentLevel(Enchantment.DURABILITY);
		
		for(Block block : treeBlocks) {
			
			if(baseTreeBlock.equals(block)) continue;
			
			if(this.leavesMaterials.contains(block.getType()) && !ChopChopConfig.destroyLeaves && !this.isMushroom) break; // Using break because we know all the following blocks are gonna be leaves aswell
			
			if(isCreative) { // Don't drop blocks if the player is in creative mode
				block.setType(Material.AIR);
				continue;
			}
			
			if((!isAxe || // In case onlyAxes is disabled
				this.leavesMaterials.contains(block.getType())) && // Leaves shouldn't damage the tool either
					!this.isMushroom) { // Mushroom "leaves" should damage the tool
					block.breakNaturally(item);
				
			} else if(meta.getDamage() < (itemMat.getMaxDurability() - 1)) {
				block.breakNaturally(item);
				
				if(ChopChopConfig.wearOff) { // Config: "tools-wear-off"
					if(isEnchanted && !PlayerUtil.unbreakingShouldDamage(unbreakingLvl)) continue;
					
					meta.setDamage(meta.getDamage() + 1);
				}
				
			} else if(isEnchanted) { // This case represents the last damage point of an enchanted item
				/*
				 * Since the axe could break in the middle of a tree chop, we have to simulate the Unbreaking behavior
				 * and manually break the item when needed.
				 */
				block.breakNaturally(item);
				
				if(PlayerUtil.unbreakingShouldDamage(unbreakingLvl)) {
					PlayerUtil.breakItemInHand(player);
					return;
				}
				
			} else break; // Let the unenchanted item break naturally
		}
		
		item.setItemMeta((ItemMeta) meta);
	}
	
	public static Material getLeavesMaterial(Material blockMat) {
		return MATERIALS_MAP.get(blockMat);
	}
}
