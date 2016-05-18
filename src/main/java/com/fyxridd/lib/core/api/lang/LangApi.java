package com.fyxridd.lib.core.api.lang;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LangApi {
    public interface PlayerHandler {
        /**
         * 处理信息
         * @param p 玩家
         * @param msg 信息
         */
        void handle(Player p, FancyMessage msg);
    }

    public interface PlayerNameHandler {
        /**
         * 处理信息
         * @param name 玩家名
         * @param msg 信息
         */
        void handle(String name, FancyMessage msg);
    }

    /**
     * 获取玩家使用的语言
     * @return null表示没有选择语言(即使用默认语言)
     */
    public static String getLang(String player) {
        return CorePlugin.instance.getPlayerManager().getLang(player);
    }

    /**
     * 迭代玩家进行信息的处理
     * 对于使用相同语言的玩家,获取到的FancyMessage是相同的
     * (主要目的是用缓存来提高效率)
     * @param players 玩家列表
     * @param playerHandler 信息处理器(不要改变传入的FancyMessage,因为所有玩家使用的FancyMessage都是同一个对象)
     * @param id 语言ID
     * @param args 语言变量
     */
    public static void iteratePlayer(Collection<? extends Player> players, PlayerHandler playerHandler, int id, Object... args) {
        Map<String, FancyMessage> cache = new HashMap<>();
        for (Player p: players) {
            String lang = LangApi.getLang(p.getName());
            FancyMessage msg = cache.get(lang);
            if (msg == null) {
                msg = getLang(lang, id, args);
                cache.put(lang, msg);
            }
            playerHandler.handle(p, msg);
        }
    }

    /**
     * @see #iteratePlayer(Collection, PlayerHandler, int, Object...)
     * @param playerNames 玩家名列表
     */
    public static void iteratePlayerName(Collection<String> playerNames, PlayerNameHandler playerNameHandler, int id, Object... args) {
        Map<String, FancyMessage> cache = new HashMap<>();
        for (String name: playerNames) {
            String lang = LangApi.getLang(name);
            FancyMessage msg = cache.get(lang);
            if (msg == null) {
                msg = getLang(lang, id, args);
                cache.put(lang, msg);
            }
            playerNameHandler.handle(name, msg);
        }
    }

    private static FancyMessage getLang(String lang, int id, Object... args) {
        return CorePlugin.instance.getCoreConfig().getLang().getLang(lang, id, args);
    }
}
