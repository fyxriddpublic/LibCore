package com.fyxridd.lib.core.config;

import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.config.AnalysisApi;
import com.fyxridd.lib.core.api.config.limit.CustomLimit;
import com.fyxridd.lib.core.api.config.limit.DoubleMax;
import com.fyxridd.lib.core.api.config.limit.DoubleMin;
import com.fyxridd.lib.core.api.config.limit.Limit;
import com.fyxridd.lib.core.api.config.limit.Max;
import com.fyxridd.lib.core.api.config.limit.MaxLength;
import com.fyxridd.lib.core.api.config.limit.Min;
import com.fyxridd.lib.core.api.config.limit.MinLength;
import com.fyxridd.lib.core.api.config.limit.Pattern;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 限制管理
 */
public class LimitManager {
    private Map<Class<? extends Annotation>, Limit<? extends Annotation>> limits = new HashMap<>();

    public LimitManager() {
        //注册限制

        //Max
        register(Max.class, new Limit<Max>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, Max max) throws Exception {
                if (Number.class.isAssignableFrom(wrappedConfigClass)) {
                    Number num = (Number) value;
                    if (num.longValue() > max.value()) throw new Exception("max value is: "+max.value());
                }
            }
        });

        //Min
        register(Min.class, new Limit<Min>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, Min min) throws Exception {
                if (Number.class.isAssignableFrom(wrappedConfigClass)) {
                    Number num = (Number) value;
                    if (num.longValue() < min.value()) throw new Exception("min value is: "+min.value());
                }
            }
        });

        //DoubleMax
        register(DoubleMax.class, new Limit<DoubleMax>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, DoubleMax doubleMax) throws Exception {
                if (Number.class.isAssignableFrom(wrappedConfigClass)) {
                    Number num = (Number) value;
                    if (num.doubleValue() > doubleMax.value()) throw new Exception("max value is: "+doubleMax.value());
                }
            }
        });

        //DoubleMin
        register(DoubleMin.class, new Limit<DoubleMin>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, DoubleMin doubleMin) throws Exception {
                if (Number.class.isAssignableFrom(wrappedConfigClass)) {
                    Number num = (Number) value;
                    if (num.doubleValue() < doubleMin.value()) throw new Exception("min value is: "+doubleMin.value());
                }
            }
        });

        //MaxLength
        register(MaxLength.class, new Limit<MaxLength>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, MaxLength maxLength) throws Exception {
                if (String.class.isAssignableFrom(wrappedConfigClass)) {
                    String s = (String) value;
                    if (s.length() > maxLength.value()) throw new Exception("max length is: "+maxLength.value());
                }
            }
        });

        //MinLength
        register(MinLength.class, new Limit<MinLength>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, MinLength minLength) throws Exception {
                if (String.class.isAssignableFrom(wrappedConfigClass)) {
                    String s = (String) value;
                    if (s.length() < minLength.value()) throw new Exception("min length is: "+minLength.value());
                }
            }
        });

        //Pattern
        register(Pattern.class, new Limit<Pattern>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, Pattern pattern) throws Exception {
                if (String.class.isAssignableFrom(wrappedConfigClass)) {
                    String s = (String) value;
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern.value());
                    if (!p.matcher(s).matches()) throw new Exception("must match pattern: "+pattern.value());
                }
            }
        });

        //CustomLimit
        register(CustomLimit.class, new Limit<CustomLimit>() {
            @Override
            public void limit(Class wrappedConfigClass, Field field, Object value, CustomLimit customLimit) throws Exception {
                CustomLimit.Limiter limiter = UtilApi.newInstance(customLimit.value());
                String errorMsg = limiter.limit(value);
                if (errorMsg != null) throw new Exception("limit check error: "+errorMsg);
            }
        });
    }

    /**
     * @see AnalysisApi#registerLimit(Class, Limit)
     */
    public <T extends Annotation> void register(Class<T> limitAnnotationClass, Limit<T> limit) {
        limits.put(limitAnnotationClass, limit);
    }

    /**
     * 检测限制
     */
    public void checkLimit(Class wrappedConfigClass, Field field, Object value, Annotation annotation) throws Exception {
        Limit limit = limits.get(annotation.getClass());
        if (limit != null) {
            try {
                limit.limit(wrappedConfigClass, field, value, annotation);
            } catch (Exception e) {
                throw new Exception("Limit "+annotation.getClass()+" check error: "+e.getMessage(), e);
            }
        }
    }
}
