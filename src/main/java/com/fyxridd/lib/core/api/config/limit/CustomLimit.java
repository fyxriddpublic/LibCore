package com.fyxridd.lib.core.api.config.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义限制
 * 对所有类型有效果
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomLimit {
    interface Limiter<T> {
        /**
         * @return 错误信息,null表示无错误
         */
        String limit(T value);
    }

    Class<? extends Limiter<?>> value();
}
