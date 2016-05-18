package com.fyxridd.lib.core.api.log;

import com.fyxridd.lib.core.CorePlugin;

public class LogApi {
    /**
     * 注册日志上下文
     * @param type 日志类型
     */
    public static void register(String type) {
        CorePlugin.instance.getLogManager().register(type);
    }
    
    /**
     * 记录日志
     * @param type 日志类型
     * @param level 日志等级
     * @param msg 日志信息
     */
    public static void log(String type, Level level, Object msg) {
        CorePlugin.instance.getLogManager().log(type, level, msg);
    }
}
