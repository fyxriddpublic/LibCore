package com.fyxridd.lib.core.api.config.convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import com.fyxridd.lib.core.api.config.basic.ListType;

/**
 * 使用List转换器
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListConvert {
    /**
     * 转换器
     * 实现类必须有个空的构造器,如果是内部类则必须是静态的
     */
    interface ListConverter<T> {
        T convert(String plugin, List list);
    }

    /**
     * 转换器
     */
    Class<? extends ListConverter> value();

    /**
     * 列表类型辅助
     */
    ListType listType();
}
