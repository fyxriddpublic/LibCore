package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;

public class EcoApi {
    /**
     * 经济是否有效
     */
    public static boolean isEnable() {
        return CorePlugin.instance.getEcoManager().isEnable();
    }

    /**
     * 获取玩家的金钱
     */
    public static double get(String name) {
        return CorePlugin.instance.getEcoManager().get(name);
    }
    
    /**
     * 增加金币
     * @return 是否增加成功
     */
    public static boolean add(String name, double amount) {
        return CorePlugin.instance.getEcoManager().add(name, amount);
    }

    /**
     * 减少金币
     * @return 是否减少成功
     */
    public static boolean del(String name, double amount) {
        return CorePlugin.instance.getEcoManager().del(name, amount);
    }

    /**
     * 设置金币
     */
    public static boolean set(String name, double amount) {
        return CorePlugin.instance.getEcoManager().set(name, amount);
    }
}
