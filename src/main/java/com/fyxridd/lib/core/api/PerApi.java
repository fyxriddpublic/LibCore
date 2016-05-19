package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;

public class PerApi {
    /**
     * 权限是否有效
     */
    public static boolean isEnable() {
        return CorePlugin.instance.getPerManager().isEnable();
    }

    /**
     * 检测玩家是否有权限
     */
    public static boolean has(String name, String per) {
        return CorePlugin.instance.getPerManager().has(name, per);
    }
}
