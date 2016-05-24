package com.fyxridd.lib.core.api.config;

/**
 * 设置器
 */
public interface Setter<T> {
    /**
     * 在值需要更新时调用(需要注意的是,可能会被额外多次调用)
     * @param t 新值
     */
    void set(T t);
}
