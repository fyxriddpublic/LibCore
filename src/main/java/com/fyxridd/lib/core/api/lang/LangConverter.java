package com.fyxridd.lib.core.api.lang;

import org.bukkit.configuration.ConfigurationSection;

import com.fyxridd.lib.core.api.config.convert.ConfigConvert;
import com.fyxridd.lib.core.lang.LangGetterImpl;

/**
 * 语言转换器
 */
public class LangConverter implements ConfigConvert.ConfigConverter<LangGetter>{
    @Override
    public LangGetter convert(String plugin, ConfigurationSection config) throws Exception {
        return new LangGetterImpl(plugin, config);
    }
}
