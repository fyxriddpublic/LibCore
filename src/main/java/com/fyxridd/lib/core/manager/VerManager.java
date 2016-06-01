package com.fyxridd.lib.core.manager;

import com.fyxridd.lib.core.api.ver.Ver;

import java.util.HashMap;
import java.util.Map;

/**
 * 版本控制管理
 */
public class VerManager {
    //类 实例
    private Map<Class<? extends Ver>, Ver> vers = new HashMap<>();

    /**
     * @see com.fyxridd.lib.core.api.VerApi#register(Class, Ver)
     */
    public <T extends Ver> void register(Class<T> c, T t) {
        vers.put(c, t);
    }

    /**
     * @see com.fyxridd.lib.core.api.VerApi#get(Class)
     */
    public <T extends Ver> T get(Class<T> c) {
        return (T) vers.get(c);
    }
}
