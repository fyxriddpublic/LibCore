package com.fyxridd.lib.core.config;

import com.fyxridd.lib.core.api.config.AnalysisApi;
import com.fyxridd.lib.core.api.config.pipe.Color;
import com.fyxridd.lib.core.api.config.pipe.LowerCase;
import com.fyxridd.lib.core.api.config.pipe.Pipe;
import com.fyxridd.lib.core.api.config.pipe.UpperCase;
import com.fyxridd.lib.core.api.UtilApi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PipeManager {
    private Map<Class<? extends Annotation>, Pipe<? extends Annotation>> pipes = new HashMap<>();

    public PipeManager() {
        //注册管道

        //Color
        AnalysisApi.registerPipe(Color.class, new Pipe<Color>() {
            @Override
            public Object pipe(Class wrappedConfigClass, Field field, Object value, Color color) {
                if (String.class.isAssignableFrom(wrappedConfigClass)) return UtilApi.convert((String) value);
                return value;
            }
        });

        //UpperCase
        AnalysisApi.registerPipe(UpperCase.class, new Pipe<UpperCase>() {
            @Override
            public Object pipe(Class wrappedConfigClass, Field field, Object value, UpperCase upperCase) {
                if (String.class.isAssignableFrom(wrappedConfigClass)) return ((String) value).toUpperCase();
                return value;
            }
        });

        //LowerCase
        AnalysisApi.registerPipe(LowerCase.class, new Pipe<LowerCase>() {
            @Override
            public Object pipe(Class wrappedConfigClass, Field field, Object value, LowerCase lowerCase) {
                if (String.class.isAssignableFrom(wrappedConfigClass)) return ((String) value).toLowerCase();
                return value;
            }
        });
    }

    public <T extends Annotation> void register(Class<T> pipeAnnotationClass, Pipe<T> pipe) {
        pipes.put(pipeAnnotationClass, pipe);
    }

    /**
     * 检测限制
     */
    public Object checkPipe(Class wrappedConfigClass, Field field, Object value, Annotation annotation) throws Exception {
        Pipe pipe = pipes.get(annotation.getClass());
        if (pipe != null) return pipe.pipe(wrappedConfigClass, field, value, annotation);
        return value;
    }
}
