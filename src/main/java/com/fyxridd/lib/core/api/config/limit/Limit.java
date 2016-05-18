package com.fyxridd.lib.core.api.config.limit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 限制接口
 */
public interface Limit<T extends Annotation> {
    void limit(Class wrappedConfigClass, Field field, Object value, T annotation) throws Exception;
}
