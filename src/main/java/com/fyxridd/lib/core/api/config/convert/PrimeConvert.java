package com.fyxridd.lib.core.api.config.convert;

import org.bukkit.configuration.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用Prime转换器
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimeConvert {
    enum PrimeType {
        Boolean,
        Byte,
        Short,
        Integer,
        Long,
        Float,
        Double,
        String,
        Object
        ;

        /**
         * 获取类型对应的值
         */
        public Object getValue(Configuration config, String path) {
            if (this == PrimeType.Boolean) return config.getBoolean(path);
            else if (this == PrimeType.Byte) return (byte) config.getInt(path);
            else if (this == PrimeType.Short) return (short) config.getInt(path);
            else if (this == PrimeType.Integer) return config.getInt(path);
            else if (this == PrimeType.Long) return config.getLong(path);
            else if (this == PrimeType.Float) return (float) config.getDouble(path);
            else if (this == PrimeType.Double) return config.getDouble(path);
            else if (this == PrimeType.String) return config.getString(path);
            else return config.get(path);//其它只有Object
        }
    }

    /**
     * 转换器
     * 实现类必须有个空的构造器
     */
    interface PrimeConverter<F, T> {
        T convert(String plugin, F from);
    }

    /**
     * 转换器
     */
    Class<? extends PrimeConverter> value();

    /**
     * 基本类型辅助
     */
    PrimeType primeType();
}
