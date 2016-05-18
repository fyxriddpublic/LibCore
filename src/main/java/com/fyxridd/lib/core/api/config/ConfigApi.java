package com.fyxridd.lib.core.api.config;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.config.ConfigManager.Setter;

/**
 * 读取配置Api
 */
public class ConfigApi {
    /**
     * 注册
     * (注册后,会马上自动进行读取一次)
     * @param plugin 插件名
     * @param configClass 配置对象类(必须有一个空的构造器)(如果是内部类则必须是静态的)
     */
    public static <T> void register(String plugin, Class<T> configClass) {
        CorePlugin.instance.getConfigManager().register(plugin, configClass);
    }

    /**
     * 监听
     * (监听后,会马上自动进行设置一次)
     * @param plugin 插件名
     * @param configClass 配置对象类(必须有一个空的构造器)(如果是内部类则必须是静态的)
     */
    public static <T> void addListener(String plugin, Class<T> configClass, Setter<T> setter) {
        CorePlugin.instance.getConfigManager().addListener(plugin, configClass, setter);
    }

    /**
     * 重载插件内所有的配置上下文
     * @param plugin 插件名
     */
    public static void reload(String plugin) {
        CorePlugin.instance.getConfigManager().reload(plugin);
    }

    /**
     * 重载插件指定的配置上下文
     */
    public static void reload(String plugin, Class<?> configClass) {
        CorePlugin.instance.getConfigManager().reload(plugin, configClass);
    }

    /**
     * 获取内存中保存的配置对象
     * @param plugin 插件名
     * @param configClass 配置类
     * @return 异常返回null
     */
    public static <T> T get(String plugin, Class<T> configClass) {
        return CorePlugin.instance.getConfigManager().get(plugin, configClass);
    }
}
