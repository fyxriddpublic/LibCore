package com.fyxridd.lib.core.api.event;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 读取FancyMessage事件
 * (可以监听此事件并修改读取的信息,如添加额外内容)
 */
public class LoadFancyMessageEvent extends Event{
	private static final HandlerList handlers = new HandlerList();

    //相当于show-xxx内容
	private String msg;
    //相当于info-xxx内容
	private ConfigurationSection cs;

    //此结果可以被修改
    private FancyMessage result;

    public LoadFancyMessageEvent(String msg, ConfigurationSection cs, FancyMessage result) {
        this.msg = msg;
        this.cs = cs;
        this.result = result;
    }

    @Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}

    public String getMsg() {
        return msg;
    }

    public ConfigurationSection getCs() {
        return cs;
    }

    public FancyMessage getResult() {
        return result;
    }

    public void setResult(FancyMessage result) {
        this.result = result;
    }
}
