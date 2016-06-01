package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.EnterBlockTypeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;

public class EnterBlockTypeManager {
    private class Check implements Runnable {
        @Override
        public void run() {
            for (Player p:Bukkit.getOnlinePlayers()) check(p, false);

            //下个计时
            Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.instance, check, CorePlugin.instance.getCoreConfig().getEnterBlockTypeInterval());
        }
    }

    //缓存
    private HashMap<Player, Material> inTypeHash = new HashMap<>();//进入方块类型
    private HashMap<Player, Material> onTypeHash = new HashMap<>();//走上方块类型

    private Check check = new Check();

    public EnterBlockTypeManager() {
        //注册事件
        Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, CorePlugin.instance, EventPriority.LOW, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                if (e instanceof PlayerQuitEvent) {
                    PlayerQuitEvent event = (PlayerQuitEvent) e;
                    //检测
                    check(event.getPlayer(), true);
                    //从缓存中删除
                    inTypeHash.remove(event.getPlayer());
                    onTypeHash.remove(event.getPlayer());
                }
            }
        }, CorePlugin.instance);
        //计时器
        Bukkit.getScheduler().scheduleSyncDelayedTask(CorePlugin.instance, check, CorePlugin.instance.getCoreConfig().getEnterBlockTypeInterval());
    }

    /**
     * 检测
     * @param p 玩家
     * @param exit 玩家是否退服
     */
    private void check(Player p, boolean exit) {
        //in
        {
            Material oldType = inTypeHash.get(p);
            Material newType = exit?null:p.getLocation().getBlock().getType();
            boolean change = false;
            if (newType != null) change = oldType == null || !oldType.equals(newType);
            else if (oldType != null) change = true;
            if (change) {
                inTypeHash.put(p, newType);
                Bukkit.getPluginManager().callEvent(new EnterBlockTypeEvent(p, oldType, newType, true));
            }
        }

        //on
        {
            Material oldType = onTypeHash.get(p);
            Material newType = exit?null:p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
            boolean change = false;
            if (newType != null) change = oldType == null || !oldType.equals(newType);
            else if (oldType != null) change = true;
            if (change) {
                onTypeHash.put(p, newType);
                Bukkit.getPluginManager().callEvent(new EnterBlockTypeEvent(p, oldType, newType, false));
            }
        }
    }
}
