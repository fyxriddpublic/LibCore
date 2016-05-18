package com.fyxridd.lib.core.api.config.pipe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 管道接口
 * (管道用来进行数据转换)
 */
public interface Pipe<T extends Annotation>  {
    /**
     * @return 新值(如果未转换请返回旧值)
     */
    Object pipe(Class wrappedConfigClass, Field field, Object value, T annotation);
}
