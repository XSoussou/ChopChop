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

import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChopChop extends JavaPlugin implements Listener {
	
	private static final int METRICS_ID = 9489;
	
	private static ChopChop instance;
	
	private Updater updater;
	private Metrics metrics;
	
	@Override
	public void onEnable() {
		instance = this;
		this.updater = new Updater(this);
		
		saveDefaultConfig();
		ChopChopConfig.load();
		
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getCommand("chopchop").setExecutor(new ChopChopCommand());
		
		if(ChopChopConfig.sendMetrics) metrics = new Metrics(this, METRICS_ID);
	}
	
	public Updater getUpdater() { return this.updater; }
	
	public static ChopChop getInstance() { return instance; }
}
