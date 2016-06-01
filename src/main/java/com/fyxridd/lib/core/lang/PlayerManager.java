package com.fyxridd.lib.core.lang;

import com.fyxridd.lib.core.CoreConfig;
import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.PlayerApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerManager {
    private LangDao langDao;

    private CoreConfig coreConfig;

    //不完整,动态读取
    //玩家名 Lang用户(不为null)
    private Map<String, LangUser> users = new HashMap<>();

    //需要更新的用户列表
    private Set<LangUser> needUpdates = new HashSet<>();

    public PlayerManager() {
        langDao = new LangDao();
        //添加配置监听
        ConfigApi.addListener(CorePlugin.instance.pn, CoreConfig.class, new Setter<CoreConfig>() {
            @Override
            public void set(CoreConfig value) {
                coreConfig = value;
            }
        });

        //定时更新
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CorePlugin.instance, new Runnable() {
            @Override
            public void run() {
                update();
            }
        }, 237, 237);

        //注册事件
        {
            //插件停止
            Bukkit.getPluginManager().registerEvent(PluginDisableEvent.class, CorePlugin.instance, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    if (e instanceof PluginDisableEvent) {
                        PluginDisableEvent event = (PluginDisableEvent) e;
                        if (event.getPlugin().getName().equals(CorePlugin.instance.pn)) update();
                    }
                }
            }, CorePlugin.instance);
        }
    }

    /**
     * @see com.fyxridd.lib.core.api.lang.LangApi#getLang(String)
     */
    public String getLang(String player) {
        //玩家名修正
        player = PlayerApi.getRealName(null, player);
        if (player == null) return null;

        return init(player).getLang();
    }

    /**
     * @see com.fyxridd.lib.core.api.lang.LangApi#setLang(String, String)
     */
    public void setLang(String player, String lang) {
        //玩家名修正
        player = PlayerApi.getRealName(null, player);
        if (player == null) return;

        LangUser user = init(player);
        if (user.getLang().equals(lang)) return;

        user.setLang(lang);
        needUpdates.add(user);
    }

    /**
     * 获取Lang用户
     * @param name 准确的玩家名,不为null
     * @return 信息,不存在返回null
     */
    private LangUser init(String name) {
        //先从缓存中读取
        LangUser user = users.get(name);
        if (user != null) return user;

        //再从数据库中读取
        user = langDao.getLangUser(name);

        //新建
        if (user == null) {
            user = new LangUser(name, coreConfig.getLangConfigDefault());
            needUpdates.add(user);
        }

        //保存缓存
        users.put(name, user);

        return user;
    }

    private void update() {
        if (!needUpdates.isEmpty()) {
            langDao.saveOrUpdates(needUpdates);
            needUpdates.clear();
        }
    }
}
