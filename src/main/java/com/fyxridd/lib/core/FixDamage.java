package com.fyxridd.lib.core;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.fyxridd.lib.core.api.CorePlugin;

public class FixDamage implements Listener{
    private HashMap<Integer, Integer> fixDamage;
    
    public FixDamage() {
        //读取配置文件
        loadConfig();
        //注册事件
        Bukkit.getPluginManager().registerEvents(this, CorePlugin.instance);
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onReloadConfig(ReloadConfigEvent e) {
        if (e.getPlugin().equals(CorePlugin.pn)) loadConfig();
    }

    private void loadConfig() {
        YamlConfiguration config = ConfigApi.getConfig(CorePlugin.pn);
        
        //fixDamage
        fixDamage = new HashMap<>();
        for (String s:config.getStringList("fixDamage")) {
            int id = Integer.parseInt(s.split(" ")[0]);
            int damage = Integer.parseInt(s.split(" ")[1]);
            fixDamage.put(id, damage);
        }      
    }
    
    
}
