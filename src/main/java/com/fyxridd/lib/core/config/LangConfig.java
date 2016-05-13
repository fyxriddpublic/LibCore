package com.fyxridd.lib.core.config;

import com.fyxridd.lib.config.api.basic.Path;
import com.fyxridd.lib.config.api.convert.ConfigConvert;
import com.fyxridd.lib.lang.api.LangConverter;
import com.fyxridd.lib.lang.api.LangGetter;

public class LangConfig {
    @Path("lang")
    @ConfigConvert(LangConverter.class)
    LangGetter lang;

    public LangGetter getLang() {
        return lang;
    }
}
