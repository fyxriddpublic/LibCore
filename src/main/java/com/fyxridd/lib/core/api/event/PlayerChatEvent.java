package com.fyxridd.lib.core.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * (同步)玩家聊天事件
 * 当玩家聊天时,此事件会发出
 * (与PlayerTipEvent事件对应,聊天栏一共有两种事件,一种是聊天事件,一种是提示事件)
 * 如果有其它聊天插件,则对应的插件应该修改聊天信息或取消聊天事件
 */
public class PlayerChatEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();

    private Player p;
    private String msg;
    private boolean cancelled;

    public PlayerChatEvent(Player p, String msg) {
        this.p = p;
        this.msg = msg;
    }

    public Player getP() {
        return p;
    }

    /**
     * 玩家发出的聊天信息
     */
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
