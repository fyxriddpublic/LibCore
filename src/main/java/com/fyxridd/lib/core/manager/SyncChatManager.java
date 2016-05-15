package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.event.PlayerChatReceiveEvent;
import com.fyxridd.lib.core.api.event.PlayerChatEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.lang.api.LangApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步聊天管理
 */
public class SyncChatManager{
    private final Lock chatLock = new ReentrantLock();

    private List<PlayerChatEvent> chatEvents = new ArrayList<>();

    public SyncChatManager() {
        //监听异步聊天事件
        Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, CorePlugin.instance, EventPriority.HIGHEST, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
                //取消事件
                event.setCancelled(true);
                try {
                    if (!chatLock.tryLock(3, TimeUnit.SECONDS)) throw new InterruptedException();

                    try {
                        chatEvents.add(new PlayerChatEvent(event.getPlayer(), event.getMessage()));
                    } finally {
                        chatLock.unlock();
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }, CorePlugin.instance, true);
        //同步检测发出聊天事件
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.instance, new Runnable() {
            public void run() {
                if (chatLock.tryLock()) {
                    try{
                        //发出事件
                        for (PlayerChatEvent event:chatEvents) {
                            if (!event.getP().isOnline()) continue;

                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled()) {
                                LangApi.iteratePlayer(Bukkit.getOnlinePlayers(), new LangApi.PlayerHandler() {
                                    @Override
                                    public void handle(Player p, FancyMessage msg) {
                                        //发出接收聊天事件
                                        PlayerChatReceiveEvent e = new PlayerChatReceiveEvent(p, msg);
                                        Bukkit.getPluginManager().callEvent(e);
                                        //默认处理: 给玩家显示聊天栏信息
                                        if (!e.isCancelled()) MessageApi.send(p, msg, false);
                                    }
                                }, 35, event.getP().getName(), event.getMsg());
                            }
                        }

                        //清空事件列表
                        chatEvents.clear();
                    }finally {
                        chatLock.unlock();
                    }
                }
            }
        }, 1, 1);
    }
}
