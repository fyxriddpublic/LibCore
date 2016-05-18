package com.fyxridd.lib.core.realname;

import com.fyxridd.lib.core.CorePlugin;
import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.event.FirstJoinEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;
import java.util.Map;

public class RealNameManager {
    private Dao dao;

    //不完整,动态读取
	//玩家名(小写) 玩家真名
	private Map<String, RealName> realNames = new HashMap<>();

	public RealNameManager() {
        //与数据库连接层
        dao = new Dao();

        //注册事件
        {
            //玩家登录
            Bukkit.getPluginManager().registerEvent(PlayerLoginEvent.class, CorePlugin.instance, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    PlayerLoginEvent event = (PlayerLoginEvent) e;
                    //已经禁止了
                    if (!event.getResult().equals(Result.ALLOWED)) return;
                    //限制启动
                    if (CorePlugin.instance.getCoreConfig().isRealNameLimitEnable()) {
                        //检测真名,禁止非法进入
                        try {
                            String realName = getRealName(null, event.getPlayer().getName());
                            if (realName != null && !realName.equals(event.getPlayer().getName())) {
                                event.setResult(Result.KICK_OTHER);
                                event.setKickMessage(get(event.getPlayer().getName(), 905, event.getPlayer().getName(), realName).getText());
                            }
                        } catch (NotReadyException e1) {
                            event.setResult(Result.KICK_OTHER);
                            event.setKickMessage(get(event.getPlayer().getName(), 910).getText());
                        }
                    }
                }
            }, CorePlugin.instance);

            //玩家加入
            Bukkit.getPluginManager().registerEvent(PlayerJoinEvent.class, CorePlugin.instance, EventPriority.LOWEST, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    try {
                        PlayerJoinEvent event = (PlayerJoinEvent) e;
                        //获取用户信息
                        RealName user = get(event.getPlayer().getName());

                        //用户无信息
                        if (user == null) {
                            //新建
                            user = new RealName(event.getPlayer().getName());
                            //缓存
                            realNames.put(event.getPlayer().getName().toLowerCase(), user);
                            //db
                            dao.insert(user);
                            //发出第一次进服事件
                            Bukkit.getPluginManager().callEvent(new FirstJoinEvent(event.getPlayer()));
                        }
                    } catch (NotReadyException e1) {//不应该发生
                        e1.printStackTrace();
                    }
                }
            }, CorePlugin.instance);
        }
	}

    /**
     * @see com.fyxridd.lib.core.api.PlayerApi#getRealName(CommandSender, String)
     */
    public String getRealName(CommandSender sender, String name) throws NotReadyException{
        //获取信息
        RealName user = get(name);

        //存在,返回真名
        if (user != null) return user.getName();

        //目标玩家不存在,检测提示
        if (sender != null) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                MessageApi.send(p, get(p.getName(), 900, name), true);
            } else sender.sendMessage(get(null, 900, name).getText());
        }
        return null;
    }

    /**
     * 获取玩家信息
     * @param name 玩家名,不为null
     * @return 信息,不存在返回null
     */
    private RealName get(String name) throws NotReadyException{
        //先从缓存中读取
        RealName result = realNames.get(name.toLowerCase());
        if (result != null) return result;

        //再从数据库中读取
        result = dao.getRealName(name);
        //保存缓存
        if (result != null) realNames.put(name.toLowerCase(), result);

        return result;
    }

    private static FancyMessage get(String player, int id, Object... args) {
        return CorePlugin.instance.getCoreConfig().getLang().get(player, id, args);
    }
}
