package com.fyxridd.lib.core.api.event;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 玩家提示事件
 * 给玩家发送聊天栏提示时,会发出此事件
 * (与PlayerChatEvent事件对应,聊天栏一共有两种事件,一种是聊天事件,一种是提示事件)
 * 任何时候需要给玩家显示聊天栏信息时,都会发出此事件
 */
public class PlayerTipEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();

    private Player p;
    private FancyMessage msg;
    private boolean force;
    private boolean cancelled;

    public PlayerTipEvent(Player p, FancyMessage msg, boolean force) {
        this.p = p;
        this.msg = msg;
        this.force = force;
    }

    public Player getP() {
        return p;
    }

    /**
     * 提示信息
     */
    public FancyMessage getMsg() {
        return msg;
    }

    public boolean isForce() {
        return force;
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
