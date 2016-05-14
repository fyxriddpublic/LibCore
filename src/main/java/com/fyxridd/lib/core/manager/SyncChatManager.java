package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.PlayerChatEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步聊天管理
 */
public class SyncChatManager implements Listener {
    //常量

    private static final Lock chatLock = new ReentrantLock();

    //缓存

    private static List<PlayerChatEvent> chatEvents = new ArrayList<>();

    public SyncChatManager() {
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
                                FancyMessage msg = get(35, event.getP().getName(), event.getMsg());
                                for (Player tar: Bukkit.getOnlinePlayers()) addChat(tar, msg, false);
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

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        try {
            if (!chatLock.tryLock(3, TimeUnit.SECONDS)) throw new InterruptedException();

            try {
                chatEvents.add(new PlayerChatEvent(e.getPlayer(), e.getMessage()));
            } finally {
                chatLock.unlock();
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
