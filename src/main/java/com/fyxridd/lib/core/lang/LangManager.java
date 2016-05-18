package com.fyxridd.lib.core.lang;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.show.chat.api.ShowApi;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

/**
 * 语言管理类
 */
public class LangManager implements Listener {
    public static FancyMessage load(String msg, ConfigurationSection config) throws Exception {
        if (CorePlugin.libChatShowHook) return ShowApi.load(msg, config);
        else return MessageApi.load(msg, config);
    }

    public static void convertArgs(FancyMessage msg, Object... args) {
        if (CorePlugin.libChatShowHook) ShowApi.convert(msg, args);
    }
}
