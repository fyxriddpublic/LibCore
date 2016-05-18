package com.fyxridd.lib.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fyxridd.lib.core.api.config.basic.ListType;
import com.fyxridd.lib.core.api.config.basic.Path;
import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.api.config.convert.ListConvert;
import com.fyxridd.lib.core.api.config.convert.ListConvert.ListConverter;
import com.fyxridd.lib.core.api.lang.LangConverter;
import com.fyxridd.lib.core.api.lang.LangGetter;

public class CoreConfig {
    private class FixDamageConverter implements ListConverter<Map<Integer, Integer>> {
        @Override
        public Map<Integer, Integer> convert(String plugin, List list) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (Object o:list) {
                String s = (String) o;
                String[] ss = s.split(" ");
                map.put(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
            }
            return map;
        }
    }
    
    @Path("debug")
    private boolean debug;
    
    @Path("default")
    private String defaultLang;

    @Path("fixDamage")
    @ListConvert(value=FixDamageConverter.class, listType=ListType.String)
    private Map<Integer, Integer> fixDamage;
    
    private int enterBlockTypeInterval;

    private boolean realNameLimitEnable;

    @Path("lang")
    @ConfigConvert(LangConverter.class)
    private LangGetter lang;
    
    public String getDefaultLang() {
        return defaultLang;
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
}
