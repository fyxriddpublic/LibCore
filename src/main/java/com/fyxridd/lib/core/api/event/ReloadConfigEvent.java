package com.fyxridd.lib.core.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 请求重新读取配置文件
 */
public class ReloadConfigEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private String plugin;
    private boolean override;

	public ReloadConfigEvent(String plugin, boolean override) {
		this.plugin = plugin;
        this.override = override;
	}
	
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
	/**
	 * 需要重新读取配置文件的插件
	 */
	public String getPlugin() {
		return plugin;
	}

    public boolean isOverride() {
        return override;
    }
}