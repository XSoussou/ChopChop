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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Updater {
	
	private static final String POM_URL = "https://raw.githubusercontent.com/XSoussou/ChopChop/master/pom.xml";
	private static final String GITHUB_URL = "https://github.com/XSoussou/ChopChop/releases";
	
	private JavaPlugin plugin;
	private BukkitTask timer;
	
	public Updater(JavaPlugin plugin) {
		this.plugin = plugin;
		
		init();
	}
	
	public void init() {
		if(ChopChopConfig.enableUpdater) {
			if(this.timer != null) return; // If already running, don't create the task timer
			
			this.timer = new BukkitRunnable() {
				
				@Override
				public void run() {
					if(plugin.isEnabled() && ChopChopConfig.enableUpdater) {
						try {
							checkUpdates();
						} catch (IOException | SAXException | ParserConfigurationException e) {
							plugin.getLogger().warning("Failed to check for update");
						}
						
					} else this.cancel();
				}
			}.runTaskTimerAsynchronously(this.plugin, 60, 20 * 60 * 60 * 6); // Runs every 6 hours
			
		} else if(this.timer != null) {
			// Correctly remove the timer so it can be reinitialized later if the config changes
			this.timer.cancel();
			this.timer = null;
		}
	}
	
	private void checkUpdates() throws IOException, SAXException, ParserConfigurationException {
		URL url = new URL(POM_URL);
		
		String currentVersion = plugin.getDescription().getVersion();
		String newVersion = getXmlVersion(url);
		
		if(isNewerVersion(currentVersion, newVersion)) {
			plugin.getLogger().info("New update v" + newVersion + " - Download from GitHub: " + GITHUB_URL);
		}
	}
	
	private String getXmlVersion(URL url) throws IOException, SAXException, ParserConfigurationException {
		String version = "";
		
		InputStream in = url.openStream();
		Document pom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
		NodeList elements = pom.getFirstChild().getChildNodes(); // Every element of the main <project> node
		
		for(int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);
			
			if(node.getNodeName().equals("version")) {
				version = node.getTextContent();
				break;
			}
		}
		
		in.close();
		
		return version;
	}
	
	private int[] getVersionNumbers(String version) {
		String[] split = version.split("\\-")[0].split("\\.");
		int[] numbers = new int[split.length];
		
		for(int i = 0; i < split.length; i++) {
			numbers[i] = Integer.parseInt(split[i]);
		}
		
		return numbers;
	}
	
	private boolean isNewerVersion(String ver1, String ver2) {
		int[] ver1Nums = getVersionNumbers(ver1);
		int[] ver2Nums = getVersionNumbers(ver2);
		
		for(int i = 0; i < Math.max(ver1Nums.length, ver2Nums.length); i++) {
			int left = (i < ver1Nums.length) ? ver1Nums[i] : 0;
			int right = (i < ver2Nums.length) ? ver2Nums[i] : 0;
			
			if(left != right) return left < right;
		}
		
		return false;
	}
}
