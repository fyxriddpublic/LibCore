package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.CoreConfig;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.PerApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EcoManager {
    private Economy eco;

    private CoreConfig config;

    public EcoManager() {
        //经济
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) eco = economyProvider.getProvider();
        //添加配置监听
        ConfigApi.addListener(CorePlugin.instance.pn, CoreConfig.class, new Setter<CoreConfig>() {
            @Override
            public void set(CoreConfig value) {
                config = value;
            }
        });
    }

    /**
     * @see PerApi#isEnable()
     */
    public boolean isEnable() {
        return eco != null;
    }

    /**
     * @see com.fyxridd.lib.core.api.EcoApi#get(String)
     */
    public double get(String name) {
        if (isEnable()) return eco.getBalance(config.getEcoDefaultWorld(), name);
        return 0;
    }

    /**
     * @see com.fyxridd.lib.core.api.EcoApi#add(String, double)
     */
    public boolean add(String name, double amount) {
        return isEnable() && eco.depositPlayer(config.getEcoDefaultWorld(), name, amount).transactionSuccess();
    }

    /**
     * @see com.fyxridd.lib.core.api.EcoApi#del(String, double)
     */
    public boolean del(String name, double amount) {
        return isEnable() && eco.withdrawPlayer(config.getEcoDefaultWorld(), name, amount).transactionSuccess();
    }

    /**
     * @see com.fyxridd.lib.core.api.EcoApi#set(String, double)
     */
    public boolean set(String name, double amount) {
        if (!isEnable()) return false;

        double has = get(name);
        if (amount == has) return true;
        else if (amount > has) return add(name, amount-has);
        else return del(name, has-amount);
    }
}
