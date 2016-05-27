package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;

public class EcoApi {
    /**
     * 权限是否有效
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
     * 添加权限
     * @return 是否添加成功
     */
    public static boolean add(String name, double amount) {
        return CorePlugin.instance.getEcoManager().add(name, amount);
    }

    /**
     * 删除权限
     * @return 是否删除成功
     */
    public static boolean del(String name, double amount) {
        return CorePlugin.instance.getEcoManager().del(name, amount);
    }
}
