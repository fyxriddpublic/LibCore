package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.event.RealDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.EventExecutor;

/**
 * 真实伤害管理
 */
public class RealDamageManager {
	public RealDamageManager() {
		//注册事件
		Bukkit.getPluginManager().registerEvent(EntityDamageByEntityEvent.class, CorePlugin.instance, EventPriority.MONITOR, new EventExecutor() {
            @Override
            public void execute(Listener listener, Event e) throws EventException {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                //无伤害
                if (event.getEntity() instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    if (le.getNoDamageTicks() > le.getMaximumNoDamageTicks()/2 &&
                            event.getDamage() <= le.getLastDamage()) return;
                }
                //发出真实伤害事件
                Bukkit.getPluginManager().callEvent(new RealDamageEvent(event));
            }
        }, CorePlugin.instance, true);
	}
}
