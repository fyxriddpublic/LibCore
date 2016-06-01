package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.ver.Ver;

public class VerApi {
    /**
     * 注册版本实现
     */
    public static <T extends Ver> void register(Class<T> c, T t) {
        CorePlugin.instance.getVerManager().register(c, t);
    }

    /**
     * 获取版本实现
     * @return 不存在返回null
     */
    public static <T extends Ver> T get(Class<T> c) {
        return CorePlugin.instance.getVerManager().get(c);
    }
}
