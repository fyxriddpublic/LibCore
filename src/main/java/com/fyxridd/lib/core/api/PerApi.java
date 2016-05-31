package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;

public class PerApi {
    /**
     * 检测玩家是否有权限
     */
    public static boolean has(String name, String per) {
        return CorePlugin.instance.getPerManager().has(name, per);
    }
    
    /**
     * 检测是否有权限,无权限时会提示
     */
    public static boolean checkHasPer(String name, String per) {
        return CorePlugin.instance.getPerManager().checkHasPer(name, per);
    }

    /**
     * 添加权限
     * @return 是否添加成功
     */
    public static boolean add(String name, String per) {
        return CorePlugin.instance.getPerManager().add(name, per);
    }

    /**
     * 删除权限
     * @return 是否删除成功
     */
    public static boolean del(String name, String per) {
        return CorePlugin.instance.getPerManager().del(name, per);
    }
}
