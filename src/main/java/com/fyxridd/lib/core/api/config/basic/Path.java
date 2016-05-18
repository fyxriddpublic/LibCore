package com.fyxridd.lib.core.api.config.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 有@Path注解的类变量才会被解析设置值
 * 以下类型的变量可以被直接设置值:
 *   Boolean
 *   Byte
 *   Short
 *   Integer
 *   Long
 *   Float
 *   Double
 *   String
 *   List(需要使用ListType进行辅助)
 *
 *   放在类上时,内部的Field在被解析时,会被自动加上此路径前缀(连接时,中间会自动加一个.)
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    /**
     * 路径(以.分隔)
     */
    String value();
}
