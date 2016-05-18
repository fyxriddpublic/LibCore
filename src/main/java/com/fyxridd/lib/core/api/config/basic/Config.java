package com.fyxridd.lib.core.api.config.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    String DEFAULT_CONFIG_FILE_NAME = "config.yml";
    
    /**
     * 从哪个配置文件里读取
     * 相对路径,相对于此配置所属插件的目录
     */
    String value() default DEFAULT_CONFIG_FILE_NAME;
}
