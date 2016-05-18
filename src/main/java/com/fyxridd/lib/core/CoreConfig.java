package com.fyxridd.lib.core;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fyxridd.lib.core.api.config.basic.ListType;
import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.api.config.convert.ListConvert;
import com.fyxridd.lib.core.api.config.convert.ListConvert.ListConverter;
import com.fyxridd.lib.core.api.config.convert.PrimeConvert;
import com.fyxridd.lib.core.api.lang.LangConverter;
import com.fyxridd.lib.core.api.lang.LangGetter;

public class CoreConfig {
    private class FixDamageConverter implements ListConverter<Map<Integer, Integer>> {
        @Override
        public Map<Integer, Integer> convert(String plugin, List list) {
            Map<Integer, Integer> map = new HashMap<>();
            for (Object o:list) {
                String s = (String) o;
                String[] ss = s.split(" ");
                map.put(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
            }
            return map;
        }
    }

    private class DateFormatConverter implements PrimeConvert.PrimeConverter<String, SimpleDateFormat> {
        @Override
        public SimpleDateFormat convert(String plugin, String from) {
            return new SimpleDateFormat(from);
        }
    }

    //调试
    @Path("debug")
    private boolean debug;

    //总体语言配置
    @Path("langConfig.default")
    private String langConfigDefault;

    //语言
    @Path("lang")
    @ConfigConvert(LangConverter.class)
    private LangGetter lang;

    //日志
    @Path("log.dateFormat")
    @PrimeConvert(value = DateFormatConverter.class, primeType = PrimeConvert.PrimeType.String)
    private SimpleDateFormat logDateFormat;
    @Path("log.prefix")
    private String logPrefix;

    //伤害修正
    @Path("fixDamage")
    @ListConvert(value=FixDamageConverter.class, listType=ListType.String)
    private Map<Integer, Integer> fixDamage;

    //进入/走上方块类型
    @Path("enterBlockType.interval")
    private int enterBlockTypeInterval;

    //玩家真名
    private boolean realNameLimitEnable;

    public String getLangConfigDefault() {
        return langConfigDefault;
    }

    public int getEnterBlockTypeInterval() {
        return enterBlockTypeInterval;
    }

    public boolean isRealNameLimitEnable() {
        return realNameLimitEnable;
    }

    public Map<Integer, Integer> getFixDamage() {
        return fixDamage;
    }

    public LangGetter getLang() {
        return lang;
    }

    public boolean isDebug() {
        return debug;
    }

    public SimpleDateFormat getLogDateFormat() {
        return logDateFormat;
    }

    public String getLogPrefix() {
        return logPrefix;
    }
}
