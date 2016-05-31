package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CoreConfig;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.PerApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PerManager {
    private Permission per;

    private CoreConfig config;

    public PerManager() {
        //权限
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) per = permissionProvider.getProvider();
        //添加配置监听
        ConfigApi.addListener(CorePlugin.instance.pn, CoreConfig.class, new Setter<CoreConfig>() {
            @Override
            public void set(CoreConfig value) {
                config = value;
            }
        });
    }

    /**
     * @see PerApi#has(String, String)
     */
    public boolean has(String name, String per) {
        return this.per.has(config.getPermissionDefaultWorld(), name, per);
    }

    /**
     * @see PerApi#checkHasPer(String, String)
     */
    public boolean checkHasPer(String name, String per) {
        if (!has(name, per)) {
            MessageApi.send(name, get(name, 10, per), true);
            return false;
        }
        return true;
    }

    /**
     * @see PerApi#add(String, String)
     */
    public boolean add(String name, String per) {
        return this.per.playerAdd(config.getPermissionDefaultWorld(), name, per);
    }

    /**
     * @see PerApi#del(String, String)
     */
    public boolean del(String name, String per) {
        return this.per.playerRemove(config.getPermissionDefaultWorld(), name, per);
    }

    private FancyMessage get(String player, int id, Object... args) {
        return config.getLang().get(player, id, args);
    }
}
