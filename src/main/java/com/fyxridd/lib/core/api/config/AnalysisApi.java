package com.fyxridd.lib.core.api.config;

import java.lang.annotation.Annotation;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.config.limit.Limit;
import com.fyxridd.lib.core.api.config.pipe.Pipe;

/**
 * 配置分析Api
 */
public class AnalysisApi {
    /**
     * 注册限制
     */
    public static <T extends Annotation> void registerLimit(Class<T> limitAnnotationClass, Limit<T> limit) {
        CorePlugin.instance.getLimitManager().register(limitAnnotationClass, limit);
    }

    /**
     * 注册管道(用来转换数据)
     */
    public static <T extends Annotation> void registerPipe(Class<T> pipeAnnotationClass, Pipe<T> pipe) {
        CorePlugin.instance.getPipeManager().register(pipeAnnotationClass, pipe);
    }
}
