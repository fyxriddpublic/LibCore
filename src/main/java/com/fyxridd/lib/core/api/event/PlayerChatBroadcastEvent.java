package com.fyxridd.lib.core.api.event;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 玩家聊天广播事件
 * 其它插件可以监听并获取聊天内容自行进行处理,然后取消事件
 */
public class PlayerChatBroadcastEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player p;
    private FancyMessage msg;

    public PlayerChatBroadcastEvent(Player p, FancyMessage msg) {
        this.p = p;
        this.msg = msg;
    }

    public Player getP() {
        return p;
    }

    public FancyMessage getMsg() {
        return msg;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
