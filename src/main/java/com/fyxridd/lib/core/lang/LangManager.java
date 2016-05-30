package com.fyxridd.lib.core.lang;

import com.fyxridd.lib.core.api.lang.LangGetter;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

/**
 * 语言管理类
 */
public class LangManager implements Listener {
    //所有插件的语言缓存
    //插件名 语言获取器
    private Map<String, LangGetter> langs = new HashMap<>();

    public void onLangLoad(String plugin, LangGetter lang) {
        langs.put(plugin, lang);
    }

    /**
     * @see com.fyxridd.lib.core.api.lang.LangApi#getPluginLang(String)
     */
    public LangGetter getPluginLang(String plugin) {
        return langs.get(plugin);
    }
}
