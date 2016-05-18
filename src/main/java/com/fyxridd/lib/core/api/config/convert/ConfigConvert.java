package com.fyxridd.lib.core.api.config.convert;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用Configuration转换器
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigConvert {
    /**
     * 转换器
     * 实现类必须有个空的构造器
     */
    interface ConfigConverter<T> {
        T convert(String plugin, ConfigurationSection config) throws Exception;
    }

    /**
     * 转换器
     */
    Class<? extends ConfigConverter> value();
}
