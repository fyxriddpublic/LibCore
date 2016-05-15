package com.fyxridd.lib.core.api.event;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 玩家接收聊天事件
 * 当玩家需要接收聊天信息时,会发出此事件
 * 其它插件可以监听并获取聊天内容自行进行处理,然后取消事件
 */
public class PlayerChatReceiveEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player p;
    private FancyMessage msg;

    public PlayerChatReceiveEvent(Player p, FancyMessage msg) {
        this.p = p;
        this.msg = msg;
    }

    /**
     * @return 接收聊天信息的玩家(不一定是发出聊天信息的玩家)
     */
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
