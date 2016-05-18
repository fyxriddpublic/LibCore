package com.fyxridd.lib.core.api.lang;

import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

/**
 * 语言获取器
 */
public interface LangGetter {
    /**
     * 获取语言
     * @param player 玩家名
     * @param id 语言ID
     * @param args 变量
     * @return 语言内容
     */
    FancyMessage get(String player, int id, Object... args);

    /**
     * 获取语言
     * @param id 语言ID
     * @param args 变量
     * @return 语言内容
     */
    FancyMessage get(int id, Object... args);

    /**
     * 获取语言
     * @param lang 语言
     * @param id 语言ID
     * @param args 变量
     * @return 语言内容
     */
    FancyMessage getLang(String lang, int id, Object... args);
}
