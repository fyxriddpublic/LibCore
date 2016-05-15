package com.fyxridd.lib.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.fyxridd.lib.config.api.ConfigApi;
import com.fyxridd.lib.core.api.CoreApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.core.api.event.ServerCloseEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.core.config.LangConfig;
import com.fyxridd.lib.core.manager.SyncChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CorePlugin extends SimplePlugin{
    public static CorePlugin instance;
    private ProtocolManager protocolManager;

    private LangConfig langConfig;

    private CoreManager coreManager;
    private SyncChatManager syncChatManager;

    @Override
    public void onLoad() {
        CoreApi.serverPath = System.getProperty("user.dir");
        CoreApi.pluginPath = getFile().getParentFile().getAbsolutePath();
        CoreApi.serverVer = CoreApi.getMcVersion(Bukkit.getServer());

        protocolManager = ProtocolLibrary.getProtocolManager();

        super.onLoad();
    }

    //启动插件
    @Override
    public void onEnable() {
        instance = this;

        //注册配置对象
        com.fyxridd.lib.config.api.ConfigApi.register(pn, LangConfig.class);
        //添加配置监听
        ConfigApi.addListener(pn, LangConfig.class, new com.fyxridd.lib.config.manager.ConfigManager.Setter<LangConfig>() {
            @Override
            public void set(LangConfig value) {
                langConfig = value;
            }
        });

        //初始化
        coreManager = new CoreManager();
        syncChatManager = new SyncChatManager();

        super.onEnable();
    }

    //停止插件
    @Override
    public void onDisable() {
        //计时器
        Bukkit.getScheduler().cancelAllTasks();

        super.onDisable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("stop")) {
            if (sender instanceof Player && !sender.isOp()) return true;
            //先T人
            for (Player p:Bukkit.getOnlinePlayers()) p.kickPlayer(get(p.getName(), 70).getText());
            //发出关服事件
            Bukkit.getPluginManager().callEvent(new ServerCloseEvent());
            //再关服
            Bukkit.shutdown();
        }
        return true;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public LangConfig getLangConfig() {
        return langConfig;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public SyncChatManager getSyncChatManager() {
        return syncChatManager;
    }

    private static FancyMessage get(String player, int id, Object... args) {
        return CorePlugin.instance.getLangConfig().getLang().get(player, id, args);
    }
}
